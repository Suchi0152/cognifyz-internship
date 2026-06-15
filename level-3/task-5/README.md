# 💾 Expense Tracker with File Persistence

## 📋 Project Information
- **Task:** Level 3, Task 5
- **Internship:** Cognifyz Software Development Program
- **Language:** Python 3
- **Concept:** File I/O, JSON storage, CRUD operations

## 🎯 Objective
Enhance the CRUD application to store task data persistently using file I/O. Implement file storage for tasks to enable saving and loading from a text file.

## ✨ Features

### Core Features (from Task 3)
- ✅ Create new expenses
- ✅ Read/View all expenses
- ✅ Update existing expenses
- ✅ Delete expenses
- ✅ Category-wise summary
- ✅ Monthly expense reports

### NEW Features (File Persistence)
- 💾 **Auto-save** - Data automatically saved after every modification
- 📂 **Load on startup** - Previous data loads when program starts
- 🔄 **Backup system** - Create and restore from backup files
- 🛡️ **Error handling** - Handles corrupted files, permission errors
- 📁 **File management menu** - Manual save, restore, delete data
- 📊 **File info viewer** - View file size and modification time

## 📁 Files Created by This Program

| File | Purpose | Auto-created? |
|------|---------|---------------|
| `expenses.json` | Main data storage file | ✅ Yes (on first save) |
| `expenses_backup.json` | Backup file for recovery | ⚠️ Manual backup only |

### expenses.json Structure
```json
[
    {
        "id": 1,
        "amount": 250.00,
        "category": "Food",
        "date": "2026-06-10",
        "description": "Grocery shopping"
    },
    {
        "id": 2,
        "amount": 50.00,
        "category": "Transport",
        "date": "2026-06-11",
        "description": "Uber ride"
    }
]