"""
==================================================
EXPENSE TRACKER - CRUD APPLICATION
Level 2 Task 3 for Cognifyz Internship
==================================================
Features: Create, Read, Update, Delete operations
          + Category reports + Monthly reports
==================================================
"""

from datetime import date

# ==============================================
# STEP 1: DEFINE EXPENSE CLASS WITH ATTRIBUTES
# ==============================================

class Expense:
    """Expense class to represent each financial transaction"""
    
    def __init__(self, expense_id, amount, category, date_str, description):
        self.id = expense_id          # Unique ID (auto-generated)
        self.amount = amount          # Amount spent (float)
        self.category = category      # Category (string)
        self.date = date_str          # Date (string format: YYYY-MM-DD)
        self.description = description  # Description (string)
    
    def display(self):
        """Display expense details in formatted way"""
        print(f"ID: {self.id}")
        print(f"Amount: ₹{self.amount:.2f}")
        print(f"Category: {self.category}")
        print(f"Date: {self.date}")
        print(f"Description: {self.description}")
        print("-" * 30)
    
    def to_short_string(self):
        """Return short string representation for list view"""
        return f"{self.id:3} | ₹{self.amount:8.2f} | {self.category:12} | {self.date} | {self.description[:20]}"


# ==============================================
# STEP 2: CREATE FUNCTIONALITY
# ==============================================

def create_expense(expenses_list, next_id):
    """
    CREATE: Add a new expense to the list
    """
    print("\n" + "=" * 50)
    print("➕ ADD NEW EXPENSE")
    print("=" * 50)
    
    # Get amount with validation
    while True:
        try:
            amount = float(input("Enter amount (₹): "))
            if amount <= 0:
                print("❌ Amount must be positive. Try again.")
                continue
            break
        except ValueError:
            print("❌ Invalid input. Please enter a number.")
    
    # Get category with options
    categories = ["Food", "Transport", "Shopping", "Entertainment", "Bills", "Healthcare", "Other"]
    print("\nAvailable categories:")
    for i, cat in enumerate(categories, 1):
        print(f"  {i}. {cat}")
    
    while True:
        try:
            cat_choice = int(input("Select category (1-7): "))
            if 1 <= cat_choice <= 7:
                category = categories[cat_choice - 1]
                break
            else:
                print("❌ Please select 1-7")
        except ValueError:
            print("❌ Invalid input. Enter a number.")
    
    # Get date
    date_input = input("Enter date (YYYY-MM-DD) or press Enter for today: ")
    if date_input == "":
        date_input = date.today().strftime("%Y-%m-%d")
    
    # Get description
    description = input("Enter description: ")
    if description == "":
        description = "No description"
    
    # Create and add the expense
    new_expense = Expense(next_id, amount, category, date_input, description)
    expenses_list.append(new_expense)
    
    print(f"\n✅ Expense added successfully! (ID: {next_id})")
    return next_id + 1


# ==============================================
# STEP 3: READ FUNCTIONALITY (DISPLAY)
# ==============================================

def display_all_expenses(expenses_list):
    """
    READ: Display all expenses in a formatted table
    """
    print("\n" + "=" * 70)
    print("📋 ALL EXPENSES")
    print("=" * 70)
    
    if not expenses_list:
        print("📭 No expenses found. Add some expenses first!")
        return
    
    print(f"{'ID':3} | {'Amount':8} | {'Category':12} | {'Date':10} | {'Description'}")
    print("-" * 70)
    
    for expense in expenses_list:
        print(expense.to_short_string())
    
    print("=" * 70)
    
    # Show total
    total = sum(expense.amount for expense in expenses_list)
    print(f"📊 TOTAL EXPENSES: ₹{total:.2f}")
    print("=" * 70)

def display_expense_by_id(expenses_list):
    """
    READ: Display a single expense by ID
    """
    print("\n" + "=" * 50)
    print("🔍 FIND EXPENSE BY ID")
    print("=" * 50)
    
    if not expenses_list:
        print("📭 No expenses found!")
        return
    
    try:
        expense_id = int(input("Enter expense ID: "))
    except ValueError:
        print("❌ Invalid ID!")
        return
    
    for expense in expenses_list:
        if expense.id == expense_id:
            print("\n💰 EXPENSE DETAILS")
            print("=" * 40)
            expense.display()
            return
    
    print(f"❌ Expense with ID {expense_id} not found!")

def show_category_summary(expenses_list):
    """
    READ: Show spending by category
    """
    print("\n" + "=" * 50)
    print("📊 CATEGORY-WISE SUMMARY")
    print("=" * 50)
    
    if not expenses_list:
        print("📭 No expenses to summarize!")
        return
    
    category_totals = {}
    for expense in expenses_list:
        if expense.category in category_totals:
            category_totals[expense.category] += expense.amount
        else:
            category_totals[expense.category] = expense.amount
    
    print(f"{'Category':15} | {'Amount':10} | {'Percentage'}")
    print("-" * 40)
    
    total = sum(category_totals.values())
    for category, amount in sorted(category_totals.items()):
        percentage = (amount / total) * 100 if total > 0 else 0
        print(f"{category:15} | ₹{amount:8.2f} | {percentage:5.1f}%")
    
    print("-" * 40)
    print(f"{'TOTAL':15} | ₹{total:8.2f} | 100.0%")
    print("=" * 50)

def show_monthly_report(expenses_list):
    """
    READ: Show expenses grouped by month
    """
    print("\n" + "=" * 50)
    print("📅 MONTHLY EXPENSE REPORT")
    print("=" * 50)
    
    if not expenses_list:
        print("📭 No expenses to report!")
        return
    
    monthly_totals = {}
    for expense in expenses_list:
        month = expense.date[:7]  # Extract YYYY-MM from date
        if month in monthly_totals:
            monthly_totals[month] += expense.amount
        else:
            monthly_totals[month] = expense.amount
    
    print(f"{'Month':10} | {'Amount':10} | {'Transactions'}")
    print("-" * 40)
    
    for month in sorted(monthly_totals.keys()):
        count = sum(1 for e in expenses_list if e.date[:7] == month)
        print(f"{month:10} | ₹{monthly_totals[month]:8.2f} | {count:5}")
    
    print("=" * 50)


# ==============================================
# STEP 4: UPDATE FUNCTIONALITY
# ==============================================

def update_expense(expenses_list):
    """
    UPDATE: Modify an existing expense
    """
    print("\n" + "=" * 50)
    print("✏️ UPDATE EXPENSE")
    print("=" * 50)
    
    if not expenses_list:
        print("📭 No expenses to update!")
        return
    
    try:
        expense_id = int(input("Enter expense ID to update: "))
    except ValueError:
        print("❌ Invalid ID!")
        return
    
    # Find the expense
    for expense in expenses_list:
        if expense.id == expense_id:
            print("\n📌 Current details:")
            expense.display()
            
            print("\n📝 Enter new values (press Enter to keep current value):")
            
            # Update amount
            new_amount = input(f"New amount (₹{expense.amount:.2f}): ")
            if new_amount:
                try:
                    expense.amount = float(new_amount)
                    print("✅ Amount updated!")
                except ValueError:
                    print("❌ Invalid amount! Keeping original.")
            
            # Update category
            categories = ["Food", "Transport", "Shopping", "Entertainment", "Bills", "Healthcare", "Other"]
            print("\nCategories:")
            for i, cat in enumerate(categories, 1):
                print(f"  {i}. {cat}")
            
            new_category = input(f"New category ({expense.category}): ")
            if new_category:
                try:
                    cat_choice = int(new_category)
                    if 1 <= cat_choice <= 7:
                        expense.category = categories[cat_choice - 1]
                        print("✅ Category updated!")
                except ValueError:
                    print("❌ Invalid category! Keeping original.")
            
            # Update date
            new_date = input(f"New date ({expense.date}): ")
            if new_date:
                expense.date = new_date
                print("✅ Date updated!")
            
            # Update description
            new_desc = input(f"New description ({expense.description}): ")
            if new_desc:
                expense.description = new_desc
                print("✅ Description updated!")
            
            print("\n✅ Expense updated successfully!")
            print("\n🔄 Updated details:")
            expense.display()
            return
    
    print(f"❌ Expense with ID {expense_id} not found!")


# ==============================================
# STEP 5: DELETE FUNCTIONALITY
# ==============================================

def delete_expense(expenses_list):
    """
    DELETE: Remove an expense from the list
    """
    print("\n" + "=" * 50)
    print("🗑️ DELETE EXPENSE")
    print("=" * 50)
    
    if not expenses_list:
        print("📭 No expenses to delete!")
        return
    
    try:
        expense_id = int(input("Enter expense ID to delete: "))
    except ValueError:
        print("❌ Invalid ID!")
        return
    
    # Find and delete the expense
    for i, expense in enumerate(expenses_list):
        if expense.id == expense_id:
            print("\n📌 Expense to delete:")
            expense.display()
            
            confirm = input("\n⚠️ Are you sure you want to delete this expense? (yes/no): ").lower()
            if confirm == 'yes' or confirm == 'y':
                deleted = expenses_list.pop(i)
                print(f"\n✅ Expense ID {expense_id} deleted successfully!")
                print(f"   (Amount: ₹{deleted.amount:.2f}, Category: {deleted.category})")
            else:
                print("\n❌ Deletion cancelled!")
            return
    
    print(f"❌ Expense with ID {expense_id} not found!")


# ==============================================
# MENU AND MAIN FUNCTION
# ==============================================

def show_menu():
    """Display main menu options"""
    print("\n" + "=" * 50)
    print("💰 EXPENSE TRACKER 💰")
    print("=" * 50)
    print("1. ➕ ADD expense (CREATE)")
    print("2. 📋 VIEW all expenses (READ)")
    print("3. 🔍 VIEW expense by ID (READ)")
    print("4. 📊 CATEGORY summary (REPORT)")
    print("5. 📅 MONTHLY report (REPORT)")
    print("6. ✏️ UPDATE expense (UPDATE)")
    print("7. 🗑️ DELETE expense (DELETE)")
    print("8. ❌ EXIT")
    print("=" * 50)

def main():
    """Main function to run the Expense Tracker"""
    
    # Initialize empty list for expenses
    expenses = []
    next_id = 1
    
    # Load sample data for testing (Step 6: Test scenarios)
    print("\n" + "=" * 50)
    print("📌 LOADING SAMPLE DATA FOR TESTING")
    print("=" * 50)
    
    sample_expenses = [
        Expense(1, 250.00, "Food", "2026-06-10", "Grocery shopping"),
        Expense(2, 50.00, "Transport", "2026-06-11", "Uber ride"),
        Expense(3, 500.00, "Shopping", "2026-06-12", "New shoes"),
        Expense(4, 100.00, "Entertainment", "2026-06-13", "Movie ticket"),
        Expense(5, 2000.00, "Bills", "2026-06-14", "Electricity bill"),
        Expense(6, 75.00, "Food", "2026-06-15", "Restaurant dinner"),
        Expense(7, 30.00, "Transport", "2026-06-16", "Bus fare"),
        Expense(8, 150.00, "Healthcare", "2026-06-17", "Medicine"),
    ]
    expenses.extend(sample_expenses)
    next_id = 9
    
    print("✅ Loaded 8 sample expenses for testing")
    print("   (IDs 1-8 are pre-loaded for you to test CRUD operations)")
    
    # Welcome message
    print("\n" + "=" * 50)
    print("💸 WELCOME TO EXPENSE TRACKER 💸")
    print("=" * 50)
    print("Track your spending with full CRUD operations!")
    print("\n📌 QUICK TEST SCENARIOS:")
    print("   • Test CREATE: Add a new expense (Option 1)")
    print("   • Test READ: View all expenses (Option 2)")
    print("   • Test UPDATE: Modify expense ID 3 (Option 6)")
    print("   • Test DELETE: Remove expense ID 8 (Option 7)")
    print("   • Test REPORTS: Check categories & monthly (Options 4 & 5)")
    
    while True:
        show_menu()
        choice = input("\nEnter your choice (1-8): ")
        
        if choice == '1':
            next_id = create_expense(expenses, next_id)
        
        elif choice == '2':
            display_all_expenses(expenses)
        
        elif choice == '3':
            display_expense_by_id(expenses)
        
        elif choice == '4':
            show_category_summary(expenses)
        
        elif choice == '5':
            show_monthly_report(expenses)
        
        elif choice == '6':
            update_expense(expenses)
        
        elif choice == '7':
            delete_expense(expenses)
        
        elif choice == '8':
            print("\n" + "=" * 50)
            print("👋 THANK YOU FOR USING EXPENSE TRACKER!")
            print("=" * 50)
            print(f"📊 FINAL SUMMARY:")
            print(f"   • Total expenses tracked: {len(expenses)}")
            print(f"   • Total amount spent: ₹{sum(e.amount for e in expenses):.2f}")
            print(f"   • Average expense: ₹{sum(e.amount for e in expenses)/len(expenses) if expenses else 0:.2f}")
            print("\n💰 Goodbye! Keep tracking your expenses! 💰")
            break
        
        else:
            print("❌ Invalid choice! Please enter 1-8")
        
        input("\nPress Enter to continue...")


# ==============================================
# RUN THE APPLICATION
# ==============================================

if __name__ == "__main__":
    main()