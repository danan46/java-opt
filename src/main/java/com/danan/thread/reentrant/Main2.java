package com.danan.thread.reentrant;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main2 {
    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws Exception {
        InvetoryDatabase invetoryDatabase = new InvetoryDatabase();
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            invetoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writerThread = new Thread(() -> {
            while (true) {
                try {
                    invetoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                    invetoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
        });

        writerThread.setDaemon(true);
        writerThread.start();

        int numberOfReaderThreads = 7;
        List<Thread> threads = new ArrayList<>();

        for (int readerIndex = 0; readerIndex < numberOfReaderThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    try {
                        invetoryDatabase.getNumberOfItemsPriceRange(lowerBoundPrice, upperBoundPrice);
                    } catch (Exception e) {
                    }
                }
            });

            reader.setDaemon(true);
            threads.add(reader);
        }

        long startReadingThread = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long stopReadingThread = System.currentTimeMillis();
        System.out.println(String.format("Time Reading was %d ms", stopReadingThread - startReadingThread));
        System.out.println(String.format("Number of readers %d", invetoryDatabase.getNumberOfReaders()));
        System.out.println(String.format("Number of writers %d", invetoryDatabase.getNumberOfWriters()));
    }

    public static class InvetoryDatabase {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private int numberOfReaders = 0;
        private int numberOfWriters = 0;

        private ReentrantLock lock = new ReentrantLock();
        // private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
        // private Lock readLock = rwlock.readLock();
        // private Lock writeLock = rwlock.writeLock();

        public int getNumberOfItemsPriceRange(int lowerBound, int upperBound) throws Exception {
            lock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                NavigableMap<Integer, Integer> rangedOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;
                for (int numberOfItemsForPrice : rangedOfPrices.values()) {
                    sum += numberOfItemsForPrice;
                }

                return sum;
            } finally {
                numberOfReaders++;
                lock.unlock();
            }
        }

        public void addItem(int price) throws Exception {
            lock.lock();
            try {
                Integer numberOfItems = priceToCountMap.get(price);
                if (numberOfItems != null) {
                    priceToCountMap.put(price, numberOfItems + 1);
                } else {
                    priceToCountMap.put(price, 1);
                }
            } finally {
                numberOfWriters++;
                lock.unlock();
            }
        }

        public void removeItem(int price) throws Exception {
            lock.lock();
            try {
                Integer numberOfItems = priceToCountMap.get(price);
                if (numberOfItems == null) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numberOfItems - 1);
                }
            } finally {
                numberOfWriters++;
                lock.unlock();
            }
        }

        public int getNumberOfReaders() {
            return numberOfReaders;
        }

        public void setNumberOfReaders(int numberOfReaders) {
            this.numberOfReaders = numberOfReaders;
        }

        public int getNumberOfWriters() {
            return numberOfWriters;
        }

        public void setNumberOfWriters(int numberOfWriters) {
            this.numberOfWriters = numberOfWriters;
        }

    }
}
