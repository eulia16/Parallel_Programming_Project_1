package org.example;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//will represent a factory floor of different stations,
public class FactoryFloor implements Runnable, Cloneable {
    private static Exchanger<Station[][]> exchanger = new Exchanger<>();
    private Station[][] factoryFloor;
    private volatile double totalFloorAffinity;
    private int hashCode;

    /*when creating a factory floor, there should be one constructor that allows for defining the floor,
      and one that will instantiate random stations for the whole floor.
     */
    //will accept defined factory floor
    public FactoryFloor(int hashCode){
       //upon creation of the floor make a random board, ensuring there is a constant number of holes(8 in this case)
        this.hashCode = hashCode;
       //now we have a random board, we must see if e
       generateRandomFactoryFloor();
    }

    public FactoryFloor(FactoryFloor f){
        this.factoryFloor = f.factoryFloor;
        this.totalFloorAffinity = f.getAffinity(); // getTotalAffinity(f.factoryFloor);
    }


    //mating(will use other thread as 'parent', using the exchanger we will exchange randomly selected strands)
    //we will be using swap mutation for each row, there will be a randomly assigned value that is the 'mutation' rate
    // and will be applied accordingly
    public void mutation(){

        Station[][] tempFactoryFloor = new Station[Constants.boardWidth][Constants.boardHeight];  // = this.factoryFloor;
        for(int i=0; i<Constants.boardWidth; ++i){
            for(int j=0; j<Constants.boardHeight; ++j){
                tempFactoryFloor[i][j] = this.factoryFloor[i][j].clone();
            }
        }
        double tempFactoryFloorScore = this.totalFloorAffinity;

        int mutationRate = ThreadLocalRandom.current().nextInt( Constants.mutationRate * 20);

        //happens 1 percent of the time
        if(mutationRate < Constants.mutationRate){
            int randomRow = ThreadLocalRandom.current().nextInt( Constants.boardWidth);
            int randomCol = ThreadLocalRandom.current().nextInt( Constants.boardHeight);
            tempFactoryFloor[randomRow][randomCol] = new Station(StationType.getRandomStation());
        }

        double newFactoryAffinity = this.getTotalAffinity(tempFactoryFloor);

        //using raw if newFactoryAffinity > tempFactoryFloorScore pretty much sticks us at local maxima,
        //so we should tinker w/ different > a certain percentage values
         //if( newFactoryAffinity >  tempFactoryFloorScore ) {

        System.out.println( "****The new Floor has been mutated and has a better affinity than the previous floor****");
        System.out.println("new affinity: " + newFactoryAffinity  + ", " + " prev affinity "+  tempFactoryFloorScore);
        this.factoryFloor = tempFactoryFloor.clone();
        this.totalFloorAffinity = newFactoryAffinity;

//        }
//        else
//            System.out.println("mutated Floor design was worse than current floor plan, mutation did NOT occur");

    }

    public void crossover() throws InterruptedException, TimeoutException {
       Station[][] temp = getSubSetOfFactoryFloor();
       Station[][] segmentFromOtherThread = this.exchanger.exchange(temp, 1000, TimeUnit.MILLISECONDS);

       Station[][] potentialNewFloor = new Station[Constants.width][Constants.height];

       if(segmentFromOtherThread != null){
           System.out.println( "Thread: " + Thread.currentThread().getName() +  ", Inside crossover method");
           int randomRow = ThreadLocalRandom.current().nextInt(Constants.boardWidth);

           for(int i=0; i<Constants.boardWidth; ++i){

               for(int j=0; j<Constants.boardHeight; ++j){
                   if(i == randomRow &&  segmentFromOtherThread[0][j] != null) {
                       potentialNewFloor[i][j] = segmentFromOtherThread[0][j];
                   }
                   else{
                       potentialNewFloor[i][j] = this.factoryFloor[i][j];
                   }
               }
           }
       }
        //if the segment from the other floor is null, the crossover did not work/there wasnt another thread to exchange
        //with, so we will just keep the same factory floor as before the crossover and move onto mutation

        double affinityOfCurrentFloor = this.totalFloorAffinity;
        double affinityOfContestingFloor = this.getTotalAffinity(potentialNewFloor);

        if(this.totalFloorAffinity > this.getTotalAffinity(potentialNewFloor)){
            return;
        }


        //if(affinityOfContestingFloor > affinityOfCurrentFloor) {
        System.out.println("Thread: " + this.getHashCode() + " has found a better floor, the new affinity is: " + affinityOfContestingFloor + ", while " +
                 " the old affinity was: " + affinityOfCurrentFloor  );
        this.factoryFloor = potentialNewFloor;
        this.totalFloorAffinity = affinityOfContestingFloor;
         //}

        //now grab the current floor in the CCHSMP and check to see if the potential new floor is better
        FactoryFloor bestCurrentFloor = App.hashCodeToFloors.get(this.hashCode);
        if(this.totalFloorAffinity > bestCurrentFloor.getAffinity()){
            App.hashCodeToFloors.put(this.hashCode, this);
        }

    }

    //will take a portion of the floor of this factory floor and return it to allow for it to be exchanged w another
    //thread
    private Station[][] getSubSetOfFactoryFloor() {
        int randomRow = ThreadLocalRandom.current().nextInt(Constants.boardWidth);//max half the board

        Station[][] tempStationSegment = new Station[1][Constants.boardWidth];
            for(int i=0; i<Constants.boardWidth; ++i){
                tempStationSegment[0][i] = this.factoryFloor[randomRow][i];//new Station(StationType.getRandomStation());//this.factoryFloor[randomRow][i];
            }
        return tempStationSegment;
    }


    //to check for affinity we'll have to sum all the north, south, east, and west neighbors and then normalize
    public double getTotalAffinity(Station[][] passedFactoryFloor){
        double totalBoardAffinity = 0;

        for(int i = 0; i < Constants.boardWidth; ++i){

            for(int j = 0; j < Constants.boardHeight; ++j) {
               totalBoardAffinity += getNeighborSum(passedFactoryFloor[i][j], i, j);

            }

        }
            return totalBoardAffinity;
        }

        public double getAffinity(){
        return this.totalFloorAffinity;
        }

        public Station[][] getFloorOfStations(){
            return this.factoryFloor;
        }

    public double getNeighborSum(Station individualStation, int rowIndex, int colIndex) {
        int numRows = Constants.boardWidth;
        int numCols = Constants.boardHeight;
        double sum = 0;

        if (rowIndex >= 0 && rowIndex < numRows && colIndex >= 0 && colIndex < numCols) {
            // Check north neighbor
            if (rowIndex > 0) {
                sum += Constants.affinityCalculation(individualStation.getStation(), this.factoryFloor[rowIndex - 1][colIndex].getStation() ) ;
            }
            // Check east neighbor
            if (colIndex < numCols - 1) {
                sum += Constants.affinityCalculation(individualStation.getStation(), this.factoryFloor[rowIndex][colIndex + 1].getStation());
            }
            // Check south neighbor
            if (rowIndex < numRows - 1) {
                sum += Constants.affinityCalculation(individualStation.getStation(), this.factoryFloor[rowIndex + 1][colIndex].getStation());
            }
            // Check west neighbor
            if (colIndex > 0) {
                sum += Constants.affinityCalculation(individualStation.getStation(), this.factoryFloor[rowIndex][colIndex - 1].getStation());
            }

            return sum;
        } else {
            return 0;
        }
    }

    public void generateRandomFactoryFloor(){
        this.factoryFloor = new Station[Constants.boardHeight][Constants.boardWidth];
        int numHolesCount=0;

        StationType holder;
        for(int i = 0; i < Constants.boardWidth; ++i){
            for(int j = 0; j < Constants.boardHeight; ++j){
                if(numHolesCount == Constants.numHoles){
                    for(;;){
                        holder = StationType.getRandomStation();
                        if(holder.getStationType(holder.getID()) != "Hole"){
                            this.factoryFloor[i][j] = new Station(holder);
                            this.factoryFloor[i][j].setXandY(i, j);
                            break;
                        }
                    }
                }
                else {
                    holder = StationType.getRandomStation();
                    if (holder.getStationType(holder) == "Hole") {
                        numHolesCount++;
                    }
                    this.factoryFloor[i][j] = new Station(holder);
                    this.factoryFloor[i][j].setXandY(i, j);
                }
            }

        }

        this.totalFloorAffinity = this.getTotalAffinity(this.factoryFloor);

    }

    public int getHashCode(){
        return this.hashCode;
    }

    @Override
    public Object clone(){
        return new FactoryFloor(this);
    }

    //currently just a test for the phaser and such
    @Override
    public void run() {
        try {
            crossover();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
            mutation();

    }


}
