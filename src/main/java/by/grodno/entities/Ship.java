package by.grodno.entities;

import by.grodno.Main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class Ship {
    private int shipId;
    private int capacity;
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

    public void loadContainer(Container container) {
        this.containers2Loading.add(container);
    }

    public void unloadContainer(Container container) {
        this.containers2Unloading.remove(container);
    }

    public ContainerTypes getRequiredTypeOfContainers() {
        return requiredTypeOfContainers;
    }

    public void go2Dock(Dock dock){
        //ship goes to dock from 1 to 10 sec
        dock.tryLockTheDock();
        int time2ReachPort = new Random().nextInt(10)*1000 + 1000;

        try {
            Thread.sleep(time2ReachPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Main.LOGGER.error(e.getStackTrace());
        }
        System.out.println();
        Main.LOGGER.info("Ship #"+shipId+" arrived to dock #"+dock.getDockId());
    }

    public Dock leaveDock(Dock dock){
        dock.setFree(true);
        Main.LOGGER.info("Ship #" + shipId + " left dock #" + dock.getDockId());
        System.out.println();
        dock.unlockTheDock();
        return dock;
    }
}
