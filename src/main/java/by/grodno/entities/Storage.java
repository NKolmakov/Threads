package by.grodno.entities;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class Storage{
    private int capacity;
    private List<Container> containers;

    public Storage(int capacity) {
        this.capacity = capacity;
        containers = new ArrayList<>(capacity);
    }

    public int getCapacity() {
        return capacity;
    }

}
