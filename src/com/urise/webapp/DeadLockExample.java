package com.urise.webapp;

public class DeadLockExample {

    static String lock1 = "lock1";
    static String lock2 = "lock2";

    static void deadLock(String lock1, String lock2) {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " is waiting for " + lock1);
            synchronized (lock1) {
                System.out.println(Thread.currentThread().getName() + " holds " + lock1);
                System.out.println(Thread.currentThread().getName() + " is waiting for " + lock2);
                synchronized (lock2) {
                    System.out.println(Thread.currentThread().getName() + " holds " + lock2);
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        deadLock(lock1, lock2);
        deadLock(lock2, lock1);
    }

}
