/* Team Members: Abigail Boggs (C00513558), Darshan Kumar (C00529580)
 * CMPS 455
 * Project 1 - Task 1
 */

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.Random;

// Task 1 - Dining Philosophers
public class Task_1 {

    // Variables for number of philosophers,P, and number of meals, M.
    private static final int P;
    private static final int M;

    private static final Semaphore[] chopsticks;
    private static int mealsRemaining;
    private static final Semaphore mealsMutex = new Semaphore(1);
    private static final Lock finishLock = new Lock();

    private static int mealsEaten = 0;

    // Receive user input for the values of M and P
    static {

        Scanner input = new Scanner(System.in);

        int tempP;

        while (true) {
            try {
                System.out.print("How many philosophers should be created? (Enter integer between 1-10,000): ");
                tempP = input.nextInt();

                // Handle case for if P is zero or one
                // Repeat request until correct input is entered
                while (tempP < 2) {
                     System.out.println("Too little philosophers. Please create at least 2 philosophers to have a pair of chopsticks.");
                     System.out.print("How many philosophers should be created? (Enter integer between 1-10,000): ");
                     tempP = input.nextInt();
                }

                // Handle case for if P is greater than 10,000
                // Repeat request until correct input is entered
                while (tempP > 10000) {
                    System.out.println("Too many philosophers. Please create less than 10,000 philosophers.");
                    System.out.print("How many philosophers should be created? (Enter integer between 1-10,000): ");
                    tempP = input.nextInt();
                }
                break;

                // Handle case if P is not an integer
                // Repeat request until correct input is entered
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                input.nextLine();
            }
        }

        // Assign P to the valid user input
        P = tempP;

        int tempM;

        while (true) {


            try {
                System.out.print("How many meals should the philosophers eat? (Enter integer between 1-10000): ");
                tempM = input.nextInt();

                // Handle case if M is less than one and greater than 10,000
                // Repeat request until valid input is entered
                while (tempM < 1 || tempM > 10000) {
                    System.out.println("Invalid number of meals.");
                    System.out.print("How many meals should the philosophers eat? (Enter integer between 1-10000): ");
                    tempM = input.nextInt();
                }
                break;

                // Handle case if M is not an integer
                // Repeat request until valid input is entered
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                input.nextLine();
            }
        }

        // Assign M and mealsRemaining to valid user input
        M = tempM;
        mealsRemaining = M;

        // Create chopsticks array of semaphores based on number of philosophers
        chopsticks = new Semaphore[P];
        for (int i = 0; i < P; i++) {
            chopsticks[i] = new Semaphore(1);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // Create barrier until all philosophers have arrived and finished eating
        Barrier arrivalBarrier = new Barrier(P, "--All philosophers have arrived.");
        Barrier departureBarrier = new Barrier(P, "---------All philosophers have finished eating.");

        Thread[] philosophers = new Thread[P];

        // Start new thread for each philosopher
        for (int i = 0; i < P; i++) {
            philosophers[i] = new Thread(new Philosopher(i, arrivalBarrier, departureBarrier));
            System.out.println("-Philosopher " + (i+1) + " starting.");
            philosophers[i].start();

        }

        // Wait for all threads to end
        for (int i = 0; i < P; i++) {
            philosophers[i].join();
        }
    }

    static class Philosopher implements Runnable {

        // Variables to track philosopher and barriers
        private final int philosopherId;
        private final Barrier arrivalBarrier;
        private final Barrier departureBarrier;

        // Create philosopher object
        Philosopher(int philosopherId, Barrier arrivalBarrier, Barrier departureBarrier) throws InterruptedException {
            this.philosopherId = philosopherId;
            this.arrivalBarrier = arrivalBarrier;
            this.departureBarrier = departureBarrier;
        }

        // Method to wait 3-6 cycles after eating or thinking
        private void simulateWait() {
            Random rand = new Random();
            int waitCycles = 3 + rand.nextInt(4);
            for (int j = 0; j < waitCycles; j++) {
                Thread.yield();
            }
        }

        @Override
        public void run() {

            // Do not allow eating until all philosophers have arrived and sat
            try {
                arrivalBarrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Allow philosophers to eat and think until all meals are eaten
            while (true) {

                // Identify left and right chopstick
                // Left chopstick will be picked up first
                int left = philosopherId;
                int right = (philosopherId + 1) % P;

                int first = Math.min(left, right);
                int second = Math.max(left, right);

                // If both chopsticks are available, start eating sequence for philosopher
                try {

                    // If there are no meals, prevent philosopher from eating
                    if (mealsRemaining <= 0) {
                        break;
                    }

                    // Acquire chopsticks
                    if (!chopsticks[first].tryAcquire()) {
                        System.out.println("---Philosopher " + (philosopherId + 1) + "'s left chopstick is NOT available.");
                        chopsticks[first].acquire();
                    }
                    System.out.println("---Philosopher " + (philosopherId + 1) + "'s left chopstick is available.");

                    if (!chopsticks[second].tryAcquire()) {
                        System.out.println("---Philosopher " + (philosopherId + 1) + "'s right chopstick is NOT available.");
                        chopsticks[second].acquire();
                    }
                    System.out.println("---Philosopher " + (philosopherId + 1) + "'s right chopstick is available.");

                    // Semaphore so only one philosopher at a time can change the meal count
                    mealsMutex.acquire();

                    // If there are no meals, prevent philosopher from eating
                    // Philosophers may still pick up chopsticks
                    try {
                        if (mealsRemaining <= 0) {
                            mealsMutex.release();
                            chopsticks[first].release();
                            chopsticks[second].release();
                            break;
                        }

                        // Decrement meals remaining if meals available
                        mealsRemaining--;

                    } finally {
                        mealsMutex.release();

                    }

                    // Notify user if philosopher grabs chopsticks and begins eating
                    System.out.println("----Philosopher " + (philosopherId + 1) + " grabs both chopsticks.");
                    System.out.println("----Philosopher " + (philosopherId + 1) + " has a pair of chopsticks.");
                    System.out.println("-----Philosopher " + (philosopherId + 1) + " is eating...");

                    // Wait 3-6 cycles to simulate eating
                    simulateWait();

                    // Prevent other threads from picking up chopsticks immediately after release and print actions
                    finishLock.lock();

                    try {

                        // Separate count to keep order of meals eaten
                        mealsEaten++;
                        System.out.println("******** Meals eaten: " + mealsEaten + " ********");
                        System.out.println("------Philosopher " + (philosopherId + 1) + " is finished eating.");

                        // Once eating is completed, allow philosopher to put down chopsticks and start thinking
                        chopsticks[first].release();
                        chopsticks[second].release();
                        System.out.println("-------Philosopher " + (philosopherId + 1) + " dropped his left chopstick.");
                        System.out.println("-------Philosopher " + (philosopherId + 1) + " dropped his right chopstick.");
                        System.out.println("--------Philosopher " + (philosopherId + 1) + " is thinking...");

                        // End thinking and eating process for thread if all meals are finished
                        if (mealsEaten == M) {
                            break;
                        }

                        // Allow other threads to pick up chopsticks
                    } finally {
                        finishLock.unlock();

                        // Wait 3-6 cycles to simulate thinking
                        simulateWait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Wait for all philosophers to finish eating/thinking before leaving
            try {
                departureBarrier.await();
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Notify user when philosopher leaves
            System.out.println("----------Philosopher "+ (philosopherId + 1) +" is leaving.");
        }
    }
}