import random

# Step 3: Game logic using conditional statements

def get_card_name(card_value):
    """Convert number to card name"""
    if card_value == 1:
        return "Ace"
    elif card_value == 11:
        return "Jack"
    elif card_value == 12:
        return "Queen"
    elif card_value == 13:
        return "King"
    else:
        return str(card_value)

def play_higher_lower():
    print("=" * 40)
    print("🃏 HIGHER OR LOWER CARD GAME 🃏")
    print("=" * 40)
    print("Rules:")
    print("- Cards are 1 (Ace) to 13 (King)")
    print("- Guess if the next card will be Higher (H) or Lower (L)")
    print("- If the cards are equal, you lose that round")
    print("- You'll play 5 rounds")
    print("=" * 40)
    
    score = 0
    rounds_to_play = 5
    
    # Game loop
    for round_num in range(1, rounds_to_play + 1):
        print(f"\n🔄 ROUND {round_num} of {rounds_to_play}")
        print("-" * 30)
        
        # Generate two random cards
        card1 = random.randint(1, 13)
        card2 = random.randint(1, 13)
        
        # Show first card
        print(f"📊 First card: {get_card_name(card1)}")
        
        # Get player's guess with validation
        while True:
            guess = input("Will the next card be Higher (H) or Lower (L)? ").upper()
            if guess in ['H', 'L']:
                break
            print("❌ Invalid! Please enter 'H' or 'L'")
        
        # ===== CONDITIONAL STATEMENTS START HERE =====
        
        # Check if tie (card1 == card2)
        if card1 == card2:
            print(f"\n📊 Second card: {get_card_name(card2)}")
            print("❌ TIE! You lose this round (cards are equal)")
            
        # Check if player guessed Higher
        elif guess == 'H':
            print(f"\n📊 Second card: {get_card_name(card2)}")
            if card2 > card1:
                print("✅ WIN! The second card is HIGHER!")
                score += 1
            else:
                print("❌ LOSE! The second card is LOWER!")
                
        # Check if player guessed Lower
        elif guess == 'L':
            print(f"\n📊 Second card: {get_card_name(card2)}")
            if card2 < card1:
                print("✅ WIN! The second card is LOWER!")
                score += 1
            else:
                print("❌ LOSE! The second card is HIGHER!")
        
        # ===== CONDITIONAL STATEMENTS END HERE =====
        
        # Show current score
        print(f"📈 Current score: {score}/{round_num}")
    
    # Game over - final score
    print("\n" + "=" * 40)
    print("🎮 GAME OVER! 🎮")
    print("=" * 40)
    print(f"🏆 Your final score: {score} out of {rounds_to_play}")
    
    # Final message based on score
    if score == rounds_to_play:
        print("🎉 PERFECT! You're a card master! 🎉")
    elif score >= rounds_to_play - 2:
        print("👍 Great job! You have good intuition!")
    elif score >= rounds_to_play // 2:
        print("📊 Not bad! Keep practicing!")
    else:
        print("🃏 Better luck next time! Want to play again?")

# Run the game
if __name__ == "__main__":
    play_higher_lower()
    
    # Ask to play again (bonus conditional)
    while True:
        play_again = input("\n🔁 Play again? (yes/no): ").lower()
        if play_again == 'yes' or play_again == 'y':
            print("\n" * 2)
            play_higher_lower()
        elif play_again == 'no' or play_again == 'n':
            print("Thanks for playing! 👋")
            break
        else:
            print("Please enter 'yes' or 'no'")