/* Team Members: Abigail Boggs (C00513558), Darshan Kumar (C00529580)
 * CMPS 455
 * Project 1 - Task 2
 */

import java.util.Scanner;
import java.util.concurrent.Semaphore;

// Task 2 - Readers-Writer Problem
public class Task_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        int R = getValidNum(scanner, 1, 10000, "How many reading threads should be created? (integer from 1-10000): ");
        int W = getValidNum(scanner, 1, 10000, "How many writer threads should be created? (integer from 1-10000): ");
        int N = getValidNum(scanner, 1, R, "How many readers should be allowed to read at once? (integer from 1-" + R + "): ");

        // Shared resource synchronization primitives
        Semaphore writersMutex = new Semaphore(0);      // Init readers count
        Semaphore readersSemaphore = new Semaphore(N);          // Init readers count
        
        Semaphore activeReadersCountMutex = new Semaphore(1);

        Semaphore writersLeftToWriteMutex = new Semaphore(1);
        Semaphore readersLeftToReadMutex = new Semaphore(1);

        // Starts reader threads
        for (int i = 0; i < R; i++) {
            if (i == 0) {
                // First reader thread initializes the readersLeftToRead variable to the total number of readers
                new myThreads(readersSemaphore, activeReadersCountMutex, readersLeftToReadMutex, writersMutex, writersLeftToWriteMutex, i, N, R).start();
            }
            else {
                // Subsequent reader threads use the second constructor
                new myThreads(readersSemaphore, activeReadersCountMutex, readersLeftToReadMutex, writersMutex, writersLeftToWriteMutex, i).start();
            }
        }
        // Starts writer threads
        for (int i = 0; i < W; i++) {
            if (i == 0) {
                // First writer thread initializes the writersLeftToWrite variable to the total number of writers
                new myThreads(readersSemaphore, writersMutex, readersLeftToReadMutex, writersLeftToWriteMutex, i, W).start();
            }
            else {
                // Writer threads use the third constructor
                new myThreads(readersSemaphore, writersMutex, readersLeftToReadMutex, writersLeftToWriteMutex, i).start();
            }
        }
    }


    private static class myThreads extends Thread {
        Semaphore writersMutex, readersSemaphore, activeReadersCountMutex, readersLeftToReadMutex, writersLeftToWriteMutex;
        boolean isReader;
        int ID;
        static int activeReadersCount = 0, maxReaderBatchSize, readersLeftToRead, writersLeftToWrite;


        // First readers thread constructor, initializes the readersLeftToRead variable to the total number of readers
        public myThreads(Semaphore readersSemaphore, Semaphore activeReadersCountMutex, Semaphore readersLeftToReadMutex, Semaphore writersMutex, Semaphore writersLeftToWriteMutex, int ID, int maxReaderBatchSize, int totalReaders) {
            // Initialize the thread with the shared synchronization primitives
            this.readersSemaphore = readersSemaphore;
            this.activeReadersCountMutex = activeReadersCountMutex;
            this.readersLeftToReadMutex = readersLeftToReadMutex;
            this.writersLeftToWriteMutex = writersLeftToWriteMutex;
            this.writersMutex = writersMutex;

            // Initialize thread type and ID
            isReader = true;
            this.ID = ID;
            Task_2.myThreads.maxReaderBatchSize = maxReaderBatchSize;   // Access the class static var instead of instances var
            // Increment the count of readers left to read
            Task_2.myThreads.readersLeftToRead = totalReaders;
        }

        // Reader thread constructor
        public myThreads(Semaphore readersSemaphore, Semaphore activeReadersCountMutex, Semaphore readersLeftToReadMutex, Semaphore writersMutex, Semaphore writersLeftToWriteMutex, int ID) {
            // Initialize the thread with the shared synchronization primitives
            this.readersSemaphore = readersSemaphore;
            this.activeReadersCountMutex = activeReadersCountMutex;
            this.readersLeftToReadMutex = readersLeftToReadMutex;
            this.writersLeftToWriteMutex = writersLeftToWriteMutex;
            this.writersMutex = writersMutex;

            // Initialize thread type and ID
            isReader = true;
            this.ID = ID;
        }

        // Writer thread constructor
        public myThreads(Semaphore readersSemaphore, Semaphore writersMutex, Semaphore readersLeftToReadMutex, Semaphore writersLeftToWriteMutex, int ID, int totalWriters) {
            // Initialize the thread with the shared synchronization primitive
            this.readersSemaphore = readersSemaphore;
            this.writersMutex = writersMutex;
            this.readersLeftToReadMutex = readersLeftToReadMutex;
            this.writersLeftToWriteMutex = writersLeftToWriteMutex;

            // Initialize thread type and ID
            isReader = false;
            this.ID = ID;
            Task_2.myThreads.writersLeftToWrite = totalWriters;
        }

        // Writer thread constructor
        public myThreads(Semaphore readersSemaphore, Semaphore writersMutex, Semaphore readersLeftToReadMutex, Semaphore writersLeftToWriteMutex, int ID) {
            // Initialize the thread with the shared synchronization primitives
            this.readersSemaphore = readersSemaphore;
            this.writersMutex = writersMutex;
            this.readersLeftToReadMutex = readersLeftToReadMutex;
            this.writersLeftToWriteMutex = writersLeftToWriteMutex;

            // Initialize thread type and ID
            isReader = false;
            this.ID = ID;
        }
       

        @Override
        public void run() {
            try {
                // Handles reader case
                if (isReader) {
                    // Allow N readers during this readers turn
                    readersSemaphore.acquire();             // Wait until readers turn and/or current readers batch is done
                    activeReadersCountMutex.acquire();
                    activeReadersCount++;
                    activeReadersCountMutex.release();


                    // Aquire permit to read
                    // Simulate reading time
                    System.out.println("-R" + (ID + 1) + " began reading.");
                    Thread.sleep(1000);
                    System.out.println("--R" + (ID + 1) + " finished reading.");


                    boolean isLastReaderInBatch = false, isLastReaderOverall_1 = false;
                    // Decriment active readers count
                    activeReadersCountMutex.acquire();
                    activeReadersCount--;
                    if (activeReadersCount == 0) {
                        isLastReaderInBatch = true;
                    }
                    activeReadersCountMutex.release();
                    // Decriment readers left to read count
                    readersLeftToReadMutex.acquire();
                    readersLeftToRead--;
                    if (readersLeftToRead == 0) {
                        isLastReaderOverall_1 = true;
                    }
                    readersLeftToReadMutex.release();

                    // Are you the last reader in the batch and not the last batch? If so switch it to writers turn.
                    if (isLastReaderInBatch && !isLastReaderOverall_1) {
                        writersMutex.release();
                    }
                }
                // Handles Writer case
                else {
                    writersMutex.acquire();


                    System.out.println("---W" + (ID + 1) + " began writing.");
                    Thread.sleep(1000); // Simulate writing time
                    System.out.println("----W" + (ID + 1) + " finished writing.");


                    writersLeftToWriteMutex.acquire();
                    writersLeftToWrite--;
                    if (writersLeftToWrite > 0) {
                        readersSemaphore.release(maxReaderBatchSize);
                    }
                    writersLeftToWriteMutex.release();
                }

                // Check if you are the last reader/writer to finish, if so switch the turn to the other type of thread
                boolean isLastReaderOverall_2 = false, isLastWriterOverall = false;
                readersLeftToReadMutex.acquire();
                if (readersLeftToRead <= 0) {
                    // No more readers left to read, release the writers turn mutex to allow writers to proceed
                    writersMutex.release();
                    isLastReaderOverall_2 = true;
                }
                readersLeftToReadMutex.release();
                writersLeftToWriteMutex.acquire();
                if (writersLeftToWrite <= 0) {
                    // No more writers left to write, release the readers turn mutex to allow readers to proceed
                    if (!isLastReaderOverall_2) {
                        readersSemaphore.release(maxReaderBatchSize);
                    }
                    isLastWriterOverall = true;
                }
                writersLeftToWriteMutex.release();
                if (isLastReaderOverall_2 && isLastWriterOverall) {
                    System.out.println("\r\nAll writers have finished.");

                    System.out.println("\n******* End of readers-writer problem *******");
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
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


