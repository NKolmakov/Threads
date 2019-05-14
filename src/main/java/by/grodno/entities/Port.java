package by.grodno.entities;

import by.grodno.Main;
import by.grodno.managers.Employee;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private Lock lock = new ReentrantLock();

    //necessary to know common docks amount
    private int dockAmount;

    //queue of ships which reached port and waiting for unload
    private Queue<Ship> waitingUnloadQueue;

    //this queue helps to get faster available dock form whole docks list
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
        this.time2Wait = Executors.newScheduledThreadPool(shipQueueCapacity);
        this.storage = storage;
    }

    /**
     * <p>Method to get queue of available docks</p>
     * @return queue of available docks
     */
    public Queue<Dock> getAvailableDocks() {
        return availableDocks;
    }

    /**
     *<p>Removes one dock from available docks queue. Other ships can't see this dock if it was reserved</p>
     * @param dock - is dock will be reserved
     */
    public void reserveDock(Dock dock) {
        availableDocks.remove(dock);
    }

    /**
     * <p>Method adds a dock to queue of available docks. Use this method when ship leave port</p>
     * @param dock - is dock that became available again
     */
    public void removeReservation(Dock dock) {
        availableDocks.add(dock);
    }

    /**
     * <p>A queue of ships that didn't reserve docks and want to reach port</p>
     * @return queue of ships in status "Waiting dock"
     */
    public Queue<Ship> getWaitingDockQueue() {
        return waitingDockQueue;
    }

    /**
     * <p>Method adds ship to queue of ships that want to wait available docks</p>
     * @param ship - ship that will be added
     * @return  <i>true</i> if queue not full or <i>false</i> if queue is full
     */
    public boolean addShip2WaitingDockQueue(Ship ship) {
        return waitingDockQueue.offer(ship);
    }

    /**
     *<p>Method allows get ship from waitingDockQueue</p>
     * @return first <i>Ship</i> from queue if queue has ships or <i>null</i> if queue is empty
     */
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

    /**
     * <p>Add a ship that arrived to port and waiting to start exchange with other ships.
     * When time will be over ship try to exchange with storage and leave</p>
     *
     * @param ship ship that waiting exchanging
     */
    public void add2WaitingUnloadQueue(Ship ship) {
        this.waitingUnloadQueue.add(ship);
        time2Wait.schedule(() -> startExchangeWithStorage(ship), new Random().nextInt(2), TimeUnit.SECONDS);
        Main.LOGGER.info("Ship #" + ship.getShipId() + " started wait exchanging");
    }

    /**
     * <p>Method to ScheduledExecutorService. If given time is over ship start exchanging with storage</p>
     * @param ship - ship from waitingUnloadQueue
     */
    private void startExchangeWithStorage(Ship ship) {

        //if ship still wait exchanging
        lock();
        if (waitingUnloadQueue.contains(ship)) {
            waitingUnloadQueue.remove(ship);
            unlock();
            Main.LOGGER.info("Ship #" + ship.getShipId() + " waited enough and start exchanging with storage");

            //start exchange with storage
            Employee employee = new Employee(ship, storage);
            employee.start();
            try {

                //wait until employee work with ship
                employee.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //after exchanging leave port
            lock();
            Dock dock = ship.leaveDock();
            removeReservation(dock);
            unlock();
        }else{
            unlock();
        }
    }

    /**
     * <p>Gets ship that waiting unload by type of containers.
     * It means that method search one ship with load type of containers with appropriate required type from queue of waiting ships</p>
     * @param requiredContainersType - type of loading containers
     * @return <i>Ship</i> if ship with required type exists in queue or <i>null</i> if no such ships
     */
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
