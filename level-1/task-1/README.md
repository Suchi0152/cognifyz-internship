# Higher or Lower Card Game

## Description
A text-based card game where players guess if the next card is higher or lower.

## How to Run
python higher_lower_game.py

## Game Rules
- Cards: Ace(1) to King(13)
- Guess H (Higher) or L (Lower)
- Win 1 point per correct guess
- Play 5 rounds

## Conditional Statements Used
- if/elif/else for win/loss logic
- while loop for input validation
- if/else for score-based final message


START
  generate random number (1–20)
  set attempts = 0, maxAttempts = 5
  WHILE attempts < maxAttempts
    ask user for guess
    attempts++
    IF guess == randomNumber
      print "Correct!" and exit
    ELSE IF guess < randomNumber
      print "Too low"
    ELSE
      print "Too high"
  END WHILE
  print "Out of attempts! The number was X"
END