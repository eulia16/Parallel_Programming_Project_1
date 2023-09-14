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
        //upon creation of the floor make

    }


    //mating(will use other thread as 'parent', using the exchanger we will exchange randomly selected strands)
    public void crossover(){

    }

    public void mutation(){

    }

    //to check for affinity we'll have to sum all the north, south, east, and west neighbors and then normalize
    public double getTotalAffinity(){
        return 1.24532124224;
    }



    //currently just a test for the phaser and such
    @Override
    public void run() {
        if(ThreadLocalRandom.current().nextInt(4) == 2){
            System.out.println(Thread.currentThread().threadId() + " guessed right");
        }
        System.out.println(Thread.currentThread().threadId() + " guessed the wrong number again...");


    }
}
