package com.danan.thread.example1;

public class Main {
    public static void main( String[] args ) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run () {
                System.out.println("We are now in thread: " + Thread.currentThread().getName());
                System.out.println("Current Priority: " + Thread.currentThread().getPriority());
                throw new RuntimeException("Intentional Exception");
            }
        });

        thread.setName("Alfiau");

        thread.setPriority(Thread.MAX_PRIORITY);

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("A critical Error happended in : " 
                + Thread.currentThread().getName()
                + " .The error is: "
                + e.getMessage());
            }
        });

        System.out.println("We are in thread: " + Thread.currentThread().getName()+ " before starting a new Thread");
        thread.start();
        System.out.println("We are in thread: " + Thread.currentThread().getName()+ " after starting a new Thread");

        Thread.sleep(10000);
    }
}
