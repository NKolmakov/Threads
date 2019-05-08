package by.grodno.runner;

import by.grodno.configs.ApplicationConfig;
import by.grodno.entities.Container;
import by.grodno.entities.Dock;
import by.grodno.entities.Port;
import by.grodno.entities.Ship;
import by.grodno.managers.PortManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Runner {
    public void run() {
        initialize();
    }

    private void initialize() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        Port port = context.getBean(Port.class);
        PortManager portManager = new PortManager(port);

        for (int i = 0; i < 10; i++) {
            List<Container> containers = new ArrayList<>();
            for (int j = 0; j < 12; j++) {
                containers.add(new Container());
            }

            Ship ship = new Ship(i, 12);
            ship.setContainers(containers);

            portManager.look4Dock(ship);
        }

        portManager.getManager().shutdown();

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hello world";
            }
        };

        FutureTask<String> futureTask = new FutureTask<String>(callable);
        new Thread(futureTask).start();

        try {
            System.out.println(futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
