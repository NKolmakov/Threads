package by.grodno.entities;

import by.grodno.Main;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Port {
    private Lock lock = new ReentrantLock();
    //necessary to know common docks amount
    private int dockAmount;

    private List<Dock> availableDocks;
    private Queue<Ship> shipQueue;
    private Storage storage;

    public Port(List<Dock> docks, int shipQueueCapacity, Storage storage) {
        this.availableDocks = docks;
        this.dockAmount = docks.size();
        this.shipQueue = new ArrayBlockingQueue<Ship>(shipQueueCapacity);
        this.storage = storage;
    }

    public List<Dock> getAvailableDocks() {
        return availableDocks;
    }

    public void reserveDock(Dock dock) {
        availableDocks.remove(dock);
    }

    public void removeReservation(Dock dock) {
        availableDocks.add(dock);
    }

    public Queue<Ship> getShipQueue() {
        return shipQueue;
    }

    public boolean addShip2Queue(Ship ship) {
        return shipQueue.offer(ship);
    }

    public Ship getShipFromQueue() {
        return shipQueue.poll();
    }

    public Storage getStorage() {
        return storage;
    }

    public int getDockAmount() {
        return dockAmount;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
