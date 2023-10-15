package dev.services.generator;


import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {

    ReentrantLock lock = new ReentrantLock();
    int count = 0;

    private static IdGenerator instance;

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }


    public int getAndIncrement(){

        lock.lock();

        try{
            return count++;
        } finally {
            lock.unlock();
        }

    }



}
