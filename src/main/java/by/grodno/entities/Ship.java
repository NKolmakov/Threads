package by.grodno.entities;

import by.grodno.Main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ship {
    private Lock lock = new ReentrantLock();
    private int shipId;
    private int capacity;
    private Dock dock;
    private List<Container> containers2Unloading;
    private ContainerTypes requiredTypeOfContainers;
    private List<Container> containers2Loading;

    public Ship(int shipId, ContainerTypes requiredTypeOfContainers, int capacity) {
        this.shipId = shipId;
        this.requiredTypeOfContainers = requiredTypeOfContainers;
        this.capacity = capacity;
        this.containers2Unloading = new ArrayList<>(capacity);
        this.containers2Loading = new ArrayList<>(capacity);
    }

    public int getShipId() {
        return shipId;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Container> getContainers2Unloading() {
        return containers2Unloading;
    }

    public void setContainers2Unloading(List<Container> containers2Unloading) {
        this.containers2Unloading = containers2Unloading;
    }

    public List<Container> getContainers2Loading() {
        return containers2Loading;
    }

    /**
     * Add a container to ship. Use this method to load ship
     * @param container container to add
     * @return true if ship not full otherwise return false
     */
    public boolean loadContainer(Container container) {
        if(containers2Loading.size()+1 <= capacity) {
            this.containers2Loading.add(container);
            return true;
        }else{
            Main.LOGGER.error("Can't add container #"+container.getId()+" to ship #"+shipId+". Ship is full");
            return false;
        }
    }

    /**
     *<p>This method designed for unload current ship. Last container removes from unload container's list.</p>
     * @return the last container if it exists or null
     */
    public Container unloadContainer() {
        Container container = null;
        if(containers2Unloading.size()>0){
            container = containers2Unloading.get(containers2Unloading.size()-1);
            this.containers2Unloading.remove(containers2Unloading.size()-1);
        }

        return container;
    }

    /**
     *
     * @return type of containers that ship want to load
     */
    public ContainerTypes getRequiredTypeOfContainers() {
        return requiredTypeOfContainers;
    }

    public void go2Dock(Dock dock){
        //ship goes to dock from 1 to 10 sec
        dock.tryLockTheDock();
        this.dock = dock;
        int time2ReachPort = new Random().nextInt(10)*1000 + 1000;

        try {
            Thread.sleep(time2ReachPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Main.LOGGER.error(e.getStackTrace());
        }
        Main.LOGGER.info("Ship #"+shipId+" was swimming to dock #"+dock.getDockId()+" for "+time2ReachPort/1000+" sec.");
        dock.unlockTheDock();
    }

    public Dock leaveDock(){
        dock.tryLockTheDock();
        Dock dock = this.dock;
        dock.setFree(true);
        Main.LOGGER.info("Ship #" + shipId + " left dock #" + dock.getDockId());
        dock.unlockTheDock();
        this.dock = null;
        return dock;
    }

    public void lockShip(){
        lock.lock();
    }

    public void unlockShip(){
        lock.unlock();
    }

}
