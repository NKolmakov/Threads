package by.grodno.entities;

import org.springframework.stereotype.Component;

public class Container {
    private int id;
    private ContainerTypes type;

    public Container(int id, ContainerTypes type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContainerTypes getType() {
        return type;
    }

}
