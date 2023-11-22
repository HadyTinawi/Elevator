// Simulation.java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation { //simulation class
    private BuildingConfiguration config;
    private ElevatorSystem elevatorSystem;
    private int ticks;
    private Random random;
    private List<BuildingOccupant> allOccupants;

    public Simulation(String configFile) {
        config = new BuildingConfiguration(configFile);
        allOccupants = new ArrayList<>();
        initializeSimulation();
    }

    private void initializeSimulation() { //initializes the simulation (# of elevators, capacity, system, ticks, random object)
        int numElevators = config.getNumElevators();
        int elevatorCapacity = config.getElevatorCapacity();
        elevatorSystem = new ElevatorSystem(numElevators, elevatorCapacity);
        ticks = config.getSimulationDuration();
        random = new Random();
    }

    public void runSimulation() { //runs the simulation and generates passengers, moves elevator, keeps track of passenger wait times, and summarizes results
        for (int tick = 0; tick < ticks; tick++) {
            generateOccupants();
            elevatorSystem.moveElevators();
            updateOccupantWaitTimes();
        }
        summarizeResults();
    }

    private void generateOccupants() { //helper that actually generates the passengers
        int totalFloors = config.getTotalFloors();
        double probability = config.getPassengerProbability();

        for (int floor = 1; floor <= totalFloors; floor++) {
            if (random.nextDouble() < probability) {
                int destinationFloor;
                do {
                    destinationFloor = random.nextInt(totalFloors) + 1;
                } while (destinationFloor == floor);
                BuildingOccupant occupant = new BuildingOccupant(floor, destinationFloor);
                elevatorSystem.addOccupant(occupant);
                allOccupants.add(occupant);
            }
        }
    }

    private void updateOccupantWaitTimes() { //updates the wait time
        for (BuildingOccupant occupant : allOccupants) {
            if (!occupant.isInElevator()) {
                occupant.incrementWaitTime();
            }
        }
    }

    private void summarizeResults() { //sumarizes the result and handles case of 0 divison
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