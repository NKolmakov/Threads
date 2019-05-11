package by.grodno.managers;

import by.grodno.Main;
import by.grodno.entities.*;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PortManager extends Thread {
    private Port port;
    private ExecutorService manager;
    private ExecutorService employee;


    public PortManager(Port port) {
        this.port = port;
        manager = Executors.newFixedThreadPool(port.getDockAmount());
        employee = Executors.newFixedThreadPool(port.getDockAmount());
    }

    public void startWork(Ship ship) {
        Dock dock = getAvailableDock();
        if (dock != null) {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " going to dock #" + dock.getDockId());
            manager.submit(() -> waitShip(ship, dock));
        } else {
            if (port.addShip2Queue(ship)) {
                Main.LOGGER.info("Ship #" + ship.getShipId() + " added to queue");
            } else {
                Main.LOGGER.info("Ship #" + ship.getShipId() + " don't come");
            }
        }
    }

    public ExecutorService getManager() {
        return manager;
    }

    private Dock getAvailableDock() {

        //if port has available docks
        if (port.getAvailableDocks().size() > 0) {

            //check every dock
            for (Dock dock : port.getAvailableDocks()) {

                /**if current dock don't locked by other ships lock it
                 * this check required cause of other ships can try to change dock status at the same time
                 */
                if (dock.tryLockTheDock())

                    //if current dock have "Free" status reserve this dock and return
                    if (dock.isFree()) {
                        dock.setFree(false);
                        port.reserveDock(dock);
                        dock.unlockTheDock();
                        return dock;
                    }

            }
        }
        return null;
    }

    private void waitShip(Ship ship, Dock dock) {
        ship.go2Dock(dock);
        ship.leaveDock(dock);
        port.lock();
        port.removeReservation(dock);
        port.unlock();
        checkReady();
    }

    private void checkReady() {
        //    Main.LOGGER.info("STATE: queue "+port.getShipQueue().size());
        if (port.getShipQueue().size() > 0) {
            startWork(port.getShipFromQueue());
        }
    }
}
