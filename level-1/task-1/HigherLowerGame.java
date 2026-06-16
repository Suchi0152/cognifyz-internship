import java.util.Random;
import java.util.Scanner;

/**
 * Higher or Lower Card Game
 * Cognifyz Internship - Level 1 Task 1
 * Java Version - Fully Corrected
 */
public class HigherLowerGame {
    
    private static final Random random = new Random();
    
    /**
     * Convert number to card name using switch expression (modern Java)
     */
    public static String getCardName(int cardValue) {
        return switch (cardValue) {
            case 1 -> "Ace";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> String.valueOf(cardValue);
        };
    }
    
    /**
     * Play one round of the game
     * Uses Scanner passed from main to avoid resource leak
     */
    public static void playHigherLower(Scanner scanner) {
        System.out.println("=".repeat(40));
        System.out.println("🃏 HIGHER OR LOWER CARD GAME 🃏");
        System.out.println("=".repeat(40));
        System.out.println("Rules:");
        System.out.println("- Cards are 1 (Ace) to 13 (King)");
        System.out.println("- Guess if the next card will be Higher (H) or Lower (L)");
        System.out.println("- If the cards are equal, you lose that round");
        System.out.println("- You'll play 5 rounds");
        System.out.println("=".repeat(40));
        
        int score = 0;
        final int ROUNDS_TO_PLAY = 5;
        
        // Game loop
        for (int roundNum = 1; roundNum <= ROUNDS_TO_PLAY; roundNum++) {
            System.out.printf("\n🔄 ROUND %d of %d\n", roundNum, ROUNDS_TO_PLAY);
            System.out.println("-".repeat(30));
            
            // Generate two random cards
            int card1 = random.nextInt(13) + 1;  // 1 to 13
            int card2 = random.nextInt(13) + 1;  // 1 to 13
            
            // Show first card
            System.out.printf("📊 First card: %s\n", getCardName(card1));
            
            // Get player's guess with validation
            char guess;
            while (true) {
                System.out.print("Will the next card be Higher (H) or Lower (L)? ");
                String input = scanner.nextLine().toUpperCase();
                if (input.length() > 0 && (input.charAt(0) == 'H' || input.charAt(0) == 'L')) {
                    guess = input.charAt(0);
                    break;
                }
                System.out.println("❌ Invalid! Please enter 'H' or 'L'");
            }
            
            // ===== CONDITIONAL STATEMENTS START HERE =====
            
            // Check if tie (card1 == card2)
            if (card1 == card2) {
                System.out.printf("\n📊 Second card: %s\n", getCardName(card2));
                System.out.println("❌ TIE! You lose this round (cards are equal)");
            }
            // Check if player guessed Higher
            else if (guess == 'H') {
                System.out.printf("\n📊 Second card: %s\n", getCardName(card2));
                if (card2 > card1) {
                    System.out.println("✅ WIN! The second card is HIGHER!");
                    score++;
                } else {
                    System.out.println("❌ LOSE! The second card is LOWER!");
                }
            }
            // Check if player guessed Lower
            else if (guess == 'L') {
                System.out.printf("\n📊 Second card: %s\n", getCardName(card2));
                if (card2 < card1) {
                    System.out.println("✅ WIN! The second card is LOWER!");
                    score++;
                } else {
                    System.out.println("❌ LOSE! The second card is HIGHER!");
                }
            }
            
            // ===== CONDITIONAL STATEMENTS END HERE =====
            
            // Show current score
            System.out.printf("📈 Current score: %d/%d\n", score, roundNum);
        }
        
        // Game over - final score
        System.out.println("\n" + "=".repeat(40));
        System.out.println("🎮 GAME OVER! 🎮");
        System.out.println("=".repeat(40));
        System.out.printf("🏆 Your final score: %d out of %d\n", score, ROUNDS_TO_PLAY);
        
        // Final message based on score (Conditional Statements)
        if (score == ROUNDS_TO_PLAY) {
            System.out.println("🎉 PERFECT! You're a card master! 🎉");
        } else if (score >= ROUNDS_TO_PLAY - 2) {
            System.out.println("👍 Great job! You have good intuition!");
        } else if (score >= ROUNDS_TO_PLAY / 2) {
            System.out.println("📊 Not bad! Keep practicing!");
        } else {
            System.out.println("🃏 Better luck next time!");
        }
    }
    
    /**
     * Main method - Entry point of the program
     * Using try-with-resources for automatic resource management
     */
    public static void main(String[] args) {
        // Try-with-resources ensures Scanner is closed automatically
        try (Scanner scanner = new Scanner(System.in)) {
            // Play the game initially
            playHigherLower(scanner);
            
            // Ask to play again using switch (modern Java)
            while (true) {
                System.out.print("\n🔁 Play again? (yes/no): ");
                String playAgain = scanner.nextLine().toLowerCase();
                
                // Using switch expression for cleaner code
                switch (playAgain) {
                    case "yes", "y" -> {
                        System.out.println("\n".repeat(2));
                        playHigherLower(scanner);
                    }
                    case "no", "n" -> {
                        System.out.println("Thanks for playing! 👋");
                        return; // Exit the program
                    }
                    default -> System.out.println("Please enter 'yes' or 'no'");
                }
            }
        }
    }
}