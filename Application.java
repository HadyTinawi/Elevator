public class Application {
    public static void main(String[] args) {
        String configFile; //creating string for file

        if (args.length > 0) { 
            configFile = args[0]; //case of argument
        } else {
            configFile = "prop.properties"; //default case
        }

        BuildingApp buildingApp = new BuildingApp(); //start building
        buildingApp.runSimulation(configFile); //run the simulation
    }
}