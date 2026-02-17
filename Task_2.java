import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Task_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        int R = getValidNum(scanner, 1, 10000, "How many reading threads should be created? (integer from 1-10000): ");
        int W = getValidNum(scanner, 1, 10000, "How many writer threads should be created? (integer from 1-10000): ");
        int N = getValidNum(scanner, 1, R, "How many readers should be allowed to read at once? (integer from 1-R): ");

        // Shared resource synchronization primitives
        Semaphore writersMutex = new Semaphore(1);      // Init readers count
        Semaphore readersSemaphore = new Semaphore(N);          // Init readers count
        Semaphore readersTurnMutex = new Semaphore(N);          // init readers turn

        // Starts reader threads
        for (int i = 0; i < R; i++) {
            new myThreads(writersMutex, readersSemaphore, readersTurnMutex, true, i, N).start();
        }
        // Starts writer threads
        for (int i = 0; i < W; i++) {
            new myThreads(writersMutex, readersSemaphore, readersTurnMutex, false, i, N).start();
        }
    }


    private static class myThreads extends Thread {
        Semaphore writersMutex, readersSemaphore, readersTurnMutex;
        boolean isReader;
        int ID;
        static int activeReadersCount = 0, maxReaders;
        // Init readers turn
        static Semaphore activeReadersCountMutex = new Semaphore(1);
        static Semaphore writersTurnMutex = new Semaphore(0);


        // Constructor
        public myThreads(Semaphore writersMutex, Semaphore readersSemaphore, Semaphore readersTurnMutex, boolean isReader, int ID, int maxReaders) {
            this.writersMutex = writersMutex;
            this.readersSemaphore = readersSemaphore;
            this.readersTurnMutex = readersTurnMutex;
            this.isReader = isReader;
            this.ID = ID;
            Task_2.myThreads.maxReaders = maxReaders;   // Access the class static var instead of instances var
        }
       

        @Override
        public void run() {
            if (isReader) {
                try {
                    // Allow N readers during this readers turn
                    /*
                     *   Next reader after max readers shall reset the active 
                     *   reader count and wait for readersTurnMutex to be 
                     *   released by the writer.
                     */ 
                    readersTurnMutex.acquire();             // Wait until readers turn and/or current readers batch is done
                    activeReadersCountMutex.acquire();
                    activeReadersCount++;
                    activeReadersCountMutex.release();


                    // Aquire permit to read
                    readersSemaphore.acquire();
                    // Simulate reading time
                    System.out.println("-R" + (ID + 1) + " began reading.");
                    Thread.sleep(1000);
                    System.out.println("--R" + (ID + 1) + " finished reading.");

                    // Done reading, release readers permit
                    readersSemaphore.release();
                    // Decriment active readers count
                    activeReadersCountMutex.acquire();
                    activeReadersCount--;
                    // Are you the last reader? If so switch it to writers turn.
                    if (activeReadersCount == 0) {
                        writersTurnMutex.release();
                    }
                    activeReadersCountMutex.release();
                } 
                catch (Exception e) {
                    e.printStackTrace();  
                }
            }
            else {
                try {
                    writersTurnMutex.acquire();
                    writersMutex.acquire();
                    System.out.println("---W" + (ID + 1) + " began writing.");
                    Thread.sleep(1000); // Simulate writing time
                    System.out.println("----W" + (ID + 1) + " finished writing.");
                    readersTurnMutex.release(maxReaders);
                    writersMutex.release();
                } 
                catch (Exception e) {
                    e.printStackTrace();  
                }
            }
        }
    }


    private static int getValidNum(Scanner scanner, int min, int max, String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int number = Integer.parseInt(input);
                if (number < min) {
                    System.out.println("\r\n----------------------------------------------------------------");
                    System.out.println("Error: Please enter a number greater than or equal to " + min + ".");
                    System.out.print(prompt);
                }
                else if (number > max) {
                    System.out.println("\r\n----------------------------------------------------------------");
                    System.out.println("Error: Please enter a number less than or equal to " + max + ".");
                    System.out.print(prompt);
                }
                else {     // Exit case
                    System.out.println("This input is acceptable. You entered the number: " + number);
                    return number;
                }
            } 
            catch (NumberFormatException e) {
                System.out.println("\r\n----------------------------------------------------------------");
                System.out.println("Error: Invalid input. Please enter a valid integer between " + min + " and " + max + ".");
                System.out.print(prompt);
            }
        }
    }
}


