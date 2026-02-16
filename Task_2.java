import java.util.Scanner;

public class Task_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        int R = getValidNum(scanner, 1, 10000, "Provide R where R is the number of reading-agents: ");
        int W = getValidNum(scanner, 1, 10000, "Provide W where W is the number of writing-agents: ");
        int N = getValidNum(scanner, 1, R, "Provide N where N is the maximum number of reading-agents that may access the shared resource at once: ");

    }


    public static int getValidNum(Scanner scanner, int min, int max, String prompt) {
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


