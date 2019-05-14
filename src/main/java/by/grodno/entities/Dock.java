package by.grodno.entities;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dock {
    private Lock lock = new ReentrantLock();
    private int dockId;
    private boolean isFree;

    public Dock(int dockId) {
        this.dockId = dockId;
        isFree = true;
    }

    public int getDockId() {
        return dockId;
    }

    public boolean tryLockTheDock() {
        return lock.tryLock();
    }

    public void unlockTheDock() {
        lock.unlock();
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }
}
