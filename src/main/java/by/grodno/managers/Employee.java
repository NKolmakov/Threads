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
            workWithStorage(ship);
        }
    }

    private void startExchangeWithOtherShip() {

        ship.lockShip();
        ship4Exchange.lockShip();

        Main.LOGGER.info("Ship #" + ship.getShipId() + " is ready to exchange with ship #" + ship4Exchange.getShipId());
        Main.LOGGER.info("start exchanging info: Ship #" + ship.getShipId() + " load: " + ship.getContainers2Loading().size() + " unload:" + ship.getContainers2Unloading().size()+" capacity: "+ship.getCapacity());
        Main.LOGGER.info("start exchanging info: Ship #" + ship4Exchange.getShipId() + " load: " + ship4Exchange.getContainers2Loading().size() + " unload:" + ship4Exchange.getContainers2Unloading().size()+" capacity: "+ship4Exchange.getCapacity());

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

        //if ship4Exchange requesting first ship's unload type of containers
        if ((ship4Exchange.getRequiredTypeOfContainers().toString()).equalsIgnoreCase(ship.getContainers2Unloading().get(0).getType().toString())) {
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

        ship.unlockShip();
        ship4Exchange.unlockShip();


        workWithStorage(ship);
        workWithStorage(ship4Exchange);

        Main.LOGGER.info("Ship #" + ship.getShipId() + " and ship #" + ship4Exchange.getShipId() + " may be free");
        Main.LOGGER.info("end exchanging info:Ship #" + ship.getShipId() + " load: " + ship.getContainers2Loading().size() + " unload: " + ship.getContainers2Unloading().size()+" storage: "+storage.getContainers().size()+" capacity: "+storage.getCapacity());
        Main.LOGGER.info("end exchanging info:Ship #" + ship4Exchange.getShipId() + " load: " + ship4Exchange.getContainers2Loading().size() + " unload: " + ship4Exchange.getContainers2Unloading().size());

    }

    private void loadFromStorage(Ship ship) {
        storage.lockTheStorage();
        ship.lockShip();
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

        ship.unlockShip();
        storage.unlockTheStorage();
    }

    private void unload2Storage(Ship ship) {
        storage.lockTheStorage();

        AtomicInteger counter = new AtomicInteger(0);
        for (int i = 0; i < ship.getCapacity(); i++) {

            if(storage.hasFreeSpace()) {
                Container container = ship.unloadContainer();

                if (container != null) {
                    storage.loadContainer(container);
                    counter.incrementAndGet();
                }else{
                    break;
                }
            }else{
                break;
            }
        }

        if(counter.get() >0) {
            Main.LOGGER.info("Ship #" + ship.getShipId() + " unload " + counter + " containers to storage");
        }

        storage.unlockTheStorage();
    }

    private void workWithStorage(Ship ship) {
        ship.lockShip();
        loadFromStorage(ship);
        unload2Storage(ship);
        ship.unlockShip();
    }

}
