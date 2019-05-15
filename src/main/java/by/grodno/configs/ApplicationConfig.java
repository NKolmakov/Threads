package by.grodno.configs;

import by.grodno.entities.*;
import by.grodno.managers.PortManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
@ComponentScan(basePackages = "by.grodno.entities, by.grodno.managers")
public class ApplicationConfig {

    @Bean(name = "filledShips")
    public List<Ship> getShipList() {
        int nextShipId = 1;
        int nextContainerId = 1;
        List<Ship> ships = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Ship ship1 = new Ship(nextShipId++, ContainerTypes.platform, new Random().nextInt(20) + 10);
            ship1.addContainers2Unloading(getFilledContainers(ship1.getCapacity(), ContainerTypes.tank, nextContainerId));
            nextContainerId += ship1.getContainers2Unloading().size();

            Ship ship2 = new Ship(nextShipId++, ContainerTypes.isothermal, new Random().nextInt(20) + 10);
            ship2.addContainers2Unloading(getFilledContainers(ship2.getCapacity(), ContainerTypes.standard, nextContainerId));
            nextContainerId += ship2.getContainers2Unloading().size();

            Ship ship3 = new Ship(nextShipId++, ContainerTypes.refrigerator, new Random().nextInt(20) + 10);
            ship3.addContainers2Unloading(getFilledContainers(ship3.getCapacity(), ContainerTypes.isothermal, nextContainerId));
            nextContainerId += ship3.getContainers2Unloading().size();

            Ship ship4 = new Ship(nextShipId++, ContainerTypes.standard, new Random().nextInt(20) + 10);
            ship4.addContainers2Unloading(getFilledContainers(ship4.getCapacity(), ContainerTypes.refrigerator, nextContainerId));
            nextContainerId += ship4.getContainers2Unloading().size();

            Ship ship5 = new Ship(nextShipId++, ContainerTypes.tank, new Random().nextInt(20) + 10);
            ship5.addContainers2Unloading(getFilledContainers(ship5.getCapacity(), ContainerTypes.platform, nextContainerId));

            ships.add(ship1);
            ships.add(ship2);
            ships.add(ship3);
            ships.add(ship4);
            ships.add(ship5);
        }

        return ships;
    }

    @Bean(name = "port")
    public Port getPort() {
        return new Port(getDocks(), 10, getFilledStorage());
    }

    @Bean(name = "portManager")
    public PortManager getPortManager() {
        return new PortManager(getPort());
    }

    private Queue<Dock> getDocks() {
        Queue<Dock> dockList = new ArrayBlockingQueue<>(10);
        for (int i = 0; i < 10; i++) {
            Dock dock = new Dock(i + 1);
            dockList.add(dock);
        }
        return dockList;
    }

    private Storage getFilledStorage() {
        Storage storage = new Storage(300);
        List<Container> containers = new ArrayList<>();

        //create 40 containers of all types
        for (int i = 0; i < 20; i++) {
            containers.add(new Container(i + 1, ContainerTypes.platform));
            containers.add(new Container(i + 1, ContainerTypes.isothermal));
            containers.add(new Container(i + 1, ContainerTypes.refrigerator));
            containers.add(new Container(i + 1, ContainerTypes.standard));
            containers.add(new Container(i + 1, ContainerTypes.tank));
        }
        storage.addContainers(containers);

        return storage;
    }

    private List<Container> getFilledContainers(int amount, ContainerTypes type, int startId) {
        List<Container> containers = new ArrayList<>();
        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                containers.add(new Container(startId++, type));
            }
        }
        return containers;
    }

    private Storage getEmptyStorage() {
        return new Storage(200);
    }
}
