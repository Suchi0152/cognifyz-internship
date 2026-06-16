import java.util.ArrayList;
import java.util.Scanner;

/**
 * Prime Number Pyramid
 * Cognifyz Internship - Level 1 Task 2
 * Java Version
 * 
 * A pyramid pattern made entirely of prime numbers.
 * Each row contains consecutive primes with pyramid alignment.
 */
public class PrimeNumberPyramid {
    
    /**
     * Check if a number is prime
     * Prime Checking Logic:
     * IF N < 2 THEN return False
     * FOR i = 2 to square root of N
     *   IF N % i == 0 THEN return False
     * RETURN True
     */
    public static boolean isPrime(int num) {
        // IF N < 2 THEN return False
        if (num < 2) {
            return false;
        }
        
        // FOR i = 2 to square root of N
        for (int i = 2; i <= Math.sqrt(num); i++) {
            // IF N % i == 0 THEN return False
            if (num % i == 0) {
                return false;
            }
        }
        
        // RETURN True
        return true;
    }
    
    /**
     * Generate the prime number pyramid
     * 
     * START
     *   Input number of rows (say 5)
     *   Initialize prime counter = 2
     *   Initialize list to store primes found
     *   
     *   FOR row = 1 to rows
     *     WHILE count of primes in this row < row
     *       Check if current number is prime
     *       IF prime THEN add to list
     *       Increment current number
     *     END WHILE
     *     
     *     Print spaces for alignment
     *     Print primes for this row
     *   END FOR
     * END
     */
    public static void generatePrimePyramid(int rows) {
        System.out.println("=".repeat(50));
        System.out.println("🔺 PRIME NUMBER PYRAMID 🔺");
        System.out.println("=".repeat(50));
        System.out.println("Generating pyramid with " + rows + " rows:\n");
        
        // Initialize prime counter = 2
        int currentNumber = 2;
        
        // Initialize list to store primes found
        ArrayList<Integer> primesList = new ArrayList<>();
        
        // Calculate total primes needed: sum of 1 to rows = rows*(rows+1)//2
        int totalPrimesNeeded = rows * (rows + 1) / 2;
        
        // WHILE count of primes in list < total needed
        while (primesList.size() < totalPrimesNeeded) {
            // Check if current number is prime
            if (isPrime(currentNumber)) {
                // IF prime THEN add to list
                primesList.add(currentNumber);
            }
            // Increment current number
            currentNumber++;
        }
        
        // FOR row = 1 to rows
        int primeIndex = 0;
        for (int row = 1; row <= rows; row++) {
            
            // Print spaces for alignment (pyramid shape)
            // Number of spaces decreases as row increases
            for (int space = 1; space <= rows - row; space++) {
                System.out.print("   ");
            }
            
            // Print primes for this row
            // Each row 'row' has exactly 'row' numbers
            for (int col = 1; col <= row; col++) {
                System.out.printf("%-3d ", primesList.get(primeIndex));
                primeIndex++;
            }
            
            // Move to next line after finishing current row
            System.out.println();
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("✅ Pyramid generated successfully!");
    }
    
    /**
     * Generate pyramid and return as String (for display or file)
     */
    public static String generatePrimePyramidString(int rows) {
        StringBuilder output = new StringBuilder();
        
        output.append("=".repeat(50)).append("\n");
        output.append("🔺 PRIME NUMBER PYRAMID 🔺\n");
        output.append("=".repeat(50)).append("\n");
        output.append("Generating pyramid with ").append(rows).append(" rows:\n\n");
        
        int currentNumber = 2;
        ArrayList<Integer> primesList = new ArrayList<>();
        int totalPrimesNeeded = rows * (rows + 1) / 2;
        
        while (primesList.size() < totalPrimesNeeded) {
            if (isPrime(currentNumber)) {
                primesList.add(currentNumber);
            }
            currentNumber++;
        }
        
        int primeIndex = 0;
        for (int row = 1; row <= rows; row++) {
            for (int space = 1; space <= rows - row; space++) {
                output.append("   ");
            }
            for (int col = 1; col <= row; col++) {
                output.append(String.format("%-3d ", primesList.get(primeIndex)));
                primeIndex++;
            }
            output.append("\n");
        }
        
        output.append("\n").append("=".repeat(50)).append("\n");
        output.append("✅ Pyramid generated successfully!\n");
        
        return output.toString();
    }
    
    /**
     * Interactive menu
     */
    public static void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🔺 PRIME NUMBER PYRAMID GENERATOR 🔺");
            System.out.println("=".repeat(50));
            System.out.println("1. Generate Pyramid (Console Output)");
            System.out.println("2. Generate Pyramid (String Output)");
            System.out.println("3. Generate Multiple Pyramids (1-10)");
            System.out.println("4. Exit");
            System.out.println("=".repeat(50));
            
            System.out.print("\nEnter your choice (1-4): ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter number of rows (1-10): ");
                    try {
                        int rows = Integer.parseInt(scanner.nextLine());
                        if (rows >= 1 && rows <= 10) {
                            generatePrimePyramid(rows);
                        } else {
                            System.out.println("❌ Please enter a number between 1 and 10");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid input! Please enter a number.");
                    }
                }
                
                case "2" -> {
                    System.out.print("Enter number of rows (1-10): ");
                    try {
                        int rows = Integer.parseInt(scanner.nextLine());
                        if (rows >= 1 && rows <= 10) {
                            String result = generatePrimePyramidString(rows);
                            System.out.println(result);
                        } else {
                            System.out.println("❌ Please enter a number between 1 and 10");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid input! Please enter a number.");
                    }
                }
                
                case "3" -> {
                    System.out.println("\n📊 GENERATING PYRAMIDS (1-10 rows)");
                    System.out.println("=".repeat(50));
                    for (int i = 1; i <= 10; i++) {
                        System.out.println("\n🔹 " + i + " row(s):");
                        System.out.println(generatePrimePyramidString(i));
                    }
                }
                
                case "4" -> {
                    System.out.println("\n👋 Thank you for using Prime Number Pyramid Generator!");
                    System.out.println("Goodbye! 🔺");
                    scanner.close();
                    return;
                }
                
                default -> System.out.println("❌ Invalid choice! Please enter 1-4");
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    /**
     * Main method - Entry point
     */
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌟 WELCOME TO PRIME NUMBER PYRAMID GENERATOR 🌟");
        System.out.println("=".repeat(60));
        System.out.println("\n📌 Generate beautiful pyramid patterns using prime numbers!");
        System.out.println("📌 Prime numbers are numbers greater than 1 with no divisors other than 1 and itself.");
        System.out.println("📌 Features:");
        System.out.println("   • Generate pyramids with 1-10 rows");
        System.out.println("   • Multiple output formats");
        System.out.println("   • Batch generation (1-10 rows at once)");
        
        interactiveMode();
    }
}