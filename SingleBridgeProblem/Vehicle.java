import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Vehicle implements Runnable {
    private enum State { WAITING, CROSSING, INTERRUPTED, CROSSED, TURNED_AWAY }

    private static int vehicleCount = 0;
    private static int carsCount = 0;
    private static int trucksCount = 0;
    private static int emergencyCount = 0;
    private static int vehiclesCrossed = 0;
    
    private static Semaphore emergencyPresent = new Semaphore(1);

    private int id;
    private String type;
    private String direction;
    private Semaphore bridgeSemaphore;
    private Semaphore directionQueueSemaphore;
    private State state;
    private int queueNumber;
    private static final int MAX_WAIT_TIME = 1000; // Max waiting time in queue.

    public Vehicle(String type, String direction, Semaphore bridgeSemaphore, Semaphore directionQueueSemaphore, int queueNumber) {
        // TODO: Initialize vehicle attributes and update counts.
        this.type = type;
        this.direction = direction;
        this.bridgeSemaphore = bridgeSemaphore;
        this.directionQueueSemaphore = directionQueueSemaphore;
        this.queueNumber = queueNumber;
        this.id = id;
        
        if(this.type.equals("Car"))
        {
            carsCount++;
            vehicleCount++;
            this.id = carsCount;
        }
        if(this.type.equals("Truck"))
        {
            trucksCount++;
            vehicleCount++;
            this.id = trucksCount;
        }
        if(this.type.equals("Emergency Vehicle"))
        {
            emergencyCount++;
            vehicleCount++;
            //System.out.println("EMERGENCY VEHICLE INSTANTIATED");
            try
            {
                this.emergencyPresent.acquire();

            }
            catch (InterruptedException e)
            {
                System.out.println("Could not acquire emergency semaphore");
            }
            
            this.id = emergencyCount;
        }
    }

    @Override
    public void run() {
        try {
            //Thread.sleep(1000);
            // TODO: Implement the logic for vehicle crossing, waiting, and turning away.
            /*ideas:
            implement turning away
            try and access north and south queue semaphore and get permits. if more than 10, turn away. if less, add to queue
            have north and south go one at a time after each other
            use bridge as mux as it only has 0 or 1, free or occupied 

            NEED TO FIGURE OUT HOW TO ACCESS SEMAPHORE FOR VEHICLES IN QUEUE AND FIGURE OUT HOW THEY ARE QUEUED

            */
            int temp = 0;
            if(this.type.equals("Emergency Vehicle"))
            {
                temp = 2 - this.directionQueueSemaphore.availablePermits();
            }
            else
            {
                temp = 11 - this.directionQueueSemaphore.availablePermits();
            }
            

           if(this.directionQueueSemaphore.availablePermits() == 0 && this.type != ("Emergency Vehicle"))
           {
                System.out.println(this.type + " number " + this.id + " has turned around in fustration.");
                state = State.TURNED_AWAY;
                return;
                //just keep this here to compile the program
                //throw new InterruptedException();
           }
            this.waitForEmergency();
            System.out.println(this.type + " number " + this.id + " approaches the bridge from the " + this.direction + ". Queue spot: " + temp);
            this.waitForEmergency();
            this.directionQueueSemaphore.acquire();
            state = State.WAITING;
            this.waitForEmergency();
            this.bridgeSemaphore.acquire();
            
            while(this.emergencyPresent.availablePermits() == 0 && !this.type.equals("Emergency Vehicle"))
            {
                System.out.println("Waiting for emergency vehicle");
                this.bridgeSemaphore.release();
                //this.directionQueueSemaphore.release();
                state = State.INTERRUPTED;
                this.waitForEmergency();
                this.bridgeSemaphore.acquire();
                //this.directionQueueSemaphore.acquire();
            }
            // this.directionQueueSemaphore.acquire();
            state = State.CROSSING;
            Thread.sleep(getCrossingTime());
            System.out.println(this.type + " number " + this.id + " is crossing the bridge");

            this.updateCrossedCount();
            System.out.println(this.type + " number " + this.id + " has crossed the bridge");
            state = State.CROSSED;

            this.directionQueueSemaphore.release();
            this.bridgeSemaphore.release();
            if(this.type.equals("Emergency Vehicle"))
            {
                this.emergencyPresent.release();
                //System.out.println("Returned emergency semaphore");
            }
            /*
            System.out.println(this.type + " number " + this.id + " has turned around in fustration");
            Thread.currentThread().interrupt();
            state = State.INTERRUPTED;
            */
            // if(this.bridgeSemaphore.availablePermits() == 1 && this.directionQueueSemaphore.availablePermits() == 10 && vehicleCount == 52)
            // {
            //     System.out.println(getSummary());
            // }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO: Handle interruption.
            state = State.INTERRUPTED;
            System.out.println(type + " " + id + " " + "was interuppted while crossing");
        }

        
    }

    private void waitForEmergency() throws InterruptedException {
        // TODO: Implement waiting logic for emergencies.
        //while
        //System.out.println("Checking for emergencies");
        while(this.emergencyPresent.availablePermits() == 0 && !this.type.equals("Emergency Vehicle"))
        {
            // this.directionQueueSemaphore.release();
            // this.emergencyPresent.acquire();
            // this.emergencyPresent.release();
            // this.directionQueueSemaphore.acquire();
            Thread.sleep(100);
            
        }

        if(this.type.equals("Emergency Vehicle"))
        {
            // this.directionQueueSemaphore.release();
            // this.emergencyPresent.acquire();
            // this.emergencyPresent.release();
            // this.directionQueueSemaphore.acquire();
            Thread.sleep(100);
            //this.directionQueueSemaphore.acquire();

            
        }
                  
    }

    private void updateCrossedCount() {
        vehiclesCrossed++;
    }

    private int getCrossingTime() {
        if(this.type.equals("Car"))
        {
            return 2000; 
        }
        if(this.type.equals("Truck"))
        {
            return 3000; 
        }
        if(this.type.equals("Emergency Vehicle"))
        {
            return 1000; 
        }
        return 2000;
        // Placeholder
    }

    public static synchronized String getSummary() {
        // TODO: Return a summary of vehicle statistics.
        return ("SUMMARY: Vehicles: " + vehicleCount + ", Cars: " + carsCount + ", Trucks: " + trucksCount + ", Emergency Vehicles: " + emergencyCount + ", Vehicles Crossed: " + vehiclesCrossed); // Placeholder
    }
}

