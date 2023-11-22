//BuildingApp.java
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class BuildingApp {

 public static void main(String[] args) {
     String configFile;

     if (args.length > 0) {
         configFile = args[0];
     } else {
         configFile = "prop.properties";
     }
     //create the instance 
     BuildingApp buildingApp = new BuildingApp();
     buildingApp.runSimulation(configFile);
 }

 void runSimulation(String configFile) { //Running the simulation
     BuildingConfiguration buildingConfig = new BuildingConfiguration(configFile); //buildingCongifugration
     List<BuildingOccupant> allOccupants = new ArrayList<>(); //Creating the Arraylist of occupants (passengers)
     ElevatorSystem elevatorSystem = new ElevatorSystem(buildingConfig.getNumElevators(), buildingConfig.getElevatorCapacity()); //creating instance of the system

     Random random = new Random(); //random object
     int simulationTicks = buildingConfig.getSimulationDuration(); //getting the duration of simulation ticks

     for (int tick = 0; tick < simulationTicks; tick++) {//generating passengers
         generateOccupants(allOccupants, buildingConfig, elevatorSystem, random); 
         elevatorSystem.moveElevators();//moving elevator
         updateOccupantWaitTimes(allOccupants);//update the wait times of passengers
     }

     summarizeResults(allOccupants); //call the summarize function
 }

 private void generateOccupants(List<BuildingOccupant> allOccupants, BuildingConfiguration buildingConfig,
                                 ElevatorSystem elevatorSystem, Random random) { //create passengers
     int floors = buildingConfig.getTotalFloors();
     double probability = buildingConfig.getPassengerProbability();

     for (int floor = 1; floor <= floors; floor++) {
         if (random.nextDouble() < probability) {
             int destinationFloor;
             do {
                 destinationFloor = random.nextInt(floors) + 1;
             } while (destinationFloor == floor);
             BuildingOccupant occupant = new BuildingOccupant(floor, destinationFloor);
             elevatorSystem.addOccupant(occupant);
             allOccupants.add(occupant);
         }
     }
 }

 private void updateOccupantWaitTimes(List<BuildingOccupant> allOccupants) { //updating wait times
     for (BuildingOccupant occupant : allOccupants) {
         if (!occupant.isInElevator()) {
             occupant.incrementWaitTime();
         }
     }
 }

 private void summarizeResults(List<BuildingOccupant> allOccupants) { //summarizes results and prints the output
     int totalWaitTime = 0;
     int longestWaitTime = 0;
     int shortestWaitTime = Integer.MAX_VALUE;

     for (BuildingOccupant occupant : allOccupants) {
         int waitTime = occupant.getWaitTime();
         totalWaitTime += waitTime;
         longestWaitTime = Math.max(longestWaitTime, waitTime);
         shortestWaitTime = Math.min(shortestWaitTime, waitTime);
     }

     double averageWaitTime;

     if (allOccupants.isEmpty()) {
         averageWaitTime = 0;
     } else {
         averageWaitTime = (double) totalWaitTime / allOccupants.size();
     }

     System.out.println("Average Wait Time: " + averageWaitTime);
     System.out.println("Longest Wait Time: " + longestWaitTime);
     System.out.print("Shortest Wait Time: ");

     if (shortestWaitTime == Integer.MAX_VALUE) {
         System.out.println(0);
     } else {
         System.out.println(shortestWaitTime);
     }
 }
}

class BuildingConfiguration { //reads properties
 private final Properties properties = new Properties();

 private static final String DEFAULT_STRUCTURE = "linked";
 private static final int DEFAULT_TOTAL_FLOORS = 32;
 private static final double DEFAULT_PASSENGER_PROBABILITY = 0.03;
 private static final int DEFAULT_NUM_ELEVATORS = 1;
 private static final int DEFAULT_ELEVATOR_CAPACITY = 10;
 private static final int DEFAULT_SIMULATION_DURATION = 500;

 public BuildingConfiguration(String filename) {
     try {
         properties.load(new FileInputStream(filename));
     } catch (IOException e) { //catches exceptions
         setDefaultProperties(); //gives the default properties in Table 1
     }
 }

 private void setDefaultProperties() { //stores the default properties
     properties.setProperty("structure", DEFAULT_STRUCTURE);
     properties.setProperty("total_floors", Integer.toString(DEFAULT_TOTAL_FLOORS));
     properties.setProperty("passenger_probability", Double.toString(DEFAULT_PASSENGER_PROBABILITY));
     properties.setProperty("num_elevators", Integer.toString(DEFAULT_NUM_ELEVATORS));
     properties.setProperty("elevator_capacity", Integer.toString(DEFAULT_ELEVATOR_CAPACITY));
     properties.setProperty("simulation_duration", Integer.toString(DEFAULT_SIMULATION_DURATION));
 }

 public int getTotalFloors() { //returns total floors
     return Integer.parseInt(properties.getProperty("total_floors", String.valueOf(DEFAULT_TOTAL_FLOORS)));
 }

 public double getPassengerProbability() { //returns prob
     return Double.parseDouble(properties.getProperty("passenger_probability", String.valueOf(DEFAULT_PASSENGER_PROBABILITY)));
 }

 public int getNumElevators() { //returns # of elevators
     return Integer.parseInt(properties.getProperty("num_elevators", String.valueOf(DEFAULT_NUM_ELEVATORS)));
 }

 public int getElevatorCapacity() { //returns capacity
     return Integer.parseInt(properties.getProperty("elevator_capacity", String.valueOf(DEFAULT_ELEVATOR_CAPACITY)));
 }

 public int getSimulationDuration() { //returns duration
     return Integer.parseInt(properties.getProperty("simulation_duration", String.valueOf(DEFAULT_SIMULATION_DURATION)));
 }
}

class BuildingOccupant { // the passengers
 private int currentFloor;
 private int destinationFloor;
 private boolean inElevator;
 private int waitTime;

 public BuildingOccupant(int currentFloor, int destinationFloor) {
     if (currentFloor < 1 || destinationFloor < 1 || currentFloor == destinationFloor) {
         throw new IllegalArgumentException("Invalid floor numbers");
     }
     this.currentFloor = currentFloor;
     this.destinationFloor = destinationFloor;
     this.inElevator = false;
     this.waitTime = 0;
 }

 public void incrementWaitTime() { //increments wait time
     this.waitTime++;
 }

 public int getWaitTime() { //returns wait time
     return waitTime;
 }

 public boolean isInElevator() { //returns if inElevator
     return inElevator;
 }

 public int getCurrentFloor() { //returns current floor
     return currentFloor;
 }

 public int getDestinationFloor() { //returns the desired floor
     return destinationFloor;
 }
}

class ElevatorUnit { //the unit
 private int currentFloor;
 private boolean movingUp;
 private final List<BuildingOccupant> occupants; 
 private final int capacity; 
 private final List<Integer> destinationFloors;

 public ElevatorUnit(int capacity) {
     this.currentFloor = 1; //start at first floor
     this.movingUp = true; //move up bool
     this.occupants = new ArrayList<>(); //passenger list
     this.capacity = capacity; //capacity
     this.destinationFloors = new ArrayList<>(); //destination list
 }

 public int getCurrentFloor() { //return current floor
     return currentFloor;
 }

 public void move() { //moves elevator if no one wants to visit destination floor
     if (!destinationFloors.isEmpty()) {
         if (movingUp) {
             currentFloor++; //moves floor up
         } else {
             currentFloor--; //moves floor down
         }
         checkDestinationFloor(); //calls function for the next destination
     }
 }

 public void addOccupant(BuildingOccupant occupant) { //add passengers depending on capacity and asks for destination
     if (this.occupants.size() >= capacity) {
         throw new IllegalStateException("Elevator is full");
     }
     this.occupants.add(occupant);
     addDestinationFloor(occupant.getDestinationFloor());
 }

 private void addDestinationFloor(int destinationFloor) { //adds destination floor
     if (!destinationFloors.contains(destinationFloor)) {
         destinationFloors.add(destinationFloor);
     }
 }

 private void checkDestinationFloor() { //checks the destination floor
     if (destinationFloors.contains(currentFloor)) {
         unloadOccupants();
         destinationFloors.remove(Integer.valueOf(currentFloor));
     }
 }

 private void unloadOccupants() { //unload passengers if they reach destination
     List<BuildingOccupant> occupantsToUnload = new ArrayList<>(this.occupants);
     for (BuildingOccupant occupant : occupantsToUnload) {
         if (occupant.getDestinationFloor() == currentFloor) {
             occupant.isInElevator();
             releaseOccupant(occupant);
         }
     }
 }

 private void releaseOccupant(BuildingOccupant occupant) { // helper method
     this.occupants.remove(occupant);
 }

 public boolean isMovingUp() { //boolean moveUp
     return movingUp;
 }

 public int getCapacity() { //returns capacity
     return capacity;
 }

 public List<BuildingOccupant> getOccupants() { //returns passengers
     return occupants;
 }

 public void update() { //moves elevator and checks destination
     move();
     checkDestinationFloor();
 }
}

class ElevatorSystem { //system
 private final List<ElevatorUnit> elevatorUnits;

 public ElevatorSystem(int numElevators, int elevatorCapacity) { //keeps track of elevator units
     this.elevatorUnits = new ArrayList<>();
     for (int i = 0; i < numElevators; i++) {
         elevatorUnits.add(new ElevatorUnit(elevatorCapacity));
     }
 }

 public void addOccupant(BuildingOccupant occupant) { //adds passengers
     ElevatorUnit bestElevator = findBestElevatorForOccupant(occupant);
     if (bestElevator != null) {
         bestElevator.addOccupant(occupant);
     }
 }

 public void moveElevators() { //moves elevator
     for (ElevatorUnit elevator : elevatorUnits) {
         elevator.update();
     }
 }

 private ElevatorUnit findBestElevatorForOccupant(BuildingOccupant occupant) { //finds the best elevator for passenger
     ElevatorUnit bestElevator = null;
     int minDistance = Integer.MAX_VALUE;

     for (ElevatorUnit elevator : elevatorUnits) {
         if (elevator.getOccupants().size() < elevator.getCapacity()) {
             int distance = Math.abs(elevator.getCurrentFloor() - occupant.getCurrentFloor());

             boolean isElevatorMovingTowardsOccupant = (elevator.isMovingUp() && occupant.getCurrentFloor() > elevator.getCurrentFloor()) ||
                     (!elevator.isMovingUp() && occupant.getCurrentFloor() < elevator.getCurrentFloor());

             if (distance < minDistance && isElevatorMovingTowardsOccupant) {
                 minDistance = distance;
                 bestElevator = elevator;
             }
         }
     }

     if (bestElevator == null) {
         for (ElevatorUnit elevator : elevatorUnits) {
             if (elevator.getOccupants().size() < elevator.getCapacity()) {
                 int distance = Math.abs(elevator.getCurrentFloor() - occupant.getCurrentFloor());
                 if (distance < minDistance) {
                     minDistance = distance;
                     bestElevator = elevator;
                 }
             }
         }
     }

     return bestElevator;
 }
}

