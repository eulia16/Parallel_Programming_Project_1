package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Phaser;

public class App {
    //get number of available processors, this will represent the number of threads we'll spin up
    final static int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    static volatile boolean visible = false;
    static final int delay = 500;//in milliseconds

    public static void main(String[] args) {
        //use concurrent hashmap to store all floor plans with an atomic int to store next_available_slot for
        //the concurrent hashmap to store each floor plan, also keep track of the best floor plan to display to user and
        //tell what thread it currently lives on, design base classes first such as factory floor and station,
        //and then we can devise the GUI and other shit. Look into different kinds of thread pools and such for
        //holding the threads while they work, almost all work should be independent(for each thread), aside for
        //using the exchanger class for two threads randomly exchanging part of their solution.
        //look at phaser for each epoch?

        FactoryFloor f = new FactoryFloor();

        GUI gui = new GUI(f);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Optimized Floor Plan");
            frame.setSize(new Dimension(Constants.height, Constants.width));
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(new Color(255, 255, 255));
            frame.add(gui);
            frame.setVisible(true);
            visible = true;
        });

        new Timer(delay, event -> gui.repaint()).start();

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + processors);

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
                System.out.println("Phase/Generation: " + phase);
                return phase >= Constants.numGenerations || registeredParties == 0;
            }
        };
        phaser.register();

        Runnable[] tasks = new Runnable[numberOfProcessors];
        for(int i =0; i< tasks.length; ++i) {
            tasks[i] = new FactoryFloor();
        }

        for (final Runnable task : tasks) {
            phaser.register();
            new Thread() {
                public void run() {
                    do {
                        task.run();
                        phaser.arriveAndAwaitAdvance();
                    } while (!phaser.isTerminated());
                }
            }.start();
        }
        phaser.arriveAndDeregister(); // deregister self, don't wait
    }



}






