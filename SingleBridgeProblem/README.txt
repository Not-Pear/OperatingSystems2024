Student Name: Shane Norden
Student ID: 011008524
Class: CSCE 36103 - OPERATING SYSTEMS
Assignment: HW6: Single-lane Bridge Simulator programming assignment

How My Program Works:
To start off, I had found that for majority of cases, the runtime given as an argument was too short given the constraints of the assignment. Trucks would take 3 seconds or 
3000 milliseconds and cars would take 2 seconds or 2000 milliseconds. Given that there are 25 cars and trucks with queue max of 10 for two directions, this would easily take 
over a minute. In order to give the threads more time to cross and fully test the queue and semaphores, I had increased the time by a factor of 4 in the runtime, but when determining 
when to interrupt the time was kept the same. 

From there I created generateVehicles().Seeing as the instructions required there to be 25 trucks and 25 cars, I ran a loop 50 + emergencyVehicle times. From there, an if statement 
was immediately used. This if statement kept track of truckCounter and carCounter. It would pick a number 0 - 10. 0-4 would be a car, 5-9 would be a truck, and 10 would be emergency 
vehicles. If the max amount of cars or trucks had occurred, it would only make vehicle types of the opposite type as well as emergency vehicles. I did this in order to try to maximize 
the randomness of traffic. From there, the vehicle would be scheduled to a thread with a random delay to simulate the vehicles coming in at random times. 

Once that was done, I created the excessive wait time case. The way this was done was getting the number of available permits for that direction semaphore and checking if it was zero and
if the vehicle was not an emergency vehicle. If it was zero, then that meant that the queue was full and thus the vehicle would turn around in frustration. 

From there, the bridge management was created. The first thing before any permits or semaphores were acquired was checking if there was a emergency vehicle. If there was a emergency vehicle, it would take the current thread and sleep it, effectively taking in the next thread. If that thread wasn't an emergency vehicle then that thread would be slept too. This continued until the current thread was an emergency vehicle. Once the emergency vehicle had crossed, the threads that were slept would be no longer slept. This would keep the queue the same length as well as the same order as all the threads were slept the same amount. The bridge semaphore acted as a MUX as it only had one permit. Since all of the threads were trying to access the bridge with the acquire command, it would put them in a queue which allowed for FIFO way of handling the vehicles and each direction got an equal opportunity to cross. Once the bridge was acquired, the thread would be sleep for the according amount of time for its vehicle type, then it would print that it crossed which would release the semaphore and give it to the next thread that tried to acquire it first. The amount of time it took to cross the bridge, or how long the thread was slept, was determined by the getCrossingTime() function which would return 1000ms for Emergency vehicles, 2000ms for cars, and 3000ms for trucks. Once the simulation time ran out and all the vehicles a queue had crossed, I printed out a statement which included the total number of vehicles, emergency vehicles, cars, trucks, and number of vehicles actually crossed. One small quirk I included in my code was that when an emergency vehicle was present but another thread was already running, I did not interrupt that thread. The way I saw it was that vehicle should clear the bridge first then allow the emergency vehicle to pass as it makes no sense for the vehicle to stop in the middle of the road. 