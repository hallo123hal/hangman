# Hangman Game in Kotlin

A simple Android Hangman game built with Kotlin that demonstrates basic game logic, UI animations, sound effects, and state management. The game features two screens: a main menu and the gameplay screen, where users guess letters to reveal a hidden word.

## Overview

This project is an Android application where the user can play a classic game of Hangman. The game randomly selects a word (from a list of country names) and displays it as a series of underscores. The user then selects a position in the word and guesses a letter. With each guess, the game updates the display, plays sound effects, and shows animations for correct or incorrect guesses. A countdown timer adds an extra layer of challenge to the game.

## Features

- **Two-Screen Navigation:**  
  - **Main Menu:** Displays the game title and a Play button.  
  - **Game Screen:** Contains the Hangman image, letter selection UI, timer, hint option, and animations for win/loss.

- **Game Mechanics:**  
  - Random word selection from a predefined list (e.g., country names).  
  - User selects a position in the word to guess and then taps on a letter.
  - Tracks correct and incorrect guesses and updates the Hangman image accordingly.
  - Provides a hint option that reveals the correct letter at the selected position.

- **Multimedia Integration:**  
  - Background music that loops during gameplay.
  - Sound effects for correct (ding) and incorrect (buzz) guesses.
  - Lottie animations for win (fireworks) and loss (shake/lose animation).

- **Timer Functionality:**  
  - A 60-second countdown timer that ends the game when time runs out.

- **State Management:**  
  - Uses a `GameManager` class to handle game logic.
  - Represents game states (Running, Won, Lost) with a Kotlin sealed class (`GameState`).

## Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/hangman-kotlin.git
   cd hangman-kotlin

2. **Open in Android Studio:**

- Open Android Studio.
- Click on **File > Open**... and navigate to the cloned repository folder.
- Wait for Gradle to sync and build the project.

3. **Run the App:**

- Connect an Android device or start an emulator.
- Click the Run button in Android Studio or use *Shift + F10*.

## Project Structure
- **MainActivity.kt & activity_main.xml:**
  The entry point of the app. Displays the title and Play button that starts the game.

- **GameActivity.kt & activity_game.xml:**
  The main game screen where the Hangman game is played. Handles user interactions, displays the Hangman image, letter buttons, timer, and animations.

- **GameManager.kt:**
  Contains the core game logic including word selection, processing letter guesses, updating the Hangman image, and managing game state transitions.

- **GameConstants.kt:**
  Provides a list of words (in this case, country names) used in the game.

- **GameState.kt:**
  A sealed class representing the current state of the game (Running, Won, or Lost).

- **Resources:**

  - **Drawable assets:** Hangman images representing different stages of the game.
  - **Animations:** Lottie JSON files for win and lose animations.
  - **Audio files:** Background music and sound effects for correct and incorrect guesses.

## Usage

1. **Launch the App:**
  The app starts on the main menu displaying the title "Hangman" and a Play button.

2. **Start the Game:**
  Tap the **Play** button to navigate to the game screen.

3. **Gameplay:**
  - The game randomly selects a word and displays it with underscores.
  - Tap on any underscore to select a position to guess.
  - Tap a letter from the on-screen alphabet. The game will update the display:
    - Reveals the letter if the guess is correct.
    - Increments the wrong guess count and updates the Hangman image if the guess is incorrect.
  - Use the **Hint** button to automatically reveal the correct letter at the selected position.
  - The game runs on a 60-second timer. If time expires, the game ends with a loss.

4. **Game End:**
  The game concludes when:
    - The word is completely guessed (Win).
    - The maximum number of incorrect guesses is reached or the timer runs out (Loss). Appropriate animations and messages will indicate the outcome. A **New Game** button allows you to restart.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests for improvements and new features.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Built using [Android Studio](https://developer.android.com/studio).
- Animation assets provided by [Lottie](https://airbnb.design/lottie/).
- Inspired by the classic game of Hangman.
