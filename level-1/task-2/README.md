# 🔺 Prime Number Pyramid

## 📋 Project Information
- **Task:** Level 1, Task 2
- **Internship:** Cognifyz Software Development Program
- **Language:** Java 
- **Concept:** Nested Loops, Prime Number Logic

## 🎯 Objective
Generate and print a pyramid pattern made entirely of prime numbers. Each row contains consecutive primes with proper pyramid alignment.

START
Input number of rows (say 5)
Initialize prime counter = 2
Initialize list to store primes found

FOR row = 1 to rows
WHILE count of primes in this row < row
Check if current number is prime
IF prime THEN add to list
Increment current number
END WHILE

Print spaces for alignment
Print primes for this row
END FOR
END