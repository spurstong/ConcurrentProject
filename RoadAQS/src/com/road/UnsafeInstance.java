package com.road;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeInstance {
    //通过反射方法获取Unsafe实例
    public static Unsafe reflectUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe)field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
