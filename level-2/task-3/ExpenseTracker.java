import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * EXPENSE TRACKER - CRUD APPLICATION
 * Level 2 Task 3 for Cognifyz Internship
 * Java Version
 * 
 * Features: Create, Read, Update, Delete operations
 *           + Category reports + Monthly reports
 */
public class ExpenseTracker {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // ==============================================
    // STEP 1: DEFINE EXPENSE CLASS WITH ATTRIBUTES
    // ==============================================
    
    static class Expense {
        int id;
        double amount;
        String category;
        String date;
        String description;
        
        public Expense(int id, double amount, String category, String date, String description) {
            this.id = id;
            this.amount = amount;
            this.category = category;
            this.date = date;
            this.description = description;
        }
        
        public void display() {
            System.out.println("ID: " + id);
            System.out.printf("Amount: ₹%.2f%n", amount);
            System.out.println("Category: " + category);
            System.out.println("Date: " + date);
            System.out.println("Description: " + description);
            System.out.println("-".repeat(30));
        }
        
        public String toShortString() {
            return String.format("%3d | ₹%8.2f | %-12s | %s | %s", 
                id, amount, category, date, description.length() > 20 ? 
                description.substring(0, 20) : description);
        }
    }
    
    // ==============================================
    // STEP 2: CREATE FUNCTIONALITY
    // ==============================================
    
    public static int createExpense(List<Expense> expenses, int nextId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("➕ ADD NEW EXPENSE");
        System.out.println("=".repeat(50));
        
        // Get amount with validation
        double amount = 0;
        while (true) {
            try {
                System.out.print("Enter amount (₹): ");
                amount = Double.parseDouble(scanner.nextLine());
                if (amount <= 0) {
                    System.out.println("❌ Amount must be positive. Try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }
        
        // Get category with options
        String[] categories = {"Food", "Transport", "Shopping", "Entertainment", "Bills", "Healthcare", "Other"};
        System.out.println("\nAvailable categories:");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, categories[i]);
        }
        
        String category = "";
        while (true) {
            try {
                System.out.print("Select category (1-7): ");
                int catChoice = Integer.parseInt(scanner.nextLine());
                if (catChoice >= 1 && catChoice <= 7) {
                    category = categories[catChoice - 1];
                    break;
                } else {
                    System.out.println("❌ Please select 1-7");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Enter a number.");
            }
        }
        
        // Get date
        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        String dateInput = scanner.nextLine();
        if (dateInput.isEmpty()) {
            dateInput = LocalDate.now().format(dateFormatter);
        }
        
        // Get description
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "No description";
        }
        
        // Create and add the expense
        Expense newExpense = new Expense(nextId, amount, category, dateInput, description);
        expenses.add(newExpense);
        
        System.out.printf("%n✅ Expense added successfully! (ID: %d)%n", nextId);
        return nextId + 1;
    }
    
    // ==============================================
    // STEP 3: READ FUNCTIONALITY (DISPLAY)
    // ==============================================
    
    public static void displayAllExpenses(List<Expense> expenses) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📋 ALL EXPENSES");
        System.out.println("=".repeat(70));
        
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses found. Add some expenses first!");
            return;
        }
        
        System.out.printf("%-3s | %-8s | %-12s | %-10s | %s%n", "ID", "Amount", "Category", "Date", "Description");
        System.out.println("-".repeat(70));
        
        for (Expense expense : expenses) {
            System.out.println(expense.toShortString());
        }
        
        System.out.println("=".repeat(70));
        
        // Show total
        double total = expenses.stream().mapToDouble(e -> e.amount).sum();
        System.out.printf("📊 TOTAL EXPENSES: ₹%.2f%n", total);
        System.out.println("=".repeat(70));
    }
    
    public static void displayExpenseById(List<Expense> expenses) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔍 FIND EXPENSE BY ID");
        System.out.println("=".repeat(50));
        
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses found!");
            return;
        }
        
        try {
            System.out.print("Enter expense ID: ");
            int expenseId = Integer.parseInt(scanner.nextLine());
            
            for (Expense expense : expenses) {
                if (expense.id == expenseId) {
                    System.out.println("\n💰 EXPENSE DETAILS");
                    System.out.println("=".repeat(40));
                    expense.display();
                    return;
                }
            }
            
            System.out.printf("❌ Expense with ID %d not found!%n", expenseId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }
    
    public static void showCategorySummary(List<Expense> expenses) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 CATEGORY-WISE SUMMARY");
        System.out.println("=".repeat(50));
        
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses to summarize!");
            return;
        }
        
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.category, expense.amount, Double::sum);
        }
        
        System.out.printf("%-15s | %-10s | %s%n", "Category", "Amount", "Percentage");
        System.out.println("-".repeat(40));
        
        double total = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double percentage = (entry.getValue() / total) * 100;
            System.out.printf("%-15s | ₹%8.2f | %5.1f%%%n", entry.getKey(), entry.getValue(), percentage);
        }
        
        System.out.println("-".repeat(40));
        System.out.printf("%-15s | ₹%8.2f | 100.0%%%n", "TOTAL", total);
        System.out.println("=".repeat(50));
    }
    
    public static void showMonthlyReport(List<Expense> expenses) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📅 MONTHLY EXPENSE REPORT");
        System.out.println("=".repeat(50));
        
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses to report!");
            return;
        }
        
        Map<String, Double> monthlyTotals = new TreeMap<>();
        for (Expense expense : expenses) {
            String month = expense.date.substring(0, 7); // Extract YYYY-MM
            monthlyTotals.merge(month, expense.amount, Double::sum);
        }
        
        System.out.printf("%-10s | %-10s | %s%n", "Month", "Amount", "Transactions");
        System.out.println("-".repeat(40));
        
        for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
            long count = expenses.stream().filter(e -> e.date.startsWith(entry.getKey())).count();
            System.out.printf("%-10s | ₹%8.2f | %5d%n", entry.getKey(), entry.getValue(), count);
        }
        
        System.out.println("=".repeat(50));
    }
    
    // ==============================================
    // STEP 4: UPDATE FUNCTIONALITY
    // ==============================================
    
    public static void updateExpense(List<Expense> expenses) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("✏️ UPDATE EXPENSE");
        System.out.println("=".repeat(50));
        
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses to update!");
            return;
        }
        
        try {
            System.out.print("Enter expense ID to update: ");
            int expenseId = Integer.parseInt(scanner.nextLine());
            
            for (Expense expense : expenses) {
                if (expense.id == expenseId) {
                    System.out.println("\n📌 Current details:");
                    expense.display();
                    
                    System.out.println("\n📝 Enter new values (press Enter to keep current value):");
                    
                    // Update amount
                    System.out.printf("New amount (₹%.2f): ", expense.amount);
                    String newAmount = scanner.nextLine();
                    if (!newAmount.isEmpty()) {
                        try {
                            expense.amount = Double.parseDouble(newAmount);
                            System.out.println("✅ Amount updated!");
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid amount! Keeping original.");
                        }
                    }
                    
                    // Update category
                    String[] categories = {"Food", "Transport", "Shopping", "Entertainment", "Bills", "Healthcare", "Other"};
                    System.out.println("\nCategories:");
                    for (int i = 0; i < categories.length; i++) {
                        System.out.printf("  %d. %s%n", i + 1, categories[i]);
                    }
                    
                    System.out.printf("New category (%s): ", expense.category);
                    String newCategory = scanner.nextLine();
                    if (!newCategory.isEmpty()) {
                        try {
                            int catChoice = Integer.parseInt(newCategory);
                            if (catChoice >= 1 && catChoice <= 7) {
                                expense.category = categories[catChoice - 1];
                                System.out.println("✅ Category updated!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid category! Keeping original.");
                        }
                    }
                    
                    // Update date
                    System.out.printf("New date (%s): ", expense.date);
                    String newDate = scanner.nextLine();
                    if (!newDate.isEmpty()) {
                        expense.date = newDate;
                        System.out.println("✅ Date updated!");
                    }
                    
                    // Update description
                    System.out.printf("New description (%s): ", expense.description);
                    String newDesc = scanner.nextLine();
                    if (!newDesc.isEmpty()) {
                        expense.description = newDesc;
                        System.out.println("✅ Description updated!");
                    }
                    
                    System.out.println("\n✅ Expense updated successfully!");
                    System.out.println("\n🔄 Updated details:");
                    expense.display();
                    return;
                }
            }
            
            System.out.printf("❌ Expense with ID %d not found!%n", expenseId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }
    
    // ==============================================
    // STEP 5: DELETE FUNCTIONALITY
    // ==============================================
    
    public static void deleteExpense(List<Expense> expenses) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🗑️ DELETE EXPENSE");
        System.out.println("=".repeat(50));
        
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses to delete!");
            return;
        }
        
        try {
            System.out.print("Enter expense ID to delete: ");
            int expenseId = Integer.parseInt(scanner.nextLine());
            
            Iterator<Expense> iterator = expenses.iterator();
            while (iterator.hasNext()) {
                Expense expense = iterator.next();
                if (expense.id == expenseId) {
                    System.out.println("\n📌 Expense to delete:");
                    expense.display();
                    
                    System.out.print("\n⚠️ Are you sure you want to delete this expense? (yes/no): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    
                    if (confirm.equals("yes") || confirm.equals("y")) {
                        iterator.remove();
                        System.out.printf("%n✅ Expense ID %d deleted successfully!%n", expenseId);
                        System.out.printf("   (Amount: ₹%.2f, Category: %s)%n", expense.amount, expense.category);
                    } else {
                        System.out.println("\n❌ Deletion cancelled!");
                    }
                    return;
                }
            }
            
            System.out.printf("❌ Expense with ID %d not found!%n", expenseId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }
    
    // ==============================================
    // MENU AND MAIN FUNCTION
    // ==============================================
    
    public static void showMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💰 EXPENSE TRACKER 💰");
        System.out.println("=".repeat(50));
        System.out.println("1. ➕ ADD expense (CREATE)");
        System.out.println("2. 📋 VIEW all expenses (READ)");
        System.out.println("3. 🔍 VIEW expense by ID (READ)");
        System.out.println("4. 📊 CATEGORY summary (REPORT)");
        System.out.println("5. 📅 MONTHLY report (REPORT)");
        System.out.println("6. ✏️ UPDATE expense (UPDATE)");
        System.out.println("7. 🗑️ DELETE expense (DELETE)");
        System.out.println("8. ❌ EXIT");
        System.out.println("=".repeat(50));
    }
    
    public static void main(String[] args) {
        List<Expense> expenses = new ArrayList<>();
        int nextId = 1;
        
        // Load sample data for testing
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📌 LOADING SAMPLE DATA FOR TESTING");
        System.out.println("=".repeat(50));
        
        Expense[] sampleExpenses = {
            new Expense(1, 250.00, "Food", "2026-06-10", "Grocery shopping"),
            new Expense(2, 50.00, "Transport", "2026-06-11", "Uber ride"),
            new Expense(3, 500.00, "Shopping", "2026-06-12", "New shoes"),
            new Expense(4, 100.00, "Entertainment", "2026-06-13", "Movie ticket"),
            new Expense(5, 2000.00, "Bills", "2026-06-14", "Electricity bill"),
            new Expense(6, 75.00, "Food", "2026-06-15", "Restaurant dinner"),
            new Expense(7, 30.00, "Transport", "2026-06-16", "Bus fare"),
            new Expense(8, 150.00, "Healthcare", "2026-06-17", "Medicine"),
        };
        
        expenses.addAll(Arrays.asList(sampleExpenses));
        nextId = 9;
        
        System.out.println("✅ Loaded 8 sample expenses for testing");
        System.out.println("   (IDs 1-8 are pre-loaded for you to test CRUD operations)");
        
        // Welcome message
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💸 WELCOME TO EXPENSE TRACKER 💸");
        System.out.println("=".repeat(50));
        System.out.println("Track your spending with full CRUD operations!");
        System.out.println("\n📌 QUICK TEST SCENARIOS:");
        System.out.println("   • Test CREATE: Add a new expense (Option 1)");
        System.out.println("   • Test READ: View all expenses (Option 2)");
        System.out.println("   • Test UPDATE: Modify expense ID 3 (Option 6)");
        System.out.println("   • Test DELETE: Remove expense ID 8 (Option 7)");
        System.out.println("   • Test REPORTS: Check categories & monthly (Options 4 & 5)");
        
        while (true) {
            showMenu();
            System.out.print("\nEnter your choice (1-8): ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1" -> nextId = createExpense(expenses, nextId);
                case "2" -> displayAllExpenses(expenses);
                case "3" -> displayExpenseById(expenses);
                case "4" -> showCategorySummary(expenses);
                case "5" -> showMonthlyReport(expenses);
                case "6" -> updateExpense(expenses);
                case "7" -> deleteExpense(expenses);
                case "8" -> {
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("👋 THANK YOU FOR USING EXPENSE TRACKER!");
                    System.out.println("=".repeat(50));
                    System.out.printf("📊 FINAL SUMMARY:%n");
                    System.out.printf("   • Total expenses tracked: %d%n", expenses.size());
                    System.out.printf("   • Total amount spent: ₹%.2f%n", 
                        expenses.stream().mapToDouble(e -> e.amount).sum());
                    System.out.printf("   • Average expense: ₹%.2f%n", 
                        expenses.stream().mapToDouble(e -> e.amount).average().orElse(0));
                    System.out.println("\n💰 Goodbye! Keep tracking your expenses! 💰");
                    scanner.close();
                    return;
                }
                default -> System.out.println("❌ Invalid choice! Please enter 1-8");
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
}