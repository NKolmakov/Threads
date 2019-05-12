package by.grodno.managers;

import by.grodno.Main;
import by.grodno.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Employee extends Thread {
    private boolean isExchangeWithShip = false;
    private Ship ship;
    private Ship ship4Exchange;
    private Storage storage;

    public Employee(Ship ship, Storage storage) {
        this.ship = ship;
        this.storage = storage;
    }

    public Employee(Ship ship, Ship ship4Exchange, Storage storage) {
        this.ship = ship;
        this.ship4Exchange = ship4Exchange;
        this.storage = storage;
        this.isExchangeWithShip = true;
    }

    @Override
    public void run() {
        if (isExchangeWithShip) {
            startExchangeWithOtherShip();
        } else {
            loadFromStorage(ship);
        }
    }

    private void startExchangeWithOtherShip() {

        ship.lockShip();
        ship4Exchange.lockShip();

        Main.LOGGER.info("Ship #" + ship.getShipId() + " is ready to exchange with ship #" + ship4Exchange.getShipId());
        Main.LOGGER.info("START: Ship #" + ship.getShipId() + " load capacity: " + ship.getContainers2Loading().size() + " unload capacity: " + ship.getContainers2Unloading().size());
        Main.LOGGER.info("START: Ship #" + ship4Exchange.getShipId() + " load capacity: " + ship4Exchange.getContainers2Loading().size() + " unload capacity: " + ship4Exchange.getContainers2Unloading().size());
        //while container list to load not full unload other ship
        for (int i = 0; i < ship.getCapacity(); i++) {
            Container container = ship4Exchange.unloadContainer();

            //if container exists in ship4Exchange unload it to the first ship
            if (container != null) {
                try {
                    Thread.sleep(10 * new Random().nextInt(10));
                    ship.loadContainer(container);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        //check if ship4Exchange requesting ship's unload type of containers
        if (new String(ship4Exchange.getRequiredTypeOfContainers().toString()).equalsIgnoreCase(ship.getContainers2Unloading().get(0).getType().toString())) {
            Main.LOGGER.info("Ship #" + ship4Exchange.getShipId() + " is ready to exchange with ship #" + ship.getShipId());
            for (int i = 0; i < ship4Exchange.getCapacity(); i++) {
                Container container = ship.unloadContainer();

                if (container != null) {
                    try {
                        Thread.sleep(10 * new Random().nextInt(10));
                        ship4Exchange.loadContainer(container);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }

        Main.LOGGER.info("Exchange between ship #" + ship.getShipId() + " and ship #" + ship4Exchange.getShipId() + " is over.");

        //try to take away containers from storage if first ship not full
        if (ship.getContainers2Loading().size() < ship.getCapacity()) {
            loadFromStorage(ship);
        }

        //try to take away containers form storage if second ship not full
        if (ship4Exchange.getContainers2Loading().size() < ship4Exchange.getCapacity()) {
            loadFromStorage(ship4Exchange);
        }

        Main.LOGGER.info("Ship #" + ship.getShipId() + " and ship #" + ship4Exchange.getShipId() + " may be free");
        Main.LOGGER.info("END: Ship #" + ship.getShipId() + " load: " + ship.getContainers2Loading().size() + " unload: " + ship.getContainers2Unloading().size());
        Main.LOGGER.info("END: Ship #" + ship4Exchange.getShipId() + " load: " + ship4Exchange.getContainers2Loading().size() + " unload: " + ship4Exchange.getContainers2Unloading().size());
        ship.unlockShip();
        ship4Exchange.unlockShip();

    }

    private void loadFromStorage(Ship ship) {
        storage.lockTheStorage();
        AtomicInteger counter = new AtomicInteger(0);
        //if storage has required containers start unloading
        for (Container container : storage.getContainersByType(ship.getRequiredTypeOfContainers())) {

            //if ship has free space then load container
            if (ship.getContainers2Loading().size() + 1 <= ship.getCapacity()) {
                ship.loadContainer(container);
                counter.incrementAndGet();
            } else {
                break;
            }
        }

        Main.LOGGER.info("Ship #" + ship.getShipId() + " took " + counter + " containers of type: " + ship.getRequiredTypeOfContainers() + " from storage");
        storage.unlockTheStorage();
    }

}
