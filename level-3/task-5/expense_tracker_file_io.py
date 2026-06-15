"""
==================================================
EXPENSE TRACKER WITH FILE PERSISTENCE
Level 3 Task 5 for Cognifyz Internship
==================================================
Features: Full CRUD + Save/Load from file
==================================================
"""

import os
import json
from datetime import date

# ==============================================
# FILE CONFIGURATION
# ==============================================

DATA_FILE = "expenses.json"
BACKUP_FILE = "expenses_backup.json"

# ==============================================
# STEP 1: MODIFY APPLICATION FOR FILE I/O
# ==============================================

class Expense:
    """Expense class to represent each financial transaction"""
    
    def __init__(self, expense_id, amount, category, date_str, description):
        self.id = expense_id
        self.amount = amount
        self.category = category
        self.date = date_str
        self.description = description
    
    def display(self):
        """Display expense details"""
        print(f"ID: {self.id}")
        print(f"Amount: ₹{self.amount:.2f}")
        print(f"Category: {self.category}")
        print(f"Date: {self.date}")
        print(f"Description: {self.description}")
        print("-" * 30)
    
    def to_short_string(self):
        """Return short string for list view"""
        return f"{self.id:3} | ₹{self.amount:8.2f} | {self.category:12} | {self.date} | {self.description[:20]}"
    
    def to_dict(self):
        """Convert Expense object to dictionary for JSON storage"""
        return {
            'id': self.id,
            'amount': self.amount,
            'category': self.category,
            'date': self.date,
            'description': self.description
        }
    
    @staticmethod
    def from_dict(data):
        """Create Expense object from dictionary"""
        return Expense(
            data['id'],
            data['amount'],
            data['category'],
            data['date'],
            data['description']
        )


# ==============================================
# FILE OPERATIONS WITH ERROR HANDLING
# ==============================================

def save_expenses_to_file(expenses_list):
    """
    Save all expenses to a JSON file
    Step 1: Write tasks to text file
    """
    try:
        # Convert expenses to dictionary list
        expenses_data = [expense.to_dict() for expense in expenses_list]
        
        # Write to file with proper formatting
        with open(DATA_FILE, 'w') as file:
            json.dump(expenses_data, file, indent=4)
        
        print(f"✅ Data saved successfully to '{DATA_FILE}'")
        return True
        
    except PermissionError:
        print(f"❌ Error: Permission denied. Cannot write to '{DATA_FILE}'")
        return False
    except Exception as e:
        print(f"❌ Error saving file: {e}")
        return False


def load_expenses_from_file():
    """
    Load expenses from JSON file
    Step 1: Read tasks from text file
    Returns: List of Expense objects
    """
    if not os.path.exists(DATA_FILE):
        print(f"📭 No existing data file found. Starting fresh.")
        return [], 1  # Return empty list and starting ID
    
    try:
        with open(DATA_FILE, 'r') as file:
            expenses_data = json.load(file)
        
        # Convert dictionaries back to Expense objects
        expenses_list = [Expense.from_dict(data) for data in expenses_data]
        
        # Calculate next available ID
        next_id = max([expense.id for expense in expenses_list], default=0) + 1
        
        print(f"✅ Loaded {len(expenses_list)} expenses from '{DATA_FILE}'")
        return expenses_list, next_id
        
    except FileNotFoundError:
        print(f"📭 File '{DATA_FILE}' not found. Starting fresh.")
        return [], 1
    except json.JSONDecodeError:
        print(f"❌ Error: '{DATA_FILE}' is corrupted. Starting fresh.")
        print("   Creating backup of corrupted file...")
        create_backup()
        return [], 1
    except Exception as e:
        print(f"❌ Error loading file: {e}")
        return [], 1


def create_backup():
    """Create backup of existing data file (Step 2: Error handling)"""
    if os.path.exists(DATA_FILE):
        try:
            import shutil
            shutil.copy(DATA_FILE, BACKUP_FILE)
            print(f"✅ Backup created as '{BACKUP_FILE}'")
        except Exception as e:
            print(f"⚠️ Could not create backup: {e}")


def restore_from_backup():
    """Restore data from backup file (Step 2: Error handling)"""
    if os.path.exists(BACKUP_FILE):
        try:
            with open(BACKUP_FILE, 'r') as file:
                expenses_data = json.load(file)
            
            # Convert to Expense objects
            expenses_list = [Expense.from_dict(data) for data in expenses_data]
            next_id = max([expense.id for expense in expenses_list], default=0) + 1
            
            print(f"✅ Restored {len(expenses_list)} expenses from backup")
            return expenses_list, next_id
        except Exception as e:
            print(f"❌ Error restoring backup: {e}")
            return [], 1
    else:
        print("❌ No backup file found")
        return [], 1


# ==============================================
# AUTO-SAVE DECORATOR
# ==============================================

def auto_save(func):
    """Decorator to automatically save after modifications"""
    def wrapper(expenses_list, *args, **kwargs):
        result = func(expenses_list, *args, **kwargs)
        save_expenses_to_file(expenses_list)
        return result
    return wrapper


# ==============================================
# STEP 1 & 2: CRUD OPERATIONS WITH FILE SAVING
# ==============================================

@auto_save
def create_expense(expenses_list, next_id):
    """
    CREATE: Add new expense and auto-save to file
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
    
    # Get category
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
    
    # Create and add expense
    new_expense = Expense(next_id, amount, category, date_input, description)
    expenses_list.append(new_expense)
    
    print(f"\n✅ Expense added successfully! (ID: {next_id})")
    print("💾 Data automatically saved to file")
    return next_id + 1


def display_all_expenses(expenses_list):
    """READ: Display all expenses"""
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
    total = sum(expense.amount for expense in expenses_list)
    print(f"📊 TOTAL EXPENSES: ₹{total:.2f}")
    print("=" * 70)


def display_expense_by_id(expenses_list):
    """READ: Display single expense by ID"""
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


@auto_save
def update_expense(expenses_list):
    """
    UPDATE: Modify existing expense and auto-save
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
            print("💾 Data automatically saved to file")
            print("\n🔄 Updated details:")
            expense.display()
            return
    
    print(f"❌ Expense with ID {expense_id} not found!")


@auto_save
def delete_expense(expenses_list):
    """
    DELETE: Remove expense and auto-save
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
    
    for i, expense in enumerate(expenses_list):
        if expense.id == expense_id:
            print("\n📌 Expense to delete:")
            expense.display()
            
            confirm = input("\n⚠️ Are you sure you want to delete this expense? (yes/no): ").lower()
            if confirm == 'yes' or confirm == 'y':
                deleted = expenses_list.pop(i)
                print(f"\n✅ Expense ID {expense_id} deleted successfully!")
                print(f"   (Amount: ₹{deleted.amount:.2f}, Category: {deleted.category})")
                print("💾 Data automatically saved to file")
            else:
                print("\n❌ Deletion cancelled!")
            return
    
    print(f"❌ Expense with ID {expense_id} not found!")


# ==============================================
# REPORTING FUNCTIONS
# ==============================================

def show_category_summary(expenses_list):
    """Show spending by category"""
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
    """Show expenses grouped by month"""
    print("\n" + "=" * 50)
    print("📅 MONTHLY EXPENSE REPORT")
    print("=" * 50)
    
    if not expenses_list:
        print("📭 No expenses to report!")
        return
    
    monthly_totals = {}
    for expense in expenses_list:
        month = expense.date[:7]
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
# FILE MANAGEMENT MENU (Step 2: Error Handling)
# ==============================================

def show_file_menu():
    """Display file management options"""
    print("\n" + "=" * 50)
    print("💾 FILE MANAGEMENT")
    print("=" * 50)
    print("1. 📂 Manual Save (Save to file)")
    print("2. 🔄 Restore from Backup")
    print("3. 📁 View File Info")
    print("4. 🗑️ Delete Data File (Start Fresh)")
    print("5. 🔙 Back to Main Menu")
    print("=" * 50)


def view_file_info():
    """Display information about data file"""
    print("\n" + "=" * 50)
    print("📁 FILE INFORMATION")
    print("=" * 50)
    
    if os.path.exists(DATA_FILE):
        size = os.path.getsize(DATA_FILE)
        print(f"✅ File: {DATA_FILE}")
        print(f"📏 Size: {size} bytes")
        print(f"📅 Last Modified: {os.path.getmtime(DATA_FILE)}")
    else:
        print(f"❌ File '{DATA_FILE}' does not exist")
    
    if os.path.exists(BACKUP_FILE):
        size = os.path.getsize(BACKUP_FILE)
        print(f"✅ Backup: {BACKUP_FILE}")
        print(f"📏 Size: {size} bytes")
    else:
        print(f"⚠️ No backup file found")
    
    print("=" * 50)


def delete_data_file(expenses_list):
    """Delete data file and start fresh"""
    print("\n" + "=" * 50)
    print("⚠️ DELETE ALL DATA")
    print("=" * 50)
    
    confirm = input("⚠️ Are you sure you want to delete ALL data? (yes/no): ").lower()
    if confirm == 'yes' or confirm == 'y':
        if os.path.exists(DATA_FILE):
            os.remove(DATA_FILE)
            print(f"✅ File '{DATA_FILE}' deleted")
        
        # Clear current data
        expenses_list.clear()
        print("✅ All expenses cleared from memory")
        print("💡 Program will start fresh on next launch")
        return [], 1
    else:
        print("❌ Deletion cancelled")
        return expenses_list, max([e.id for e in expenses_list], default=0) + 1


# ==============================================
# MAIN MENU (Step 3: Test Persistence)
# ==============================================

def show_menu():
    """Display main menu"""
    print("\n" + "=" * 50)
    print("💰 EXPENSE TRACKER (With File Storage) 💰")
    print("=" * 50)
    print("1. ➕ ADD expense (CREATE)")
    print("2. 📋 VIEW all expenses (READ)")
    print("3. 🔍 VIEW expense by ID (READ)")
    print("4. 📊 CATEGORY summary (REPORT)")
    print("5. 📅 MONTHLY report (REPORT)")
    print("6. ✏️ UPDATE expense (UPDATE)")
    print("7. 🗑️ DELETE expense (DELETE)")
    print("8. 💾 FILE MANAGEMENT")
    print("9. ❌ EXIT")
    print("=" * 50)


def main():
    """
    Main function with file persistence
    Step 3: Test persistence of task data
    """
    print("\n" + "=" * 60)
    print("💾 EXPENSE TRACKER WITH FILE PERSISTENCE 💾")
    print("=" * 60)
    print("Data is automatically saved to 'expenses.json'")
    print("Your expenses will survive program restarts!\n")
    
    # Load existing data from file
    expenses, next_id = load_expenses_from_file()
    
    print(f"\n📊 Current status:")
    print(f"   • Total expenses: {len(expenses)}")
    print(f"   • Next available ID: {next_id}")
    print(f"   • Data file: {DATA_FILE}")
    
    while True:
        show_menu()
        choice = input("\nEnter your choice (1-9): ")
        
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
            # File management submenu
            while True:
                show_file_menu()
                file_choice = input("\nEnter your choice (1-5): ")
                
                if file_choice == '1':
                    save_expenses_to_file(expenses)
                elif file_choice == '2':
                    restored_expenses, restored_id = restore_from_backup()
                    if restored_expenses:
                        expenses.clear()
                        expenses.extend(restored_expenses)
                        next_id = restored_id
                        print("✅ Data restored from backup!")
                elif file_choice == '3':
                    view_file_info()
                elif file_choice == '4':
                    expenses, next_id = delete_data_file(expenses)
                elif file_choice == '5':
                    break
                else:
                    print("❌ Invalid choice!")
                
                input("\nPress Enter to continue...")
        
        elif choice == '9':
            # Final save before exit
            print("\n" + "=" * 50)
            print("💾 Saving data before exit...")
            save_expenses_to_file(expenses)
            
            print("\n👋 Thank you for using Expense Tracker!")
            print(f"📊 FINAL SUMMARY:")
            print(f"   • Total expenses tracked: {len(expenses)}")
            print(f"   • Total amount spent: ₹{sum(e.amount for e in expenses):.2f}")
            print(f"   • Data saved to: {DATA_FILE}")
            print("\n💰 Goodbye! Your data has been saved! 💰")
            break
        
        else:
            print("❌ Invalid choice! Please enter 1-9")
        
        input("\nPress Enter to continue...")


# ==============================================
# RUN THE APPLICATION
# ==============================================

if __name__ == "__main__":
    main()