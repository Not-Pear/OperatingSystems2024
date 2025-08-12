/*
Student Name: Shane Norden
Student ID: 011008524
*/

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.lang.Integer;
import java.util.ArrayList;
//imports from Bridge Simulator
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
public class FoodCourt {
    // Constants
    private static final int COUNTER_CAPACITY = 40;
    private static final int WAITING_AREA_CAPACITY = 100;
    private static final int NUM_COUNTERS = 3;
    private static final int NUM_CASHIERS = 10;
    private static final int UNIT_OF_TIME = 300; //300ms
    // Shared resources
    private static Random random = new Random();
    private static AtomicInteger waitingAreaCount;
    private static Semaphore counter1;
    private static Semaphore counter1MUX;
    private static Semaphore counter2;
    private static Semaphore counter2MUX;
    private static Semaphore counter0;
    private static Semaphore counter0MUX;
    private static Semaphore waitingArea;
    private static Semaphore cashierQueue;
    //private static AtomicInteger[] counterCounts;
    private static AtomicInteger counterCount0;
    private static AtomicInteger counterCount1;
    private static AtomicInteger counterCount2;
    private static int counterNum;
    private static AtomicInteger cashierCount;
    
    //executor stuff
    public static ScheduledExecutorService executor;
    public static boolean isUnlimited;
    // Configuration variables
    private static long timeUnits;
    public static int customerLimit;
    public static void main(String[] args) {
        // Parse command-line arguments
        parseArguments(args);
        // Initialize counters and resources
        initializeResources();
        // Start the system thread (manages food court closing time)
        startSystemThread(executor);
        // Start customer threads
        startCustomerThreads(executor);
        
    }
    // Helper method to parse arguments
    private static void parseArguments(String[] args) {
        // Parse and set timeUnits and customerLimit
        if (args.length != 2) {
            System.out.println("Usage: java FoodCourt <simulation time in seconds or 'u'> <number of customers>");
            System.exit(1);
        }
        isUnlimited = args[0].equalsIgnoreCase("u");
        timeUnits = isUnlimited ? Long.MAX_VALUE : Long.parseLong(args[0]);
        customerLimit = Integer.parseInt(args[1]);
        System.out.println("Customer Limit is: " + customerLimit);
        executor = Executors.newScheduledThreadPool(250);
        System.out.println("Simulation starts" + (isUnlimited ? " indefinitely." : " for " + timeUnits + " seconds."));
    }
    // Initialize semaphores and counters
    private static void initializeResources() {
        // Initialize semaphores and atomic counters
        // private static AtomicInteger waitingAreaCount;
        waitingAreaCount = new AtomicInteger();
        // private static Semaphore[] counters;
        counter0 = new Semaphore(COUNTER_CAPACITY);
        counter0MUX = new Semaphore(1);

        counter1 = new Semaphore(COUNTER_CAPACITY);
        counter1MUX = new Semaphore(1);
        counter2 = new Semaphore(COUNTER_CAPACITY);
        counter2MUX = new Semaphore(1);

        // private static Semaphore waitingArea;
        waitingArea = new Semaphore(WAITING_AREA_CAPACITY);
        // private static Semaphore cashierQueue;
        cashierQueue = new Semaphore(NUM_CASHIERS);
        // private static AtomicInteger[] counterCounts;
        //counterCounts = new AtomicInteger[NUM_COUNTERS];
        counterCount0 = new AtomicInteger();
        counterCount1 = new AtomicInteger();
        counterCount2 = new AtomicInteger();

        // private static AtomicInteger cashierCount;
        cashierCount = new AtomicInteger();

    }
    // Start the main system thread to manage simulation duration
private static void startSystemThread(ScheduledExecutorService executor) {
    executor.schedule(() -> {
    System.out.println("Simulation time is up. No more Customers allowed in the Waiting Area");
    executor.shutdown(); // Initiates an orderly shutdown of the executor
    try {
        if (!executor.awaitTermination(isUnlimited ? Long.MAX_VALUE : timeUnits, TimeUnit.SECONDS)) {
            System.out.println("Food Court is closed. Shutting Down");
            executor.shutdownNow();
        }
    } catch (InterruptedException e) {
        System.err.println("Simulation interrupted.");
        executor.shutdownNow();
    }
    }, isUnlimited ? Long.MAX_VALUE : timeUnits, TimeUnit.SECONDS);
}

    // Start threads for each customer
// Start threads for each customer
private static void startCustomerThreads(ScheduledExecutorService executor) {
    int end = customerLimit;
    for (int i = 0; i < end; i++) {
            try
            {
                Thread.sleep(400);
            }
            catch(InterruptedException e)
            {
                System.out.println("Main Thread Interrupted");
            }
        

        int delay = random.nextInt(1000); // Random delay for task scheduling
        int customerId = i;

        // Submit customer task to the executor with a delay
        executor.schedule(() -> new Customer(customerId).run(), delay, TimeUnit.MILLISECONDS);

        //System.out.println("Customer " + customerId + " has been scheduled.");
    }
}

    // Inner class to represent each Customer
    static class Customer extends Thread {
        int id;
        Customer(int id) {
            this.id = id;
        }
        public void run() {
        // Logic for customer's journey through the food court
            try{
                Thread.sleep(random.nextInt(10 * UNIT_OF_TIME));
            }
            catch (InterruptedException e){
                System.out.println("Error in Customor run try-catch");
            }
            System.out.println("Customer " + id + " has entered the food court");
            if(!handleWaitingArea()){
                System.out.println("Customer " + id + " has left the food court in FUSTRATION >:(");
                return;
            }
            chooseCounter();
            handleCashier();
        }
        private boolean handleWaitingArea() {
            // Logic for entering and leaving the waiting area
            if(!waitingArea.tryAcquire()){
                return false;
            }
            else{


                waitingAreaCount.getAndIncrement();
                System.out.println("Customer " + id + " has entered the Waiting Area. Waiting Area Count:" + waitingAreaCount);
                try{
                    Thread.sleep(random.nextInt(10 * UNIT_OF_TIME));
                }
                catch(InterruptedException e){
                    System.out.println("Could Not sleep thread in counter 0");
                }
                return true; 
            }
            
        }
        private void chooseCounter() {
            // Logic for selecting and using a counter
            counterNum = random.nextInt(3);
            //burgers
            if(counterNum == 0){
                try{
                    counter0.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting counter0 semaphore");

                }
                
                counterCount0.getAndIncrement();
                System.out.println("Customer " + id + " has entered the Burgers Line. Burger Queue Count: " + counterCount0);
                try{
                    counter0MUX.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting counter0MUX semaphore");
                }
                counterCount0.decrementAndGet();
                counter0.release();
                System.out.println("Customer " + id + " has entered the Burgers Counter");

            }

            //pizza
            else if(counterNum == 1){
                try{
                    counter1.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting counter1 semaphore");

                }
                counterCount1.getAndIncrement();
                System.out.println("Customer " + id + " has entered the Pizza Line. Pizza Queue Count: " + counterCount1);        
                try{
                    counter1MUX.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting counter1MUX semaphore");
                }
                counterCount1.decrementAndGet();
                counter1.release();
                System.out.println("Customer " + id + " has entered the Pizza Counter");
            }
            //wings
            else {
                try{
                    counter2.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting counter2 semaphore");

                }
                counterCount2.getAndIncrement();     
                System.out.println("Customer " + id + " has entered the Wings Line. Wings Queue Count: " + counterCount2);
                try{
                    counter2MUX.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting counter2MUX semaphore");
                }
                counterCount2.decrementAndGet();
                counter2.release();
                System.out.println("Customer " + id + " has entered the Pizza Counter");
            }
            try{
                Thread.sleep(random.nextInt(10 * UNIT_OF_TIME));
            }
            catch(InterruptedException e){
                System.out.println("Could Not sleep thread in counter 0");
            }
            //counterTracker = counterNum; 
            //System.out.println("DEBUGGING - counterNum in chooseCounter for Customer " + id + " is " + counterNum);
            waitingAreaCount.decrementAndGet();

            //System.out.println("DEBUGGING - BEFORE RELEASE: " + waitingArea.availablePermits());
            waitingArea.release();
            //System.out.println("DEBUGGING - AFTER RELEASE: " + waitingArea.availablePermits());

        }
        private void handleCashier() {
            // Logic for payment at the cashier
            //System.out.println("DEBUGGING - counterNum in handleCashier for Customer " + id + " is " + counterNum);
            if(counterNum == 0){
                try{
                    cashierQueue.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting cashier queue semaphore in handle cashier");

                }
                counter0MUX.release();
                cashierCount.getAndIncrement();
                System.out.println("Customer " + id + " has moved to the cashier from the Burger Line. Cashier Count: " + cashierCount); 
            }

            else if(counterNum == 1){
                try{
                    cashierQueue.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting cashier queue semaphore in handle cashier");

                }
                counter1MUX.release();
                cashierCount.getAndIncrement();
                System.out.println("Customer " + id + " has moved to the cashier from the Pizza Line");
                // try{
                //     Thread.sleep(random.nextInt(10 * UNIT_OF_TIME));
                // }
                // catch(InterruptedException e){
                //     System.out.println("Could Not sleep thread in handle cashier");
                // }  
            }
            else {
                try{
                    cashierQueue.acquire();
                }
                catch(InterruptedException e){
                    System.out.println("Could not acquire waiting cashier queue semaphore in handle cashier");

                }
                counter2MUX.release();
                cashierCount.getAndIncrement();
                System.out.println("Customer " + id + " has moved to the cashier from the Wings Line");
                // try{
                //     Thread.sleep(random.nextInt(10 * UNIT_OF_TIME));
                // }
                // catch(InterruptedException e){
                //     System.out.println("Could Not sleep thread in handle cashier");
                // }
            }
            try{
                Thread.sleep(random.nextInt(10 * UNIT_OF_TIME));
            }
            catch(InterruptedException e){
                System.out.println("Could Not sleep thread in handle cashier");
            }
            cashierQueue.release();
            cashierCount.decrementAndGet();
            System.out.println("Customer " + id + " has moved to paid, eaten their meal, then left");
        }
    }
}