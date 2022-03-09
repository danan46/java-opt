package com.danan.critical;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();
        Thread blogic1 = new BusinessLogic(metrics);
        Thread blogic2 = new BusinessLogic(metrics);
        Thread dashboard = new MetricsPrinter(metrics);

        blogic1.start();
        blogic2.start();
        dashboard.start();
    }

    public static class MetricsPrinter extends Thread {
        private Metrics metrics;
        
        MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                double average = metrics.getAverage();
                System.out.println("Current Average is: " + average);
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                long start = System.currentTimeMillis();
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                long end = System.currentTimeMillis();
                metrics.insertSample(end - start);
            }
        }
    }

    public static class Metrics {
        private long count = 0;
        private double average = 0.0;

        public synchronized void insertSample(long sample) {
            double actualSum = count * average;
            count++;
            average = (actualSum + sample) / count;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        
    }
}
