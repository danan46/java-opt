package com.danan.thread.example2.interrupt;

import java.math.BigInteger;

/* Thie example set a Thread as a Daemon in order to continue the elaboration even if the main is finished */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("20000"), new BigInteger("1000000000000")));
        thread.setDaemon(true);
        thread.start();
        thread.sleep(1000);
        thread.interrupt();
    }

    private static class LongComputationTask implements Runnable {
        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base+"^"+power+" = " + pow(base, power));
        }

        public BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;

            for(BigInteger i=BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if(Thread.currentThread().isInterrupted()){
                    System.err.println("System Interrupt");
                    return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }

            return result;
        }

        
    }
}
