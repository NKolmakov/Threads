package by.grodno.runner;

import by.grodno.configs.ApplicationConfig;
import by.grodno.entities.*;
import by.grodno.managers.Employee;
import by.grodno.managers.PortManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Runner {
    public void run() {
        initialize();
    }

    private void initialize() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        Port port = context.getBean("port",Port.class);
        PortManager portManager = context.getBean("portmanager",PortManager.class);

        for (int i = 0; i < 25; i++) {
            Ship ship = new Ship(i+1, ContainerTypes.platform,30);
            List<Container> containers = new ArrayList<>();
            for (int j = 0; j < ship.getCapacity(); j++) {
                Container container = new Container(i+1,ContainerTypes.refrigerator);
                containers.add(container);
            }
            ship.setContainers2Unloading(containers);
            portManager.startWork(ship);
        }

        /**if port have ship queue it's forbidden to shutdown Executor Service
         * This service will recursive call himself until ship queue won't be empty
         */
        while(port.getShipQueue().size() >0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        portManager.getManager().shutdown();

        // here for test and can be deleted

        Ship ship = new Ship(1,ContainerTypes.platform,150);
        List<Container> containerList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            containerList.add(new Container(i+1,ContainerTypes.refrigerator));
        }
        ship.setContainers2Unloading(containerList);

        Ship ship1 = new Ship(2,ContainerTypes.refrigerator,70);
        List<Container> containerList1 = new ArrayList<>();
        for (int i = 0; i < ship1.getCapacity(); i++) {
            containerList1.add(new Container(i+1,ContainerTypes.platform));
        }
        ship1.setContainers2Unloading(containerList1);

        Storage storage = new Storage(200);
        List<Container> containerList2 = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            containerList2.add(new Container(i+1,ContainerTypes.platform));
        }
        storage.addContainers(containerList2);
        storage.addContainers(containerList);
        storage.addContainers(containerList1);

        Employee employee = new Employee(ship,ship1,storage);
        employee.start();
        try {
            employee.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}