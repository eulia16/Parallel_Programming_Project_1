package org.example;


//this class will contain the type, color
public final class Station {

    private StationType type;//stationtype, allows access to color/name...etc
    private int x,y;//will use these as coordinates

    Station(StationType type){
        this.type = type;
    }


}
