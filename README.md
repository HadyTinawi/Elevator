# Elevator Simulator

## Overview
The Elevator Simulator is a Java application that simulates the operation of elevator systems in a multi-floor building. It allows users to analyze elevator performance metrics such as wait times based on various configuration parameters like number of elevators, building height, passenger generation probability, and more.

## Features
- Configurable simulation parameters via properties file
- Support for multiple elevators with customizable capacity
- Statistical analysis of passenger wait times
- Efficient elevator dispatching algorithm

## Requirements
- Java JDK 8 or higher
- File system access for configuration file reading

## Installation
1. Clone or download the repository
2. Compile the Java files:
```
javac *.java
```

## Usage
Run the application using the following command:
```
java Application [config_file_path]
```

If no configuration file is specified, the program will use the default `prop.properties` file.

## Configuration
The simulation can be configured using a properties file with the following parameters:

| Parameter | Property Name | Default Value | Description |
|-----------|---------------|---------------|-------------|
| Total Floors | floors | 32 | Number of floors in the building |
| Passenger Probability | passengers | 0.03 | Probability of passenger generation per floor per tick |
| Number of Elevators | elevators | 1 | Number of elevators in the system |
| Elevator Capacity | elevatorCapacity | 10 | Maximum number of passengers per elevator |
| Simulation Duration | duration | 500 | Number of simulation ticks to run |

### Sample Configuration File (prop.properties)
```
floors=32
passengers=0.03
elevators=1
elevatorCapacity=10
duration=500
```

## Architecture
The application consists of several key components:

- **Application**: Entry point that initializes the simulation
- **Simulation**: Controls the simulation logic and flow
- **BuildingConfiguration**: Manages simulation configuration parameters
- **BuildingOccupant**: Represents a passenger in the simulation
- **ElevatorUnit**: Represents a single elevator and its behavior
- **ElevatorSystem**: Manages all elevators and dispatches passengers

## Output
After running a simulation, the program outputs the following statistics:
- Average passenger wait time
- Longest passenger wait time
- Shortest passenger wait time

## Algorithm Description
The elevator dispatching algorithm attempts to optimize passenger wait times by:
1. Finding the elevator closest to the passenger that is already moving in the correct direction
2. If no elevator meets the above criteria, selecting the closest available elevator
3. Prioritizing destination floors in the elevator's movement direction

## Extending the Simulator
To extend the simulator with additional features:
1. Modify the BuildingConfiguration class to include new parameters
2. Update the ElevatorUnit or ElevatorSystem classes to implement new elevator behaviors
3. Add new metrics collection to the Simulation class
