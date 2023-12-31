package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GUI extends JPanel {
    private final static int gridSize = Constants.numRowsAndColumnsForGui;
    private final static int squareSize = 80;

    private final static int moveTextDownPixels = 25;
    private static int space;

    public GUI(){
        super();

        space = (800 - gridSize)/10;
        this.setBounds(600,600,800,800);
        this.setOpaque(true);
        this.setSize(1000,1000);
        this.setBackground(Color.WHITE);

        this.setVisible(true);

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

        Station[][] floorToDraw = App.bestFloorEver.getFloorOfStations();
        //Station[][] floorToDraw = App.bestEverFloor;
        for(int i =0; i < Constants.boardWidth; ++i){
            for (int j = 0; j< Constants.boardHeight; ++j){
                int x = i * squareSize + padding;
                int y = j * squareSize + padding;
                //start actually presenting the data of the best factory floor
                g2.setColor(floorToDraw[i][j].getStation().getColor()); // You can set your desired color

                g2.fillRect(x + padding, y + padding, squareSize, squareSize);
                g2.setColor(Color.BLACK);
                if(floorToDraw[i][j].getStation().getColor() == Color.BLACK)
                    g2.setColor(Color.WHITE);
                g2.drawString("(" + x + "," + y + ")", x + padding, y + padding + moveTextDownPixels);
                g2.drawString(floorToDraw[i][j].getStation().getStationType(floorToDraw[i][j].getStation()), x + padding, y + padding + moveTextDownPixels + moveTextDownPixels);



            }
        }
        g2.setColor(Color.BLACK);
        g2.drawString("Total Affinity: " + App.bestAffinityEver,350, 600 + padding + (moveTextDownPixels * 3));



    }

}
