package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GUI extends JPanel {
    private volatile FactoryFloor factoryFloor;
    private final static int gridSize = Constants.numRowsAndColumnsForGui;
    private final static int squareSize = 80;

    private final static int moveTextDownPixels = 25;
    private static int space;

    public GUI(FactoryFloor factoryFloor){
        super();

        space = (800 - gridSize)/10;
        this.setBounds(600,600,800,800);
        this.setOpaque(true);
        this.setSize(1000,1000);
        this.setBackground(Color.WHITE);

        this.setVisible(true);
        this.factoryFloor = factoryFloor;

    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int squareSize = this.squareSize;
        int padding = 20, fontSize = 9;
        Graphics2D g2 = (Graphics2D) g;

        //set font size to be smaller to allow painting the strings to be easier
        Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);
        g2.setFont(font);
        Random random = new Random();




        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int x = col * squareSize + padding;
                int y = row * squareSize + padding;
                //this will be if station(x, y coords) == null, set spot equal to black color
                if(row == random.nextInt(gridSize) || col == random.nextInt(gridSize)){
                    g2.setColor(Color.BLACK); // You can set your desired color
                    g2.fillRect(x + padding, y + padding, squareSize, squareSize);
                    //we will then draw a string that has some info on it on each square
                    g2.setColor(Color.WHITE);
                    g2.drawString("(" + x + "," + y + ")", x + padding, y + padding + moveTextDownPixels);
                    g2.drawString("Empty slot", x + padding, y + padding + moveTextDownPixels + moveTextDownPixels);


                    continue;
                }


                // Draw a smaller square
                StationType tempStation = StationType.getRandomStation();
                g2.setColor(tempStation.getColor()); // You can set your desired color

                g2.fillRect(x + padding, y + padding, squareSize, squareSize);
                g2.setColor(Color.BLACK);
                g2.drawString("(" + x + "," + y + ")", x + padding, y + padding + moveTextDownPixels);
                g2.drawString(tempStation.getStationType(tempStation), x + padding, y + padding + moveTextDownPixels + moveTextDownPixels);
            }
        }
    }


    //enforce synchronization to force only one thread to be allowed to update the floor to display
    public synchronized void setFactoryFloorToDisplay(FactoryFloor f){
        this.factoryFloor = f;
    }

    public synchronized FactoryFloor getFactoryFloorToDisplay(){
        return this.factoryFloor;
    }


}
