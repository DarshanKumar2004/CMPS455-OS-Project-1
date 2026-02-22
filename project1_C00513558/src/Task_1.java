import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.Random;

/* Task 1 - Dining Philosophers
 *
 */
public class Task_1 {

    // Variables for number of philosophers,P, and number of meals, M.
    private static final int P;
    private static final int M;

    private static final Semaphore[] chopsticks;
    private static int mealsRemaining;
    private static final Semaphore mealsMutex = new Semaphore(1);
    private static final Lock finishLock = new Lock();

    private static final OrderedStart orderedStart = new OrderedStart();

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
        Barrier arrivalBarrier = new Barrier(P, "All philosophers have arrived.");
        Barrier departureBarrier = new Barrier(P, "All philosophers have finished eating.");

        // Start new thread for each philosopher
        for (int i = 0; i < P; i++) {
            new Thread(new Philosopher(i, arrivalBarrier, departureBarrier)).start();
        }
    }

    static class Philosopher implements Runnable {

        // Variables to track philosopher and barriers
        private final int philosopherId;
        private Barrier arrivalBarrier;
        private Barrier departureBarrier;



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

            // Allow philosophers to enter and sit in order
            try {
                orderedStart.waitArrival(philosopherId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }



            // Do not allow eating until all philosophers have arrived and sat
            try {
                arrivalBarrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Allow philosophers to eat and think until all meals are eaten
            while (true) {


                int left = philosopherId;
                int right = (philosopherId + 1) % P;

                int first = Math.min(left, right);
                int second = Math.max(left, right);

                // If both chopsticks are available, start eating sequence for philosopher
                try {

                    // Check if left chopstick is available for philosopher
                    if (chopsticks[first].availablePermits() > 0) {
                        System.out.println("Philosopher " + philosopherId + "'s left chopstick is available.");
                    } else {
                        System.out.println("Philosopher " + philosopherId + "'s left chopstick is NOT available.");
                    }

                    // Check if right chopstick is available for philosopher
                    if (chopsticks[second].availablePermits() > 0) {
                        System.out.println("Philosopher " + philosopherId + "'s right chopstick is available.");
                    } else {
                        System.out.println("Philosopher " + philosopherId + "'s right chopstick is NOT available.");
                    }

                    chopsticks[first].acquire();
                    chopsticks[second].acquire();


                    mealsMutex.acquire();

                    try {
                        if (mealsRemaining <= 0) {
                            mealsMutex.release();
                            chopsticks[first].release();
                            chopsticks[second].release();
                            break;
                        }
                        mealsRemaining--;
                    } finally {
                        mealsMutex.release();

                    }

                    System.out.println("Philosopher " + philosopherId + " grabs both chopsticks");
                    System.out.println("Philosopher " + philosopherId + " has a pair of chopsticks");
                    System.out.println("Philosopher " + philosopherId + " is eating...");

                    // Wait 3-6 cycles to simulate eating
                    simulateWait();

                    finishLock.lock();

                    try {
                        mealsEaten++;
                        System.out.println("-------Meals eaten: " + mealsEaten + "-------");
                        System.out.println("Philosopher " + philosopherId + " is finished eating.");
                        // Once eating is completed, allow philosopher to put down chopsticks and start thinking
                        chopsticks[first].release();
                        chopsticks[second].release();
                        System.out.println("Philosopher " + philosopherId + " dropped his left chopstick.");
                        System.out.println("Philosopher " + philosopherId + " dropped his second chopstick.");
                        System.out.println("Philosopher " + philosopherId + " is thinking...");
                        if (mealsEaten == M) {
                            break;
                        }
                    } finally {
                        finishLock.unlock();
                        simulateWait();
                    }


                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }


            try {
                departureBarrier.await();
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Philosopher "+philosopherId +" is leaving.");
        }

//            System.out.println("All meals have been eaten.");



    }
}



