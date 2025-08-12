/*
Student Name: Shane Norden
Student ID: 011008524
*/

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.lang.Integer;
import java.util.ArrayList;
// Main class to simulate traffic on a bridge with concurrency controls.
public class BridgeSimulator {
    private static final int MAX_QUEUE_CAPACITY = 10; // Maximum capacity for vehicle queues.
    private static Semaphore northQueue = new Semaphore(MAX_QUEUE_CAPACITY);
    private static Semaphore southQueue = new Semaphore(MAX_QUEUE_CAPACITY);
    private static Semaphore bridge = new Semaphore(1);
    private static Semaphore emergencyLane = new Semaphore(1, true);
    private static Random random = new Random();
    public static ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
    public static int carCount = 0;
    public static int truckCount = 0;
    public static int emergencyVehiclesCount = 0;
    public static void main(String[] args) {
        // Check command line arguments for usage.
        if (args.length != 2) {
            System.out.println("Usage: java BridgeSimulator <simulation time in seconds or 'u'> <number of emergency vehicles>");
            System.exit(1);
        }

        boolean isUnlimited = args[0].equalsIgnoreCase("u");
        long simulationTime = isUnlimited ? Long.MAX_VALUE : Long.parseLong(args[0]);
        int emergencyVehicles = Integer.parseInt(args[1]);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);
        System.out.println("Simulation starts" + (isUnlimited ? " indefinitely." : " for " + simulationTime + " seconds."));

        // TODO: Generate vehicles (cars, trucks, and emergency vehicles) and schedule them.
        //final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        //executor.schedule(temp, )
        //ScheduledFuture<?> generatingVeh = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        generateVehicles(executor, emergencyVehicles);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(isUnlimited ? Long.MAX_VALUE : simulationTime * 4, TimeUnit.SECONDS)) {
                System.out.println("Shutting Down");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Simulation interrupted.");
            executor.shutdownNow();
        } finally {
            //System.out.println(executor.getSummary());
        }
        System.out.println(Vehicle.getSummary());
    }
    public static void generateVehicles(ScheduledExecutorService executor, int emergencyVehicles){

        //my attempt at having the vehicles arriving at random times


        //hard coding 25 cars and 25 trucks in random order as per instructions
        for(int i = 0; i < 50 + emergencyVehicles; i++){
            
            try
            {
                Thread.sleep(400);
            }
            catch(InterruptedException e)
            {
                System.out.println("Main Thread Interrupted");
            }
            
            int delayCar = 0;
            int delayTruck = 0;
            int delayEVehicle = 0;
            //get number between 0 and 10. 0-4 is car, 5-9 is truck, 10 is emergecy vehicle 

            //cars and trucks have not met count
            int randomV = 0;
            if(carCount < 25 && truckCount < 25)
            {
                if(emergencyVehiclesCount != emergencyVehicles)
                {
                    randomV = random.nextInt(11);
                }
                else
                {
                    randomV = random.nextInt(10);
                }
            }
            //if cars have not met count but trucks have
            else if(carCount < 25 && truckCount == 25)
            {
                randomV = random.nextInt(5);
            }

            //if cars havenot met count but trucks have not
            else if(carCount == 25 && truckCount < 25)
            {
                randomV = random.nextInt(5) + 5;
            }
            //emergency vehicle
            else
            {
                randomV = 10;
            }
            //System.out.println(randomV);

            //get number between 0 and 1. 0 is North, 1 is south
            int randomD = random.nextInt(2);
            if(randomV >= 0 && randomV < 5)
            {
                delayCar = random.nextInt(1000);
                int vehicleCount = i + 1;
                carCount++;
                if(randomD == 0){
                    //System.out.println("Car Count in schedule " + carCount);

                    executor.schedule(() ->  new Vehicle("Car", "North", bridge, northQueue, vehicleCount).run(), delayCar, TimeUnit.MILLISECONDS);
                    //vehicles.add(car);
                }
                else{
                    //System.out.println("Car Count in schedule " + carCount);

                    executor.schedule(() ->  new Vehicle("Car", "South", bridge, southQueue, vehicleCount).run(), delayCar, TimeUnit.MILLISECONDS);
                    
                }
                
            }
            //truck
            else if(randomV >= 5 && randomV < 10)
            {
                delayTruck = random.nextInt(1000);
                int vehicleCount = i + 1;
                truckCount++;
                if(randomD == 0){
                    //String type, String direction, Semaphore bridgeSemaphore, Semaphore directionQueueSemaphore, int queueNumber
                    //System.out.println("Truck Count in schedule " + truckCount);

                    executor.schedule(() ->  new Vehicle("Truck", "North", bridge, northQueue, vehicleCount).run(), delayTruck, TimeUnit.MILLISECONDS);

                    //vehicles.add(truck);
                }
                else{
                    //System.out.println("Truck Count in schedule " + truckCount);

                    executor.schedule(() ->  new Vehicle("Truck", "South", bridge, southQueue, vehicleCount).run(), delayTruck, TimeUnit.MILLISECONDS);

                    //vehicles.add(truck);                
                }
                
            }
            //emergency vehicle
            else
            {
                delayEVehicle = random.nextInt(1000);
                int vehicleCount = i + 1;
                emergencyVehiclesCount++;

                //System.out.println("Emergency Vehicle Count in schedule " + emergencyVehiclesCount);
                executor.schedule(() ->  new Vehicle("Emergency Vehicle", "Emergency Lane", bridge, emergencyLane, vehicleCount).run(), delayEVehicle, TimeUnit.MILLISECONDS);
                



                //vehicles.add(eVehicle);
            }         
        }
    }
}
