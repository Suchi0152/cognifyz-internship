# Prime Number Pyramid

## Description
A pyramid pattern made entirely of prime numbers. Each row contains consecutive primes with pyramid alignment.

## How to Run
```bash
python prime_pyramid.py

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

Prime Checking Logic:
text
To check if number N is prime:
  IF N < 2 THEN return False
  FOR i = 2 to square root of N
    IF N % i == 0 THEN return False
  RETURN True