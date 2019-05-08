package by.grodno.configs;

import by.grodno.entities.Dock;
import by.grodno.entities.Port;
import by.grodno.entities.Ship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "by.grodno.entities, by.grodno.managers")
public class ApplicationConfig {

    @Bean
    public List<Dock> docks() {
        List<Dock> dockList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Dock dock = new Dock();
            dock.setDockId(i);
            dockList.add(dock);
        }
        return dockList;
    }

    @Bean
    public Port port() {
        return new Port(docks());
    }


}
