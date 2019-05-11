package by.grodno.entities;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Dock {
    private Lock lock = new ReentrantLock();
    private int dockId;
    private boolean isFree;
    private Ship ship;

    public Dock() {
        isFree = true;
    }

    public int getDockId() {
        return dockId;
    }

    public Ship getProviderShip() {
        return ship;
    }

    public void setDockId(int dockId) {
        this.dockId = dockId;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
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

    public Ship getShip() {
        return ship;
    }
}
