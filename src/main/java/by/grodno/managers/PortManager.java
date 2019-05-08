package by.grodno.managers;

import by.grodno.Main;
import by.grodno.entities.Dock;
import by.grodno.entities.Port;
import by.grodno.entities.Ship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PortManager {
    private Port port;
    private Queue<Ship> shipQueue;
    private ExecutorService manager;

    public PortManager(Port port) {
        this.port = port;
        this.manager = Executors.newFixedThreadPool(port.getDocks().size() + 1);
        this.shipQueue = new ArrayBlockingQueue<Ship>(20);
    }

    public void look4Dock(Ship ship) {
        boolean finedFree = false;
        for (Dock dock : port.getDocks()) {
            if (dock.tryLockTheDock()) {
                if(dock.isFree()) {
                    finedFree = true;
                    dock.setFree(false);
                    manager.submit(() -> waitShip2ThisDock(dock, ship));
                    Main.LOGGER.info("Корабль #" + ship.getId() + " прибыл в порт #" + dock.getDockId());
                    dock.unlockTheDock();
                    break;
                }
            }
        }

        if (!finedFree) {
            try {
                shipQueue.add(ship);
                Main.LOGGER.info("Корабль #"+ship.getId()+" добавлен в очередь");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Main.LOGGER.info("Корабль не стал ждать разгрузки");
            }
        }
    }

    private void waitShip2ThisDock(Dock dock, Ship ship) {
        dock.setShip(ship);
        ship.go2Port(new Random().nextInt(10) * 1000);
    }

    public ExecutorService getManager() {
        return manager;
    }
}
