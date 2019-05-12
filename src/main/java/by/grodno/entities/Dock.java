package by.grodno.entities;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Dock {
    private Lock lock = new ReentrantLock();
    private int dockId;
    private boolean isFree;

    public Dock() {
        isFree = true;
    }

    public int getDockId() {
        return dockId;
    }

    public void setDockId(int dockId) {
        this.dockId = dockId;
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
