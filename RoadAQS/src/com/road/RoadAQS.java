package com.road;

import sun.misc.Lock;
import sun.misc.Unsafe;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

//手写一个同步器
public class RoadAQS {
    //当前锁的状态，1表示加锁，0表示未加锁
    private volatile int state = 0;
    private final static Unsafe unsafe = UnsafeInstance.reflectUnsafe();
    //state在内存中的偏移量
    private final static long stateOffset;
    //当前持有锁的线程
    private Thread lockHoder;
    //是一个线程安全的队列，记录等待获取锁的线程
    private ConcurrentLinkedQueue<Thread> waiters = new ConcurrentLinkedQueue<>();

    static {
        try {
            stateOffset = unsafe.objectFieldOffset(RoadAQS.class.getDeclaredField("state"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public int getState() {
        return state;
    }

    public Thread getLockHolder() {
        return lockHoder;
    }
    public void setLockHoder(Thread lockHoder) {
        this.lockHoder = lockHoder;
    }

    public boolean compareAndSwapInt(int expect, int upadte) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, upadte);
    }

    public boolean acquire() {
        Thread t = Thread.currentThread();
        if ((waiters.size() == 0 || t == waiters.peek()) && compareAndSwapInt(0, 1)) {
            setLockHoder(t);
            return true;
        }
        return false;
    }

    public void lock() {
        if(acquire()){
            return;
        }
        Thread current = Thread.currentThread();
        waiters.add(current);
        for(;;) {
            if(current == waiters.peek() && acquire()) {
                waiters.poll();
                return;
            }
            LockSupport.park();
        }
    }

    public void unlock() {
        if (Thread.currentThread() != getLockHolder()) {
            throw new RuntimeException("lockHolder is not current Thread");
        }
        int state = getState();
        if (compareAndSwapInt(state, 0)) {
            setLockHoder(null);
            Thread t = waiters.peek();
            if (t != null) {
                LockSupport.unpark(t);
            }
        }
    }

}
