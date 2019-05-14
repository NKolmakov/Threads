package by.grodno.entities;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Storage {
    private Lock lock = new ReentrantLock(true);
    private int capacity;
    private List<Container> containers;

    public Storage(int capacity) {
        this.capacity = capacity;
        containers = new ArrayList<>(capacity);
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * <p>Use this method to add a container to storage</p>
     *
     * @param container container to add to storage
     *
     * @return <i>true</i> if storage has free space else return <i>false</i>
     */
    public boolean loadContainer(Container container) {
        if (containers.size() + 1 <= capacity) {
            containers.add(container);
            return true;
        }

        return false;
    }

    /**
     * <p>This method return desired container from storage, if the storage has it or return null.
     * After container acquisition it will be removed from storage</p>
     *
     * @param container desired container from storage
     * @return <i>container</i> if exists or <i>null</i>
     */
    public Container unloadContainer(Container container) {

        //if storage has containers
        if (containers.size() > 0) {

            //if required container exists
            if (containers.contains(container)) {

                //remove from storage and return
                containers.remove(container);
                return container;
            }
        }

        return null;
    }

    public boolean hasFreeSpace() {
        if (containers.size() < capacity) {
            return true;
        }
        return false;
    }

    public void lockTheStorage() {
        lock.lock();
    }

    public void unlockTheStorage() {
        lock.unlock();
    }

    public List<Container> getContainersByType(ContainerTypes type) {
        List<Container> containerList = new ArrayList<>();

        for (Container container : containers) {
            if (container.getType().equals(type))
                containerList.add(container);
        }

        return containerList;
    }

    public boolean addContainers(List<Container> containers) {
        if(containers.size()+this.containers.size() <= capacity) {
            this.containers.addAll(containers);
            return true;
        }

        return false;
    }

    public List<Container> getContainers() {
        return containers;
    }
}
