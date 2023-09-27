package org.example;


import java.awt.*;

//this class will contain the type, color
public final class Station implements Cloneable{

    private StationType type;
    private int x,y;

    Station(StationType type){
        this.type = type;
    }
    Station(Station s){
        this.type = s.type;
        this.x = s.getX();
        this.y = s.getY();
    }

    public StationType getStation(){
        return this.type;
    }

    public void setXandY(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getID(){
        return this.type.getID();
    }

    @Override
    public Station clone(){
        return new Station(this);
    }

}
