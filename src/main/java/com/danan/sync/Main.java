package com.danan.sync;

/* This class handle MAX_ITER where run 1 increment Thread and 1 decrement Thread with 
safe operation (Synchronized) in the ++ an -- method */
public class Main {

    protected static final int MAX_ITER = 100000;

    public static void main(String[] args) throws InterruptedException {
        InventoryCount inventoryCount = new InventoryCount();

        Thread incrementThread = new IncrementThread(inventoryCount);
        Thread decrementThread = new DecrementThread(inventoryCount);

        incrementThread.start();
        decrementThread.start();

        incrementThread.join();
        decrementThread.join();

        System.out.println("The size is " + inventoryCount.getInventoryCount());
    }

    public static class InventoryCount {
        private int counter;

        public int getInventoryCount() {
            return this.counter;
        }

        public synchronized void incrementCounter() {
            this.counter++;
        }

        public synchronized void decrementCounter() {
            this.counter--;
        }
    }

    public static class IncrementThread extends Thread {
        InventoryCount inventoryCount;

        IncrementThread(InventoryCount inventoryCount) {
            this.inventoryCount = inventoryCount;
        }

        @Override
        public void run() {
            for(int i=0; i<Main.MAX_ITER;i++){
                this.inventoryCount.incrementCounter();
            }
        }
    }

    public static class DecrementThread extends Thread {
        InventoryCount inventoryCount;

        DecrementThread(InventoryCount inventoryCount) {
            this.inventoryCount = inventoryCount;
        }

        @Override
        public void run() {
            for(int i=0; i<Main.MAX_ITER;i++){
                this.inventoryCount.decrementCounter();
            }
        }
    }   
}
