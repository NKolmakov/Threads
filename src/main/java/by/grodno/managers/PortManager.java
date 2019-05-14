package by.grodno.managers;

import by.grodno.Main;
import by.grodno.entities.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
public class PortManager extends Thread {
    private Port port;
    private Exchanger<List<Container>> containerExchanger = new Exchanger<>();
    private ExecutorService manager;
    private ExecutorService employee;
    private ExecutorService time2Unload = Executors.newSingleThreadScheduledExecutor();


    public PortManager(Port port) {
        this.port = port;
        manager = Executors.newFixedThreadPool(port.getDockAmount());
        employee = Executors.newFixedThreadPool(port.getDockAmount());
    }

    public void startWork(Ship ship) {
        Dock dock = getAvailableDock();
        if (dock != null) {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " going to dock #" + dock.getDockId());
            manager.submit(() -> workWithShip(ship, dock));
        } else if (port.addShip2Queue(ship)) {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " added to waiting dock queue");
        } else {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " don't come");
        }
    }


    public ExecutorService getManager() {
        return manager;
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
//        port.lock();
//        port.removeReservation(dock);
//        port.unlock();
//
//        checkReady();
    }

    private void checkReady() {
        port.lock();
        Ship ship = port.getShipFromQueue();
       // Dock dock = getAvailableDock();
        port.unlock();
        if (ship != null) {
            startWork(ship);
//            if (dock != null) {
//            //    manager.submit(() -> workWithShip(ship, dock)); // here changed
//            } else {
//                port.lock();
//                port.addShip2Queue(ship);
//                port.unlock();
//            }
        }
    }

    private void lookObject4Exchange(Ship ship) {
        int available;
        // port.lock();
        //if port has queue of ships staying at port try to find ship with required container's type
        port.lock();
        Ship waitingShip = port.getWaiting2UnloadShip(ship.getRequiredTypeOfContainers());
        port.unlock();
        // port.unlock();
        if (waitingShip != null) {

            //if such ship was found give a command to start exchange
            Employee employee = new Employee(ship, waitingShip, port.getStorage());
            employee.start();
            try {
                employee.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //new
//            ship.lockShip();
//            waitingShip.lockShip();

            Dock dock = ship.leaveDock();
            Dock dock1 = waitingShip.leaveDock();

//            ship.unlockShip();
//            waitingShip.unlockShip();
            port.lock();
            port.removeReservation(dock);
            port.removeReservation(dock1);
            available = port.getAvailableDocks().size();
            port.unlock();

            while (available == 0) {
                try {
                    port.lock();
                    available = port.getAvailableDocks().size();
                    port.unlock();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            checkReady();
            //end new

        } else {
            port.lock();
            port.add2WaitingUnloadQueue(ship);
            available = port.getAvailableDocks().size();
            Main.LOGGER.info("Ship #"+ship.getShipId()+" can't find ship 4 exchange and should be added to waiting queue: "+port.getWaitingUnloadQueue().size()+"; available: "+available);
            port.unlock();
            while (available == 0) {
                try {
                    port.lock();
                    available = port.getAvailableDocks().size();
                    port.unlock();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            checkReady();
        }
    }
}
