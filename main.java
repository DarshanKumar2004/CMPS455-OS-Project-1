/* Team Members: Abigail Boggs (C00513558), Darshan Kumar (C00529580)
 * CMPS 455
 * Project 1 - Task 3
 */

import java.util.Scanner;

// Task 3 - Command Line
public class main {
    public static void main(String[] args) {

        String[] arguments;
        Scanner scanner = new Scanner(System.in);
        String input;

        if (args.length == 0) {

            // Allow user to input program arguments
            System.out.print("Please enter program arguments (-A 1/-A 2): ");

            input = scanner.nextLine();

            arguments = input.split(" ");
        } else {
            arguments = args;
        }

        // Call the corresponding task if input is valid
        // If invalid, repeat request until correct input is entered
        while (true) {

            // Handle case if there are not 2 arguments
            if (arguments.length != 2) {
                System.out.print("Invalid input. Please enter valid program arguments (-A 1/-A 2): ");
                input = scanner.nextLine();
                arguments = input.split(" ");
            } else if (arguments[0].equals("-A")) {

                // Call task 1
                if (arguments[1].equals("1")) {

                    System.out.println("\n******* Starting Task 1: Dining Philosophers (Semaphores) *******");
                    callTask1();
                    break;
                }

                // Call task 2
                else if (arguments[1].equals("2")) {
                    System.out.println("\n******* Starting Task 2: Readers-Writer Problem (Semaphores) *******\n");
                    callTask2();
                    break;
                }

                // Handle case if second argument is invalid
                System.out.print("Invalid second argument. Please enter valid program arguments (-A 1/-A 2): ");
                input = scanner.nextLine();
                arguments = input.split(" ");
            } else {

                // Handle case if first argument is invalid
                System.out.print("Invalid first argument. Please enter valid program arguments (-A 1/-A 2): ");
                input = scanner.nextLine();
                arguments = input.split(" ");
            }
        }
        scanner.close();
    }

    public static void callTask1() {
        // Call Task_1's main method directly
        try {
            Task_1.dining_philosophers(new String[]{});
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void callTask2() {
        // Call Task_2's main method directly
        Task_2.readers_writer(new String[]{});
    }  
}
