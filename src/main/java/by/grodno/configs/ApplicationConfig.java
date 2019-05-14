package by.grodno.configs;

import by.grodno.entities.*;
import by.grodno.managers.PortManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
@ComponentScan(basePackages = "by.grodno.entities, by.grodno.managers")
public class ApplicationConfig {

    @Bean(name = "docks")
    public Queue<Dock> getDocks() {
        Queue<Dock> dockList = new ArrayBlockingQueue<>(10);
        for (int i = 0; i < 10; i++) {
            Dock dock = new Dock(i+1);
            dockList.add(dock);
        }
        return dockList;
    }

    @Bean(name = "filledStorage")
    public Storage getFilledStorage(){
        Storage storage = new Storage(300);
        List<Container> containers = new ArrayList<>();

        //create 40 containers of all types
        for (int i = 0; i < 20; i++) {
            containers.add(new Container(i+1, ContainerTypes.platform));
            containers.add(new Container(i+1, ContainerTypes.isothermal));
            containers.add(new Container(i+1, ContainerTypes.refrigerator));
            containers.add(new Container(i+1, ContainerTypes.standard));
            containers.add(new Container(i+1, ContainerTypes.tank));
        }
        storage.addContainers(containers);

        return storage;
    }

    @Bean(name = "port")
    public Port getPort() {
        return new Port(getDocks(),10, getEmptyStorage());
    }

    @Bean(name = "storage")
    public Storage getEmptyStorage(){
        return new Storage(200);
    }

    @Bean(name = "portManager")
    public PortManager getPortManager(){
        return new PortManager(getPort());
    }
}
