package org.example;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

//we will have them all have the same shape(a square) for simplicity of placement
public enum StationType{
    MillingMachine (0, Color.CYAN),
    WortBoiler (1, Color.GREEN),
    FermentationTank (2, Color.RED),
    FiltrationSystem (3, Color.MAGENTA),
    Hole(4, Color.BLACK);

    private int ID;
    private Color color;

    private final static StationType[] stationTypesArray = values();

    public static StationType getRandomStation(){
        int randomNumber =  ThreadLocalRandom.current().nextInt(StationType.values().length);
        return stationTypesArray[randomNumber];
    }
    public Color getColor(){
        return this.color;
    }

    public String getStationType(StationType st){
        if(st.ID == 0)
            return "MillingMachine";
        if(st.ID == 1)
            return "WortBoiler";
        if(st.ID == 2)
            return "FermentationTank";
        if(st.ID == 3)
            return "FiltrationSystem";
        if(st.ID == 4)
            return "Hole";

        return "";
    }
    public String getStationType(int id){
        if(id == 0)
            return "MillingMachine";
        if(id == 1)
            return "WortBoiler";
        if(id == 2)
            return "FermentationTank";
        if(id == 3)
            return "FiltrationSystem";
        if(id == 4)
            return "Hole";

        return "";
    }

    public int getID(){
        return this.ID;
    }

    StationType(int i, Color cyan) {
        this.ID = i;
        this.color = cyan;
    }

}

