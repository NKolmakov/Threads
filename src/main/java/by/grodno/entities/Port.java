package by.grodno.entities;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Port {
    private List<Dock> docks;

    public Port(List<Dock> docks) {
        this.docks = docks;
    }

    public List<Dock> getDocks() {
        return docks;
    }
}
