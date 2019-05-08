package by.grodno.entities;

import by.grodno.Main;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class Ship {
   private int id;
   private int capacity;
   private List<Container> containers;

   public void go2Port(int time2ReachPort){
      try {
         Thread.currentThread().sleep(time2ReachPort);
         Main.LOGGER.info("Корабль был в пути на протяжении "+time2ReachPort);
      } catch (InterruptedException e) {
         e.printStackTrace();
         Main.LOGGER.error(e.getMessage());
      }
   }


   public Ship(int id, int capacity) {
      this.id = id;
      this.capacity = capacity;
      this.containers = new ArrayList<Container>(capacity);
   }

   public int getId(){
      return id;
   }

   public void setContainers(List<Container> containers) {
      this.containers = containers;
   }
}
