package com.map.nguyensontung.hangman

sealed class GameState {
    class Running(
        val lettersUsed: Set<Char>,
        val underscoreWord: String,
        val drawable: Int,
        val selectedPosition: Int
    ) : GameState()
    class Lost(val wordToGuess: String) : GameState()
    class Won(val wordToGuess: String) : GameState()
}