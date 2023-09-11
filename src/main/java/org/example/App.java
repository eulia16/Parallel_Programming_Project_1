package org.example;

import javax.swing.*;
import java.awt.*;

public class App {
    //get number of available processors, this will represent the number of threads we'll spin up
    final static int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    static volatile boolean visible = false;

    public static void main( String[] args ) {
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
            frame.setSize(new Dimension(Constants.height,Constants.width));
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(new Color(255, 255, 255));

            frame.add(gui);

            frame.setVisible(true);
            visible = true;
        });

        new Timer(1000, event -> gui.repaint()).start();


    }


}
