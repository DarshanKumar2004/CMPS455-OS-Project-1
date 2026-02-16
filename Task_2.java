import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Task_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        int R = getValidNum(scanner, 1, 10000, "How many reading threads should be created? (integer from 1-10000): ");
        int W = getValidNum(scanner, 1, 10000, "How many writer threads should be created? (integer from 1-10000): ");
        int N = getValidNum(scanner, 1, R, "How many readers should be allowed to read at once? (integer from 1-R): ");

        // Shared resource synchronization primitives
        Semaphore writerMutex = new Semaphore(1);
        Semaphore readerSemaphore = new Semaphore(N);

        // Starts reader threads
        for (int i = 0; i < R; i++) {
            new myThreads(writerMutex, readerSemaphore, false, i).start();
        }
        // Starts writer threads
        for (int i = 0; i < W; i++) {
            new myThreads(writerMutex, readerSemaphore, true, i).start();
        }
    }


    private static class myThreads extends Thread {
        Semaphore writerMutex;
        Semaphore readerSemaphore;
        boolean isReader;           // Reader vs Writer ID
        int ID;                     // Reader/Writer Thread ID
        static Semaphore readersTurn = new Semaphore(1);
        static Semaphore writersTurn = new Semaphore(1);

        // Constructor
        public myThreads(Semaphore writerMutex, Semaphore readerSemaphore, boolean isWriter, int ID) {
            this.writerMutex = writerMutex;
            this.readerSemaphore = readerSemaphore;
            this.isReader = isWriter;
            this.ID = ID;
        }
       
        @Override
        public void run() {
            if (isReader) {
                try {
                    readersTurn.acquire();
                    readerSemaphore.acquire();
                    System.out.println("Reader " + ID + " is reading.");
                    Thread.sleep(1000); // Simulate reading time
                    writersTurn.release();
                    readerSemaphore.release();
                } 
                catch (Exception e) {

                }
            }
            else {
                try {
                    writersTurn.acquire();
                    writerMutex.acquire();
                    System.out.println("Writer " + ID + " is writing.");
                    Thread.sleep(1000); // Simulate writing time
                    readersTurn.release();
                    writerMutex.release();
                } 
                catch (Exception e) {

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


