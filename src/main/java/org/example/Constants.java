package org.example;

public final class Constants {

    //Factory affinity score constants(for when calculating total affinity)

    public final static double sameStation = .1;
    public final static double lessThanStation = .25;
    public final static double greaterThanStation = .5;


    //GUI constants(height/width and whatnot)
    public final static int height = 1000;
    public final static int width = 1000;

    //constants for genetic algorithm and station/factoryfloor classes
    public final static int stations = 32; //at least 32 stations

    public final static int spotsForStations = stations * 2; //must be at least = to stations, can be greater, we chose to be 2x

    public final static int numRowsAndColumnsForGui = stations/4;

    public final static double mutationRate = 0.25; //randomly selected mutation rate

    public final static double numGenerations = 1000; //number of generations to be calculated

    public final static double stopAffinity = greaterThanStation * greaterThanStation;//potential stop program if
    //affinity reaches this number, this or numGenerations will be the stopping point of the program...tbd!

    //this is where we define the affinity of placing any 2 stations next to eachother
    //**SUBJECT TO CHANGE**
    public static double affinityCalculation(StationType station1, StationType station2){
        if(station1 == station2) //case: same station
            return sameStation;
        if(station1.ordinal() < station2.ordinal())
            return lessThanStation;
        if(station1.ordinal() > station2.ordinal())
            return greaterThanStation;

        /* unreachable */
        return -1;
    }

    public static double normalizeTotalAffinity(double totalAffinity){
        //the minimum has to be that every station equals itself
        double min = stations * sameStation;
        //the max has to be that every station is greater that the others(impossible but useful for normalization)
        double max = stations * greaterThanStation;

        //normalization equation = (x - xmin / xmax - xmin)
        return ( (totalAffinity - min) / (max - min));


    }

}
