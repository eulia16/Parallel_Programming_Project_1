package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;

public class App {
    //get number of available processors, this will represent the number of threads we'll spin up
    final static int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    static volatile boolean visible = false;
    static final int delay = 0;//in milliseconds
    static final int delayForStats =500;
    static final int threshold = 200;

    //CCHMP that stores a thread id to a set containing the factory floor(how the floor looks to then present the gui)
    //and that floors affinity score
    static ConcurrentHashMap<Integer, FactoryFloor> hashCodeToFloors = new ConcurrentHashMap();
    static double affinityOfBestFloor;
    static FactoryFloor bestCurrentFloor;
    static FactoryFloor bestFloorEver;
    static double bestAffinityEver;
    static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, FactoryFloor>> stats = new ConcurrentHashMap();

    public static void main(String[] args) {


        FactoryFloor f = new FactoryFloor(-1);
        bestCurrentFloor = f;
        bestFloorEver = f;
        affinityOfBestFloor = bestCurrentFloor.getTotalAffinity(bestCurrentFloor.getFloorOfStations());
        bestAffinityEver = affinityOfBestFloor;
        GUI gui = new GUI();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Optimized Floor Plan");
            frame.setSize(new Dimension(Constants.width, Constants.height));
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(new Color(255, 255, 255));
            frame.add(gui);
            frame.setVisible(true);
            visible = true;
        });

        System.out.println("Available processors: " + numberOfProcessors);


        //wait until the gui is presenting
        while (!visible) {
            Thread.onSpinWait();
        }

        //like stated above we will be using a phaser to guide
        // the threads through each iteration/Constants.numGenerations
        //grabbed this code from the java api docs(oracle)
        final Phaser phaser = new Phaser() {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                //here is where we can update the GUI as this is single threaded here so looking at state is allowed
                //grab best floor plan affinity-wise, the concurrent hashmap will be 'fully' updated after each phase,
                //so it can search for the
                try {
                    getFloorWithHighestAffinity();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                gui.repaint();

                for(Integer i : hashCodeToFloors.keySet()){
                    System.out.println("Thread: " + i + ", affinity:  " + hashCodeToFloors.get(i).getAffinity());
                }
                //just for testing purposes rn to see the gui actually change
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(phase % delayForStats == 0) {
                    ConcurrentHashMap c = new ConcurrentHashMap();

                    for(Integer i : App.hashCodeToFloors.keySet())
                        c.put(i, App.hashCodeToFloors.get(i).clone());

                    stats.put(phase, c);
                }
                if (phase == Constants.numGenerations || App.bestAffinityEver >= threshold) {
                        for(Integer i : stats.keySet()) {
                            System.out.println("Phase: " + i);
                            for (FactoryFloor c : stats.get(i).values())
                                System.out.println("Floor number: " + c.getHashCode() + ", affinity: " + c.getAffinity());
                        }
                    }



                System.out.println("Phase/Generation: " + phase);
                return phase >= Constants.numGenerations || registeredParties == 0 || App.bestAffinityEver >= threshold;
            }
        };
        phaser.register();

        Runnable[] tasks = new Runnable[numberOfProcessors];
        for(int i =0; i< tasks.length; ++i) {
            FactoryFloor temp = new FactoryFloor(i);
            tasks[i] = temp;
            hashCodeToFloors.put(i, temp);
        }


        for (final Runnable task : tasks) {
            phaser.register();
            new Thread(() -> {
                do {
                    task.run();
                    phaser.arriveAndAwaitAdvance();
                } while (!phaser.isTerminated());
            }).start();
        }

        phaser.arriveAndDeregister(); // deregister self, don't wait
    }


    //ONLY use this when phaser reaches onAdvance***
    public static void getFloorWithHighestAffinity() throws InterruptedException {
        for(Integer i : hashCodeToFloors.keySet()) {
            if (hashCodeToFloors.get(i).getAffinity() > affinityOfBestFloor) {
                double affinityOfNewBestFloor =  hashCodeToFloors.get(i).getAffinity();
                System.out.println("Best Current Floor Affinity: " + affinityOfNewBestFloor);
                affinityOfBestFloor = affinityOfNewBestFloor;
                bestCurrentFloor = new FactoryFloor(hashCodeToFloors.get(i));
            }

            }

        if (affinityOfBestFloor > bestAffinityEver) {
            bestFloorEver =  (FactoryFloor) new FactoryFloor(bestCurrentFloor).clone();


            bestAffinityEver = bestFloorEver.getAffinity(); //affinityOfBestFloor;
            System.out.println("A NEW BEST FLOOR HAS BEEN DISCOVERED WITH AN AFFINITY OF " + bestAffinityEver);
              //Thread.sleep(10000);
        }

    }

}










