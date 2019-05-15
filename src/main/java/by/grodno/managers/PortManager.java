package by.grodno.managers;

import by.grodno.Main;
import by.grodno.entities.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class PortManager extends Thread {
    private Port port;
    private ExecutorService manager;


    public PortManager(Port port) {
        this.port = port;
        manager = Executors.newFixedThreadPool(port.getDockAmount());
    }

    public void startWork(Ship ship) {
        Dock dock = getAvailableDock();
        port.lock();
        if (dock != null) {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " going to dock #" + dock.getDockId());
            manager.submit(() -> workWithShip(ship, dock));
        } else {
            if (port.addShip2WaitingDockQueue(ship)) {
                Main.LOGGER.info("Ship #" + ship.getShipId() + " added to waiting dock queue");
            } else {
                Main.LOGGER.info("Ship #" + ship.getShipId() + " don't come");
            }
        }
        port.unlock();
    }

    private Dock getAvailableDock() {
        port.lock();
        //check every dock
        for (Dock dock : port.getAvailableDocks()) {

            /*if current dock don't locked by other ships lock it
             * this check required cause of other ships can try to change dock status at the same time
             */
            if (dock.tryLockTheDock())

                //if current dock have "Free" status reserve this dock and return
                if (dock.isFree()) {
                    dock.setFree(false);
                    port.reserveDock(dock);
                    dock.unlockTheDock();
                    port.unlock();
                    return dock;
                }
        }
        port.unlock();
        return null;
    }

    private void workWithShip(Ship ship, Dock dock) {
        ship.go2Dock(dock);
        lookObject4Exchange(ship);
    }

    private void lookObject4Exchange(Ship ship) {
        port.lock();
        Ship waitingShip = port.getWaiting2UnloadShip(ship.getRequiredTypeOfContainers());
        port.unlock();

        if (waitingShip != null) {

            //if such ship was found give a command to start exchange
            Employee employee = new Employee(ship, waitingShip, port.getStorage());
            employee.start();
            try {
                employee.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            port.lock();

            Dock dock = ship.leaveDock();
            Dock dock1 = waitingShip.leaveDock();
            port.removeReservation(dock);
            port.removeReservation(dock1);

            port.unlock();

            checkReady();

        } else {
            port.lock();
            port.add2WaitingUnloadQueue(ship);
            port.unlock();
            checkReady();
        }
    }

    private void checkReady() {
        Dock dock = getAvailableDock();
        if (dock != null) {
            Ship ship = port.getShipFromQueue();
            if (ship != null) {
                manager.submit(() -> workWithShip(ship, dock));
            } else {
                port.lock();
                port.removeReservation(dock);
                port.unlock();
            }
        }
    }

    public ExecutorService getManager() {
        return manager;
    }
}
