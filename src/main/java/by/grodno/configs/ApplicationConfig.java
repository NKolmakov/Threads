package by.grodno.configs;

import by.grodno.entities.Dock;
import by.grodno.entities.Port;
import by.grodno.entities.Storage;
import by.grodno.managers.PortManager;
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
    public List<Dock> getDocks() {
        List<Dock> dockList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Dock dock = new Dock();
            dock.setDockId(i+1);
            dockList.add(dock);
        }
        return dockList;
    }

    @Bean(name = "port")
    public Port getport() {
        return new Port(getDocks(),10, getStorage());
    }

    @Bean(name = "storage")
    public Storage getStorage(){
        return new Storage(200);
    }

    @Bean(name = "portmanager")
    public PortManager getPortManager(){
        return new PortManager(getport());
    }
}
