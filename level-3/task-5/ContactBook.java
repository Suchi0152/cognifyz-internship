import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * CONTACT BOOK WITH FILE PERSISTENCE
 * Level 3 Task 5 for Cognifyz Internship
 * Java Version
 * 
 * Features: Full CRUD + Save/Load from JSON file
 */
public class ContactBook {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "contacts.json";
    private static final String BACKUP_FILE = "contacts_backup.json";
    
    // ==============================================
    // STEP 1: DEFINE CONTACT CLASS WITH ATTRIBUTES
    // ==============================================
    
    /**
     * Contact class to represent each person
     */
    static class Contact {
        int id;
        String name;
        String phone;
        String email;
        String address;
        String category; // Family, Friends, Work, Other
        
        Contact(int id, String name, String phone, String email, String address, String category) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.category = category;
        }
        
        void display() {
            System.out.println("ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Phone: " + phone);
            System.out.println("Email: " + email);
            System.out.println("Address: " + address);
            System.out.println("Category: " + category);
            System.out.println("-".repeat(30));
        }
        
        String toShortString() {
            return String.format("%3d | %-20s | %-12s | %-25s | %-10s", 
                id, 
                name.length() > 20 ? name.substring(0, 20) : name,
                phone,
                email.length() > 25 ? email.substring(0, 25) : email,
                category);
        }
    }
    
    // ==============================================
    // STEP 1: FILE OPERATIONS (Read/Write)
    // ==============================================
    
    private static String convertToJson(List<Contact> contacts) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            json.append("    {\n");
            json.append("        \"id\": ").append(contact.id).append(",\n");
            json.append("        \"name\": \"").append(escapeJson(contact.name)).append("\",\n");
            json.append("        \"phone\": \"").append(escapeJson(contact.phone)).append("\",\n");
            json.append("        \"email\": \"").append(escapeJson(contact.email)).append("\",\n");
            json.append("        \"address\": \"").append(escapeJson(contact.address)).append("\",\n");
            json.append("        \"category\": \"").append(escapeJson(contact.category)).append("\"\n");
            json.append("    }");
            if (i < contacts.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("]");
        return json.toString();
    }
    
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    private static void saveContactsToFile(List<Contact> contacts) {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            writer.write(convertToJson(contacts));
            System.out.printf("✅ Data saved successfully to '%s'%n", DATA_FILE);
        } catch (IOException e) {
            System.out.printf("❌ Error saving file: %s%n", e.getMessage());
        }
    }
    
    private static List<Contact> loadContactsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("📭 No existing data file found. Starting fresh.");
            return new ArrayList<>();
        }
        
        List<Contact> contacts = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            
            String json = content.toString().trim();
            if (json.isEmpty() || json.equals("[]")) {
                return contacts;
            }
            
            // Parse JSON manually
            contacts = parseJsonToContacts(json);
            System.out.printf("✅ Loaded %d contacts from '%s'%n", contacts.size(), DATA_FILE);
            
        } catch (FileNotFoundException e) {
            System.out.printf("📭 File '%s' not found. Starting fresh.%n", DATA_FILE);
        } catch (IOException e) {
            System.out.printf("❌ Error loading file: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: File is corrupted. Starting fresh.");
            createBackup();
        }
        
        return contacts;
    }
    
    private static List<Contact> parseJsonToContacts(String json) {
        List<Contact> contacts = new ArrayList<>();
        
        // Remove outer brackets
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        if (json.trim().isEmpty()) return contacts;
        
        // Split by object boundaries
        String[] entries = json.split("\\},\\s*\\{");
        
        for (String entry : entries) {
            // Clean up
            entry = entry.replaceAll("[{}]", "").trim();
            String[] fields = entry.split(",");
            
            int id = 0;
            String name = "";
            String phone = "";
            String email = "";
            String address = "";
            String category = "";
            
            for (String field : fields) {
                field = field.trim();
                String[] parts = field.split(":", 2);
                if (parts.length < 2) continue;
                
                String key = parts[0].trim().replaceAll("\"", "");
                String value = parts[1].trim().replaceAll("\"", "");
                
                switch (key) {
                    case "id" -> id = Integer.parseInt(value);
                    case "name" -> name = value;
                    case "phone" -> phone = value;
                    case "email" -> email = value;
                    case "address" -> address = value;
                    case "category" -> category = value;
                }
            }
            
            contacts.add(new Contact(id, name, phone, email, address, category));
        }
        
        return contacts;
    }
    
    private static int getNextId(List<Contact> contacts) {
        return contacts.stream().mapToInt(c -> c.id).max().orElse(0) + 1;
    }
    
    // ==============================================
    // STEP 2: BACKUP AND ERROR HANDLING
    // ==============================================
    
    private static void createBackup() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try {
                Files.copy(Paths.get(DATA_FILE), Paths.get(BACKUP_FILE), StandardCopyOption.REPLACE_EXISTING);
                System.out.printf("✅ Backup created as '%s'%n", BACKUP_FILE);
            } catch (IOException e) {
                System.out.printf("⚠️ Could not create backup: %s%n", e.getMessage());
            }
        }
    }
    
    private static List<Contact> restoreFromBackup() {
        File file = new File(BACKUP_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(BACKUP_FILE))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                List<Contact> contacts = parseJsonToContacts(content.toString());
                System.out.printf("✅ Restored %d contacts from backup%n", contacts.size());
                return contacts;
            } catch (Exception e) {
                System.out.printf("❌ Error restoring backup: %s%n", e.getMessage());
                return new ArrayList<>();
            }
        } else {
            System.out.println("❌ No backup file found");
            return new ArrayList<>();
        }
    }
    
    // ==============================================
    // STEP 2: CRUD OPERATIONS WITH AUTO-SAVE
    // ==============================================
    
    private static int createContact(List<Contact> contacts, int nextId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("➕ ADD NEW CONTACT");
        System.out.println("=".repeat(50));
        
        String name = "";
        while (name.isEmpty()) {
            System.out.print("Enter name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty.");
            }
        }
        
        String phone = "";
        while (phone.isEmpty()) {
            System.out.print("Enter phone number: ");
            phone = scanner.nextLine().trim();
            if (phone.isEmpty()) {
                System.out.println("❌ Phone cannot be empty.");
            }
        }
        
        System.out.print("Enter email (or press Enter for N/A): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            email = "N/A";
        }
        
        System.out.print("Enter address (or press Enter for N/A): ");
        String address = scanner.nextLine().trim();
        if (address.isEmpty()) {
            address = "N/A";
        }
        
        String[] categories = {"Family", "Friends", "Work", "Other"};
        System.out.println("\nAvailable categories:");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, categories[i]);
        }
        
        String category = "";
        while (true) {
            try {
                System.out.print("Select category (1-4): ");
                int catChoice = Integer.parseInt(scanner.nextLine().trim());
                if (catChoice >= 1 && catChoice <= 4) {
                    category = categories[catChoice - 1];
                    break;
                } else {
                    System.out.println("❌ Please select 1-4");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Enter a number.");
            }
        }
        
        Contact newContact = new Contact(nextId, name, phone, email, address, category);
        contacts.add(newContact);
        
        System.out.printf("%n✅ Contact added successfully! (ID: %d)%n", nextId);
        System.out.println("💾 Data automatically saved to file");
        
        saveContactsToFile(contacts);
        return nextId + 1;
    }
    
    private static void displayAllContacts(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📋 ALL CONTACTS");
        System.out.println("=".repeat(80));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts found. Add some contacts first!");
            return;
        }
        
        System.out.printf("%-3s | %-20s | %-12s | %-25s | %-10s%n", 
            "ID", "Name", "Phone", "Email", "Category");
        System.out.println("-".repeat(80));
        
        for (Contact contact : contacts) {
            System.out.println(contact.toShortString());
        }
        
        System.out.println("=".repeat(80));
        System.out.printf("📊 TOTAL CONTACTS: %d%n", contacts.size());
        System.out.println("=".repeat(80));
    }
    
    private static void displayContactById(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔍 FIND CONTACT BY ID");
        System.out.println("=".repeat(50));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts found!");
            return;
        }
        
        try {
            System.out.print("Enter contact ID: ");
            int contactId = Integer.parseInt(scanner.nextLine().trim());
            
            for (Contact contact : contacts) {
                if (contact.id == contactId) {
                    System.out.println("\n👤 CONTACT DETAILS");
                    System.out.println("=".repeat(40));
                    contact.display();
                    return;
                }
            }
            
            System.out.printf("❌ Contact with ID %d not found!%n", contactId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }
    
    private static void searchContactsByName(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔎 SEARCH CONTACTS BY NAME");
        System.out.println("=".repeat(50));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts found!");
            return;
        }
        
        System.out.print("Enter name to search: ");
        String searchName = scanner.nextLine().trim().toLowerCase();
        
        List<Contact> results = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.name.toLowerCase().contains(searchName)) {
                results.add(contact);
            }
        }
        
        if (results.isEmpty()) {
            System.out.printf("❌ No contacts found with name containing '%s'%n", searchName);
        } else {
            System.out.printf("%n✅ Found %d contact(s):%n", results.size());
            System.out.println("=".repeat(80));
            System.out.printf("%-3s | %-20s | %-12s | %-25s | %-10s%n", 
                "ID", "Name", "Phone", "Email", "Category");
            System.out.println("-".repeat(80));
            for (Contact contact : results) {
                System.out.println(contact.toShortString());
            }
            System.out.println("=".repeat(80));
        }
    }
    
    private static void searchContactsByCategory(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📂 SEARCH CONTACTS BY CATEGORY");
        System.out.println("=".repeat(50));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts found!");
            return;
        }
        
        String[] categories = {"Family", "Friends", "Work", "Other"};
        System.out.println("\nAvailable categories:");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, categories[i]);
        }
        
        try {
            System.out.print("Select category (1-4): ");
            int catChoice = Integer.parseInt(scanner.nextLine().trim());
            if (catChoice < 1 || catChoice > 4) {
                System.out.println("❌ Invalid choice!");
                return;
            }
            
            String category = categories[catChoice - 1];
            List<Contact> results = new ArrayList<>();
            for (Contact contact : contacts) {
                if (contact.category.equals(category)) {
                    results.add(contact);
                }
            }
            
            if (results.isEmpty()) {
                System.out.printf("❌ No contacts found in category '%s'%n", category);
            } else {
                System.out.printf("%n✅ Found %d contact(s) in '%s':%n", results.size(), category);
                System.out.println("=".repeat(80));
                System.out.printf("%-3s | %-20s | %-12s | %-25s | %-10s%n", 
                    "ID", "Name", "Phone", "Email", "Category");
                System.out.println("-".repeat(80));
                for (Contact contact : results) {
                    System.out.println(contact.toShortString());
                }
                System.out.println("=".repeat(80));
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }
    
    private static void updateContact(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("✏️ UPDATE CONTACT");
        System.out.println("=".repeat(50));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts to update!");
            return;
        }
        
        try {
            System.out.print("Enter contact ID to update: ");
            int contactId = Integer.parseInt(scanner.nextLine().trim());
            
            for (Contact contact : contacts) {
                if (contact.id == contactId) {
                    System.out.println("\n📌 Current details:");
                    contact.display();
                    
                    System.out.println("\n📝 Enter new values (press Enter to keep current value):");
                    
                    System.out.printf("New name (%s): ", contact.name);
                    String newName = scanner.nextLine().trim();
                    if (!newName.isEmpty()) {
                        contact.name = newName;
                        System.out.println("✅ Name updated!");
                    }
                    
                    System.out.printf("New phone (%s): ", contact.phone);
                    String newPhone = scanner.nextLine().trim();
                    if (!newPhone.isEmpty()) {
                        contact.phone = newPhone;
                        System.out.println("✅ Phone updated!");
                    }
                    
                    System.out.printf("New email (%s): ", contact.email);
                    String newEmail = scanner.nextLine().trim();
                    if (!newEmail.isEmpty()) {
                        contact.email = newEmail;
                        System.out.println("✅ Email updated!");
                    }
                    
                    System.out.printf("New address (%s): ", contact.address);
                    String newAddress = scanner.nextLine().trim();
                    if (!newAddress.isEmpty()) {
                        contact.address = newAddress;
                        System.out.println("✅ Address updated!");
                    }
                    
                    String[] categories = {"Family", "Friends", "Work", "Other"};
                    System.out.println("\nCategories:");
                    for (int i = 0; i < categories.length; i++) {
                        System.out.printf("  %d. %s%n", i + 1, categories[i]);
                    }
                    
                    System.out.printf("New category (%s): ", contact.category);
                    String newCategory = scanner.nextLine().trim();
                    if (!newCategory.isEmpty()) {
                        try {
                            int catChoice = Integer.parseInt(newCategory);
                            if (catChoice >= 1 && catChoice <= 4) {
                                contact.category = categories[catChoice - 1];
                                System.out.println("✅ Category updated!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Invalid category! Keeping original.");
                        }
                    }
                    
                    System.out.println("\n✅ Contact updated successfully!");
                    System.out.println("💾 Data automatically saved to file");
                    System.out.println("\n🔄 Updated details:");
                    contact.display();
                    
                    saveContactsToFile(contacts);
                    return;
                }
            }
            
            System.out.printf("❌ Contact with ID %d not found!%n", contactId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }
    
    private static void deleteContact(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🗑️ DELETE CONTACT");
        System.out.println("=".repeat(50));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts to delete!");
            return;
        }
        
        try {
            System.out.print("Enter contact ID to delete: ");
            int contactId = Integer.parseInt(scanner.nextLine().trim());
            
            Iterator<Contact> iterator = contacts.iterator();
            while (iterator.hasNext()) {
                Contact contact = iterator.next();
                if (contact.id == contactId) {
                    System.out.println("\n📌 Contact to delete:");
                    contact.display();
                    
                    System.out.print("\n⚠️ Are you sure you want to delete this contact? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    
                    if (confirm.equals("yes") || confirm.equals("y")) {
                        iterator.remove();
                        System.out.printf("%n✅ Contact ID %d deleted successfully!%n", contactId);
                        System.out.printf("   (Name: %s, Category: %s)%n", contact.name, contact.category);
                        System.out.println("💾 Data automatically saved to file");
                        saveContactsToFile(contacts);
                    } else {
                        System.out.println("\n❌ Deletion cancelled!");
                    }
                    return;
                }
            }
            
            System.out.printf("❌ Contact with ID %d not found!%n", contactId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }
    
    // ==============================================
    // CATEGORY SUMMARY
    // ==============================================
    
    private static void showCategorySummary(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 CATEGORY-WISE SUMMARY");
        System.out.println("=".repeat(50));
        
        if (contacts.isEmpty()) {
            System.out.println("📭 No contacts to summarize!");
            return;
        }
        
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Contact contact : contacts) {
            categoryCounts.merge(contact.category, 1, Integer::sum);
        }
        
        System.out.printf("%-15s | %-10s | %s%n", "Category", "Count", "Percentage");
        System.out.println("-".repeat(40));
        
        int total = contacts.size();
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / total;
            System.out.printf("%-15s | %-10d | %5.1f%%%n", entry.getKey(), entry.getValue(), percentage);
        }
        
        System.out.println("-".repeat(40));
        System.out.printf("%-15s | %-10d | 100.0%%%n", "TOTAL", total);
        System.out.println("=".repeat(50));
    }
    
    // ==============================================
    // FILE MANAGEMENT MENU
    // ==============================================
    
    private static void showFileMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💾 FILE MANAGEMENT");
        System.out.println("=".repeat(50));
        System.out.println("1. 📂 Manual Save (Save to file)");
        System.out.println("2. 🔄 Restore from Backup");
        System.out.println("3. 📁 View File Info");
        System.out.println("4. 🗑️ Delete Data File (Start Fresh)");
        System.out.println("5. 🔙 Back to Main Menu");
        System.out.println("=".repeat(50));
    }
    
    private static void viewFileInfo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📁 FILE INFORMATION");
        System.out.println("=".repeat(50));
        
        File file = new File(DATA_FILE);
        if (file.exists()) {
            System.out.printf("✅ File: %s%n", DATA_FILE);
            System.out.printf("📏 Size: %d bytes%n", file.length());
            System.out.printf("📅 Last Modified: %tF %<tT%n", file.lastModified());
        } else {
            System.out.printf("❌ File '%s' does not exist%n", DATA_FILE);
        }
        
        File backup = new File(BACKUP_FILE);
        if (backup.exists()) {
            System.out.printf("✅ Backup: %s%n", BACKUP_FILE);
            System.out.printf("📏 Size: %d bytes%n", backup.length());
        } else {
            System.out.printf("⚠️ No backup file found%n");
        }
        
        System.out.println("=".repeat(50));
    }
    
    private static List<Contact> deleteDataFile(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("⚠️ DELETE ALL DATA");
        System.out.println("=".repeat(50));
        
        System.out.print("⚠️ Are you sure you want to delete ALL data? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("yes") || confirm.equals("y")) {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                file.delete();
                System.out.printf("✅ File '%s' deleted%n", DATA_FILE);
            }
            
            contacts.clear();
            System.out.println("✅ All contacts cleared from memory");
            System.out.println("💡 Program will start fresh on next launch");
            return new ArrayList<>();
        } else {
            System.out.println("❌ Deletion cancelled");
            return contacts;
        }
    }
    
    // ==============================================
    // MAIN MENU
    // ==============================================
    
    private static void showMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📒 CONTACT BOOK (With File Storage) 📒");
        System.out.println("=".repeat(50));
        System.out.println("1. ➕ ADD contact (CREATE)");
        System.out.println("2. 📋 VIEW all contacts (READ)");
        System.out.println("3. 🔍 VIEW contact by ID (READ)");
        System.out.println("4. 🔎 SEARCH by name");
        System.out.println("5. 📂 SEARCH by category");
        System.out.println("6. 📊 CATEGORY summary");
        System.out.println("7. ✏️ UPDATE contact (UPDATE)");
        System.out.println("8. 🗑️ DELETE contact (DELETE)");
        System.out.println("9. 💾 FILE MANAGEMENT");
        System.out.println("10. ❌ EXIT");
        System.out.println("=".repeat(50));
    }
    
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📒 CONTACT BOOK WITH FILE PERSISTENCE 📒");
        System.out.println("=".repeat(60));
        System.out.println("Data is automatically saved to 'contacts.json'");
        System.out.println("Your contacts will survive program restarts!\n");
        
        // Load existing data from file
        List<Contact> contacts = loadContactsFromFile();
        int nextId = getNextId(contacts);
        
        System.out.printf("%n📊 Current status:%n");
        System.out.printf("   • Total contacts: %d%n", contacts.size());
        System.out.printf("   • Next available ID: %d%n", nextId);
        System.out.printf("   • Data file: %s%n", DATA_FILE);
        
        while (true) {
            showMenu();
            System.out.print("\nEnter your choice (1-10): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> nextId = createContact(contacts, nextId);
                case "2" -> displayAllContacts(contacts);
                case "3" -> displayContactById(contacts);
                case "4" -> searchContactsByName(contacts);
                case "5" -> searchContactsByCategory(contacts);
                case "6" -> showCategorySummary(contacts);
                case "7" -> updateContact(contacts);
                case "8" -> deleteContact(contacts);
                case "9" -> {
                    while (true) {
                        showFileMenu();
                        System.out.print("\nEnter your choice (1-5): ");
                        String fileChoice = scanner.nextLine().trim();
                        
                        switch (fileChoice) {
                            case "1" -> saveContactsToFile(contacts);
                            case "2" -> {
                                List<Contact> restored = restoreFromBackup();
                                if (!restored.isEmpty()) {
                                    contacts.clear();
                                    contacts.addAll(restored);
                                    nextId = getNextId(contacts);
                                    System.out.println("✅ Data restored from backup!");
                                }
                            }
                            case "3" -> viewFileInfo();
                            case "4" -> contacts = deleteDataFile(contacts);
                            case "5" -> { break; }
                            default -> System.out.println("❌ Invalid choice!");
                        }
                        
                        if (fileChoice.equals("5")) break;
                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                    }
                }
                case "10" -> {
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("💾 Saving data before exit...");
                    saveContactsToFile(contacts);
                    
                    System.out.println("\n👋 Thank you for using Contact Book!");
                    System.out.printf("📊 FINAL SUMMARY:%n");
                    System.out.printf("   • Total contacts tracked: %d%n", contacts.size());
                    System.out.printf("   • Data saved to: %s%n", DATA_FILE);
                    System.out.println("\n📒 Goodbye! Your contacts have been saved! 📒");
                    return;
                }
                default -> System.out.println("❌ Invalid choice! Please enter 1-10");
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
}