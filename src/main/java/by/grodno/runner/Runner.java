package by.grodno.runner;

import by.grodno.configs.ApplicationConfig;
import by.grodno.entities.Container;
import by.grodno.entities.ContainerTypes;
import by.grodno.entities.Port;
import by.grodno.entities.Ship;
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

        while(port.getShipQueue().size() >0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        portManager.getManager().shutdown();
    }
}