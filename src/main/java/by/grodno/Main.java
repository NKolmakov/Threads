package by.grodno;

import by.grodno.runner.Runner;
import org.apache.log4j.Logger;

public class Main {
    public static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.run();
    }

    //TODO: think about exchanger usage
    //TODO: instructions for manager so select objects for exchange
}
