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
        Port port = context.getBean("port", Port.class);
        PortManager portManager = context.getBean("portManager", PortManager.class);

        for (int i = 0; i < 20; i++) {
            Ship ship = new Ship(i + 1, ContainerTypes.platform, 30);
            List<Container> containers = new ArrayList<>();
            for (int j = 0; j < ship.getCapacity(); j++) {
                Container container = new Container(i + 1, ContainerTypes.refrigerator);
                containers.add(container);
            }
            ship.setContainers2Unloading(containers);
            portManager.startWork(ship);
        }

        /**if port have ship queue it's forbidden to shutdown Executor Service
         * This service will recursive call himself until ship queue won't be empty
         */
        while (port.getWaitingUnloadQueue().size() >0 || port.getWaitingDockQueue().size() >0 || port.getAvailableDocks().size() != 10){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        portManager.getManager().shutdown();
        port.getTime2Wait().shutdown();
    }
}