package org.example;


import javax.swing.plaf.TableHeaderUI;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;

//will represent a factory floor of different stations,
public class FactoryFloor implements Runnable{

    private static Exchanger<Station[][]> exchanger = new Exchanger<>();
    private Station[][] factoryFloor;
    private volatile double totalFloorAffinity;



    /*when creating a factory floor, there should be one constructor that allows for defining the floor,
      and one that will instantiate random stations for the whole floor.
     */
    //will accept defined factory floor
    public FactoryFloor(){

    }


    public double getTotalAffinity(){
        return 1.24532124224;
    }


    @Override
    public void run() {
            System.out.println(Thread.currentThread().threadId() + " guessed the wrong number again...");

    }
}
