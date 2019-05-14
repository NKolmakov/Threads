package by.grodno.entities;

import by.grodno.Main;
import by.grodno.managers.Employee;
import com.sun.org.apache.xpath.internal.operations.String;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Port {
    private Lock lock = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();

    //necessary to know common docks amount
    private int dockAmount;

    //queue of ships which reached port and waiting for unload
    private Queue<Ship> waitingUnloadQueue;

    private Queue<Dock> availableDocks;

    //queue of ships which didn't reach port and waiting for available dock
    private Queue<Ship> waitingDockQueue;
    private ScheduledExecutorService time2Wait;
    private Storage storage;

    public Port(Queue<Dock> docks, int shipQueueCapacity, Storage storage) {
        this.availableDocks = docks;
        this.dockAmount = docks.size();
        this.waitingDockQueue = new ArrayBlockingQueue<Ship>(shipQueueCapacity);
        this.waitingUnloadQueue = new ArrayBlockingQueue<Ship>(shipQueueCapacity);
        this.time2Wait = Executors.newSingleThreadScheduledExecutor();
        this.storage = storage;
    }

    public Queue<Dock> getAvailableDocks() {
        return availableDocks;
    }

    public void reserveDock(Dock dock) {
        availableDocks.remove(dock);
    }

    public void removeReservation(Dock dock) {
        availableDocks.add(dock);
    }

    public Queue<Ship> getWaitingDockQueue() {
        return waitingDockQueue;
    }

    public boolean addShip2Queue(Ship ship) {
        return waitingDockQueue.offer(ship);
    }

    public Ship getShipFromQueue() {
        return waitingDockQueue.poll();
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

    public void setWaitingUnloadQueue(Queue<Ship> waitingUnloadQueue) {
        this.waitingUnloadQueue = waitingUnloadQueue;
    }

    /**
     * <p>Add a ship that arrived to port and waiting to start exchange with other ships.
     * When time will be over ship try to exchange with storage and leave</p>
     *
     * @param ship ship that waiting exchanging
     */
    public void add2WaitingUnloadQueue(Ship ship) {

        time2Wait.schedule(() -> startExchangeWithStorage(ship), 10 + new Random().nextInt(5), TimeUnit.SECONDS);
        this.waitingUnloadQueue.add(ship);
        Main.LOGGER.info("Ship #" + ship.getShipId() + " started wait exchanging");
    }

    private void startExchangeWithStorage(Ship ship) {

        //if time is over start exchange with storage
        if (waitingUnloadQueue.contains(ship)) {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " waiting enough and start exchange with storage ");
            Employee employee = new Employee(ship, storage);
            employee.start();
            try {
                employee.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //after exchanging leave port
            Dock dock = ship.leaveDock();
            removeReservation(dock);
            waitingUnloadQueue.remove(ship);
        }
    }

    public Ship getWaiting2UnloadShip(ContainerTypes requiredContainersType) {
        Ship foundShip = null;
        for (Ship ship : waitingUnloadQueue) {
            if ((ship.getRequiredTypeOfContainers()).toString().equalsIgnoreCase(requiredContainersType.toString())) {
                foundShip = ship;
                waitingUnloadQueue.remove(ship);
                return foundShip;
            }
        }
        return foundShip;
    }

    public ScheduledExecutorService getTime2Wait() {
        return time2Wait;
    }

    public Queue<Ship> getWaitingUnloadQueue() {
        return waitingUnloadQueue;
    }
}
