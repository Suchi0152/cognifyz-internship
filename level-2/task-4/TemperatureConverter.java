import java.util.Scanner;

/**
 * Temperature Converter
 * Level 2 Task 4 for Cognifyz Internship
 * Java Version
 * 
 * Features: Convert between Celsius and Fahrenheit
 *           Weather context, batch conversion
 */
public class TemperatureConverter {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    // ==============================================
    // CONVERSION FORMULAS
    // ==============================================
    
    /**
     * Convert Celsius to Fahrenheit
     * Formula: °F = (°C × 9/5) + 32
     */
    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32;
    }
    
    /**
     * Convert Fahrenheit to Celsius
     * Formula: °C = (°F - 32) × 5/9
     */
    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }
    
    // ==============================================
    // WEATHER CONTEXT
    // ==============================================
    
    /**
     * Get weather context based on temperature
     */
    public static String getWeatherContext(double temp, char unit) {
        if (unit == 'C' || unit == 'c') {
            if (temp <= 0) return "❄️ Freezing cold! Wear heavy winter clothes.";
            if (temp <= 10) return "🧥 Very cold! Wear a warm jacket.";
            if (temp <= 20) return "🍂 Cool weather. A light jacket might help.";
            if (temp <= 30) return "☀️ Pleasant weather. Comfortable temperature!";
            if (temp <= 40) return "🔥 Hot weather! Stay hydrated.";
            return "🥵 Extremely hot! Avoid going outside.";
        } else {
            if (temp <= 32) return "❄️ Freezing cold! Wear heavy winter clothes.";
            if (temp <= 50) return "🧥 Very cold! Wear a warm jacket.";
            if (temp <= 68) return "🍂 Cool weather. A light jacket might help.";
            if (temp <= 86) return "☀️ Pleasant weather. Comfortable temperature!";
            if (temp <= 104) return "🔥 Hot weather! Stay hydrated.";
            return "🥵 Extremely hot! Avoid going outside.";
        }
    }
    
    // ==============================================
    // SINGLE CONVERSION
    // ==============================================
    
    public static void singleConversion() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌡️  TEMPERATURE CONVERTER");
        System.out.println("=".repeat(50));
        System.out.println("1. Celsius to Fahrenheit (°C → °F)");
        System.out.println("2. Fahrenheit to Celsius (°F → °C)");
        System.out.println("3. Back to Main Menu");
        System.out.println("=".repeat(50));
        
        System.out.print("\nEnter your choice (1-3): ");
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1" -> convertCelsiusToFahrenheit();
            case "2" -> convertFahrenheitToCelsius();
            case "3" -> { return; }
            default -> System.out.println("❌ Invalid choice! Please enter 1-3");
        }
    }
    
    public static void convertCelsiusToFahrenheit() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌡️  CELSIUS TO FAHRENHEIT CONVERTER");
        System.out.println("=".repeat(50));
        
        double celsius = getTemperatureInput("Enter temperature in Celsius: ");
        
        double fahrenheit = celsiusToFahrenheit(celsius);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔄 CONVERSION RESULT");
        System.out.println("=".repeat(50));
        System.out.printf("📝 Input: %.2f°C%n", celsius);
        System.out.printf("✨ Output: %.2f°F%n", fahrenheit);
        System.out.println("=".repeat(50));
        
        System.out.printf("%n📐 Formula used: (°C × 9/5) + 32%n");
        System.out.printf("   (%.2f × 9/5) + 32 = %.2f°F%n", celsius, fahrenheit);
        
        System.out.printf("%n🌍 WEATHER CONTEXT:%n");
        System.out.println("   " + getWeatherContext(celsius, 'C'));
    }
    
    public static void convertFahrenheitToCelsius() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌡️  FAHRENHEIT TO CELSIUS CONVERTER");
        System.out.println("=".repeat(50));
        
        double fahrenheit = getTemperatureInput("Enter temperature in Fahrenheit: ");
        
        double celsius = fahrenheitToCelsius(fahrenheit);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔄 CONVERSION RESULT");
        System.out.println("=".repeat(50));
        System.out.printf("📝 Input: %.2f°F%n", fahrenheit);
        System.out.printf("✨ Output: %.2f°C%n", celsius);
        System.out.println("=".repeat(50));
        
        System.out.printf("%n📐 Formula used: (°F - 32) × 5/9%n");
        System.out.printf("   (%.2f - 32) × 5/9 = %.2f°C%n", fahrenheit, celsius);
        
        System.out.printf("%n🌍 WEATHER CONTEXT:%n");
        System.out.println("   " + getWeatherContext(fahrenheit, 'F'));
    }
    
    // ==============================================
    // BATCH CONVERSION
    // ==============================================
    
    public static void batchConversion() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📦 BATCH TEMPERATURE CONVERSION");
        System.out.println("=".repeat(50));
        System.out.println("Convert multiple temperatures at once!\n");
        
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.println("3. Back to Main Menu");
        System.out.println("=".repeat(50));
        
        System.out.print("\nEnter your choice (1-3): ");
        String choice = scanner.nextLine();
        
        if (choice.equals("3")) return;
        
        System.out.println("\nEnter temperatures separated by commas (e.g., 0, 25, 100, -10)");
        System.out.print("Temperatures: ");
        String input = scanner.nextLine();
        
        try {
            String[] tempStrings = input.split(",");
            double[] temps = new double[tempStrings.length];
            for (int i = 0; i < tempStrings.length; i++) {
                temps[i] = Double.parseDouble(tempStrings[i].trim());
            }
            
            System.out.println("\n" + "=".repeat(60));
            if (choice.equals("1")) {
                System.out.println("📊 BATCH CONVERSION: Celsius → Fahrenheit");
                System.out.printf("%-15s | %-15s%n", "Celsius (°C)", "Fahrenheit (°F)");
            } else if (choice.equals("2")) {
                System.out.println("📊 BATCH CONVERSION: Fahrenheit → Celsius");
                System.out.printf("%-15s | %-15s%n", "Fahrenheit (°F)", "Celsius (°C)");
            } else {
                System.out.println("❌ Invalid choice!");
                return;
            }
            System.out.println("-".repeat(32));
            
            for (double temp : temps) {
                if (choice.equals("1")) {
                    double result = celsiusToFahrenheit(temp);
                    System.out.printf("%-15.2f | %-15.2f%n", temp, result);
                } else if (choice.equals("2")) {
                    double result = fahrenheitToCelsius(temp);
                    System.out.printf("%-15.2f | %-15.2f%n", temp, result);
                }
            }
            System.out.println("=".repeat(60));
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter numbers separated by commas.");
        }
    }
    
    // ==============================================
    // TEMPERATURE COMPARISON
    // ==============================================
    
    public static void compareTemperatures() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔍 TEMPERATURE COMPARISON TOOL");
        System.out.println("=".repeat(50));
        System.out.println("Compare two temperatures (can be in different units)\n");
        
        // First temperature
        System.out.println("📊 FIRST TEMPERATURE:");
        char unit1 = getUnitInput();
        double temp1 = getTemperatureInput("Enter temperature: ");
        
        // Second temperature
        System.out.println("\n📊 SECOND TEMPERATURE:");
        char unit2 = getUnitInput();
        double temp2 = getTemperatureInput("Enter temperature: ");
        
        // Convert both to Celsius for comparison
        double temp1C = (unit1 == 'F' || unit1 == 'f') ? fahrenheitToCelsius(temp1) : temp1;
        double temp2C = (unit2 == 'F' || unit2 == 'f') ? fahrenheitToCelsius(temp2) : temp2;
        
        // Compare
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 COMPARISON RESULT");
        System.out.println("=".repeat(50));
        
        double difference = Math.abs(temp1C - temp2C);
        
        if (temp1C > temp2C) {
            System.out.printf("✅ %.2f°%c is HOTTER than %.2f°%c%n", temp1, unit1, temp2, unit2);
            System.out.printf("   Difference: %.2f°C%n", difference);
        } else if (temp1C < temp2C) {
            System.out.printf("✅ %.2f°%c is HOTTER than %.2f°%c%n", temp2, unit2, temp1, unit1);
            System.out.printf("   Difference: %.2f°C%n", difference);
        } else {
            System.out.printf("✅ %.2f°%c is EQUAL to %.2f°%c%n", temp1, unit1, temp2, unit2);
        }
    }
    
    // ==============================================
    // HELPER METHODS
    // ==============================================
    
    public static double getTemperatureInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number (e.g., 25, 98.6, -10)");
            }
        }
    }
    
    public static char getUnitInput() {
        while (true) {
            System.out.print("Is it Celsius (C) or Fahrenheit (F)? ");
            String input = scanner.nextLine().toUpperCase();
            if (input.length() > 0 && (input.charAt(0) == 'C' || input.charAt(0) == 'F')) {
                return input.charAt(0);
            }
            System.out.println("❌ Invalid input! Please enter 'C' for Celsius or 'F' for Fahrenheit");
        }
    }
    
    // ==============================================
    // REFERENCE TABLE
    // ==============================================
    
    public static void showReferenceTable() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 QUICK REFERENCE - COMMON TEMPERATURES");
        System.out.println("=".repeat(50));
        System.out.printf("%-15s | %-15s%n", "Celsius (°C)", "Fahrenheit (°F)");
        System.out.println("-".repeat(32));
        System.out.printf("%-15s | %-15s%n", "0°C (Freezing)", "32°F");
        System.out.printf("%-15s | %-15s%n", "10°C", "50°F");
        System.out.printf("%-15s | %-15s%n", "20°C", "68°F");
        System.out.printf("%-15s | %-15s%n", "25°C (Room Temp)", "77°F");
        System.out.printf("%-15s | %-15s%n", "30°C", "86°F");
        System.out.printf("%-15s | %-15s%n", "37°C (Body Temp)", "98.6°F");
        System.out.printf("%-15s | %-15s%n", "40°C", "104°F");
        System.out.printf("%-15s | %-15s%n", "100°C (Boiling)", "212°F");
        System.out.println("=".repeat(50));
    }
    
    // ==============================================
    // TEST FUNCTION
    // ==============================================
    
    public static void runTests() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 RUNNING TEST CASES");
        System.out.println("=".repeat(60));
        
        // Test 1: 0°C to Fahrenheit
        double test1 = celsiusToFahrenheit(0);
        System.out.printf("Test 1: 0°C → %.2f°F (Expected: 32.00°F) %s%n", 
            test1, Math.abs(test1 - 32) < 0.01 ? "✅ PASSED" : "❌ FAILED");
        
        // Test 2: 100°C to Fahrenheit
        double test2 = celsiusToFahrenheit(100);
        System.out.printf("Test 2: 100°C → %.2f°F (Expected: 212.00°F) %s%n", 
            test2, Math.abs(test2 - 212) < 0.01 ? "✅ PASSED" : "❌ FAILED");
        
        // Test 3: 32°F to Celsius
        double test3 = fahrenheitToCelsius(32);
        System.out.printf("Test 3: 32°F → %.2f°C (Expected: 0.00°C) %s%n", 
            test3, Math.abs(test3 - 0) < 0.01 ? "✅ PASSED" : "❌ FAILED");
        
        // Test 4: 98.6°F to Celsius
        double test4 = fahrenheitToCelsius(98.6);
        System.out.printf("Test 4: 98.6°F → %.2f°C (Expected: 37.00°C) %s%n", 
            test4, Math.abs(test4 - 37) < 0.01 ? "✅ PASSED" : "❌ FAILED");
        
        // Test 5: -40°C to Fahrenheit
        double test5 = celsiusToFahrenheit(-40);
        System.out.printf("Test 5: -40°C → %.2f°F (Expected: -40.00°F) %s%n", 
            test5, Math.abs(test5 + 40) < 0.01 ? "✅ PASSED" : "❌ FAILED");
        
        // Test 6: 25°C to Fahrenheit
        double test6 = celsiusToFahrenheit(25);
        System.out.printf("Test 6: 25°C → %.2f°F (Expected: 77.00°F) %s%n", 
            test6, Math.abs(test6 - 77) < 0.01 ? "✅ PASSED" : "❌ FAILED");
        
        System.out.println("=".repeat(60));
        System.out.println("✅ All tests completed!");
    }
    
    // ==============================================
    // MAIN MENU
    // ==============================================
    
    public static void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌡️  TEMPERATURE CONVERTER - MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. 🔄 Single Temperature Conversion");
        System.out.println("2. 📦 Batch Conversion (Multiple Temperatures)");
        System.out.println("3. 🔍 Compare Two Temperatures");
        System.out.println("4. 📊 View Reference Table");
        System.out.println("5. 🧪 Run Tests");
        System.out.println("6. ❌ Exit");
        System.out.println("=".repeat(50));
    }
    
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌟 WELCOME TO THE TEMPERATURE CONVERTER 🌟");
        System.out.println("=".repeat(60));
        System.out.println("Convert temperatures between Celsius and Fahrenheit");
        System.out.println("with additional tools for batch conversion and comparison!\n");
        
        System.out.println("📌 QUICK TEST SCENARIOS:");
        System.out.println("   • Test 1: Convert 0°C → Should get 32°F");
        System.out.println("   • Test 2: Convert 100°C → Should get 212°F");
        System.out.println("   • Test 3: Convert 32°F → Should get 0°C");
        System.out.println("   • Test 4: Convert 98.6°F → Should get 37°C");
        System.out.println("   • Test 5: Convert -40°C → Should get -40°F");
        
        while (true) {
            showMainMenu();
            System.out.print("\nEnter your choice (1-6): ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1" -> singleConversion();
                case "2" -> batchConversion();
                case "3" -> compareTemperatures();
                case "4" -> showReferenceTable();
                case "5" -> runTests();
                case "6" -> {
                    System.out.println("\n👋 Thank you for using Temperature Converter!");
                    System.out.println("   Stay safe and keep measuring! 🌡️");
                    scanner.close();
                    return;
                }
                default -> System.out.println("❌ Invalid choice! Please enter 1-6");
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
}