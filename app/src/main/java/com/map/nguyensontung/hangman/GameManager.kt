package com.map.nguyensontung.hangman

import kotlin.random.Random
import android.content.Context
import com.map.nguyensontung.hangman.GameActivity

class GameManager {
    private lateinit var underscoreWord: String
    private lateinit var wordToGuess: String
    private val maxTries = 7
    private var currentTries = 0
    private var drawable: Int = R.drawable.game0
    private var selectedPosition: Int = 0
    private val positionGuesses = mutableMapOf<Int, MutableSet<Char>>()
    private var hintUsed = false

    fun startNewGame(): GameState {
        currentTries = 0
        drawable = R.drawable.game0
        selectedPosition = 0
        positionGuesses.clear()
        val randomIndex = Random.nextInt(0, GameConstants.words.size)
        wordToGuess = GameConstants.words[randomIndex]
        generateUnderscores(wordToGuess)
        hintUsed = false  // Reset hint usage on new game
        return getGameState()
    }

    fun setSelectedPosition(position: Int) {
        if (position < wordToGuess.length) {
            selectedPosition = position
        }
    }

    fun getSelectedPosition(): Int = selectedPosition

    fun getGuessesForPosition(position: Int): Set<Char> {
        return positionGuesses[position] ?: emptySet()
    }

    fun generateUnderscores(word: String) {
        val sb = StringBuilder()
        word.forEach { char ->
            if (char == '/') {
                sb.append('/')
            } else {
                sb.append("_")
            }
        }
        underscoreWord = sb.toString()
    }

    fun play(letter: Char): GameState {
        if (selectedPosition < wordToGuess.length) {
            positionGuesses.getOrPut(selectedPosition) { mutableSetOf() }.add(letter)

            if (wordToGuess[selectedPosition].equals(letter, true)) {
                // Correct guess: Play ding sound
                GameActivity.playDing() // Calling playDing() from GameActivity
                val sb = StringBuilder(underscoreWord)
                sb.setCharAt(selectedPosition, letter)
                underscoreWord = sb.toString()
            } else {
                // Incorrect guess: Play buzz sound
                GameActivity.playBuzz() // Calling playBuzz() from GameActivity
                currentTries++
            }
        }
        return getGameState()
    }

    private fun getHangmanDrawable(): Int {
        return when (currentTries) {
            0 -> R.drawable.game0
            1 -> R.drawable.game1
            2 -> R.drawable.game2
            3 -> R.drawable.game3
            4 -> R.drawable.game4
            5 -> R.drawable.game5
            6 -> R.drawable.game6
            7 -> R.drawable.game7
            else -> R.drawable.game7
        }
    }

    private fun getGameState(): GameState {
        if (underscoreWord.equals(wordToGuess, true)) {
            return GameState.Won(wordToGuess)
        }

        if (currentTries == maxTries) {
            return GameState.Lost(wordToGuess)
        }

        drawable = getHangmanDrawable()
        val currentGuesses = getGuessesForPosition(selectedPosition)
        return GameState.Running(currentGuesses, underscoreWord, drawable, selectedPosition)
    }

    fun useHint(): GameState {
        if (!hintUsed && selectedPosition < wordToGuess.length) {
            val correctLetter = wordToGuess[selectedPosition]
            val sb = StringBuilder(underscoreWord)
            sb.setCharAt(selectedPosition, correctLetter)
            underscoreWord = sb.toString()
            hintUsed = true
        }
        return getGameState()
    }
    fun isHintUsed(): Boolean = hintUsed
}