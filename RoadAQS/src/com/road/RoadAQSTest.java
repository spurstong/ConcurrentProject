package com.road;

public class RoadAQSTest {
    public static void main(String[] args) {
        Goods goods = new Goods();
        for(int i=0; i<100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    goods.reduceCount();
                }
            }, "Thread-" + i + "------").start();
        }
    }

    private static class Goods{
        private int count = 10;
        private RoadAQS lock = new RoadAQS();
        public void reduceCount() {

            lock.lock();

            if (count > 0) {
                System.out.println("线程" + lock.getLockHolder() + " 获取第 " + count + "件商品");
                count--;
            } else {
                System.out.println("商品已卖完！");
            }
            lock.unlock();
        }
    }
}
