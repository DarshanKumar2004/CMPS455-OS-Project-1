import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        
        
        if (args.length > 0 && args.length < 3) {
            if (args[0].equals("-A")) {
                if (args[1].equals("1")) {
                    System.out.println("Starting Task 1: Dining Philosophers (Semaphores)");
                    callTask1();
                    return;
                }
                else if (args[1].equals("2")) {
                    System.out.println("Starting Task 2: Readers-Writer Problem (Semaphores)");
                    callTask2();
                    return;
                }
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Program Arguments: ");
        while (true) { 
            String input = scanner.nextLine();
            System.out.println("\r\n");
            String[] arguments = input.split(" ");
            if (arguments.length > 0 && arguments.length < 3) {
                if (arguments[0].equals("-A")) {
                    if (arguments[1].equals("1")) {
                        System.out.println("Starting Task 1: Dining Philosophers (Semaphores)");
                        callTask1();
                        break;
                    }
                    else if (arguments[1].equals("2")) {
                        System.out.println("Starting Task 2: Readers-Writer Problem (Semaphores)");
                        callTask2();
                        break;
                    }
                    else {
                        System.out.println("Invalid input. Please enter valid program arguments. i.e. \"-A 1\" or \"-A 2\"");
                    }
                }
                else {
                    System.out.println("Invalid input. Please enter valid program arguments. i.e. \"-A 1\" or \"-A 2\"");
                }
            }
            else {
                System.out.println("Invalid input. Please enter valid program arguments. i.e. \"-A 1\" or \"-A 2\"");
            }
        }
        scanner.close();
    }


    public static void callTask1() {
        // Call Task_1's main method directly
        Task_1.main(new String[]{});
    }

    public static void callTask2() {
        // Call Task_2's main method directly
        Task_2.main(new String[]{});
    }  
}
