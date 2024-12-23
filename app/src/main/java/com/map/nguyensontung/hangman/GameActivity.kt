package com.map.nguyensontung.hangman

import android.graphics.Color
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import android.os.CountDownTimer

class GameActivity : AppCompatActivity() {

    // MediaPlayer for background music
    private lateinit var mediaPlayer: MediaPlayer

    private val gameManager = GameManager()
    private val charTextViews = mutableListOf<TextView>()
    private var timer: CountDownTimer? = null
    private var secondsElapsed = 0
    private var timerTimeLeft = 60000L // 1 minute timer (in milliseconds)

    private lateinit var timerTextView: TextView
    private lateinit var wordContainer: LinearLayout
    private lateinit var lettersUsedTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var gameLostTextView: TextView
    private lateinit var gameWonTextView: TextView
    private lateinit var newGameButton: Button
    private lateinit var lettersLayout: ConstraintLayout
    private lateinit var hintButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.isLooping = true // Loop the music
        mediaPlayer.start() // Start background music

        // Initialize SoundPool for sound effects
        soundPool = SoundPool.Builder().setMaxStreams(2).build()
        dingSound = soundPool.load(this, R.raw.ding_sound, 1)
        buzzSound = soundPool.load(this, R.raw.buzz_sound, 1)

        imageView = findViewById(R.id.imageView)
        wordContainer = findViewById(R.id.wordContainer)
        lettersUsedTextView = findViewById(R.id.lettersUsedTextView)
        gameLostTextView = findViewById(R.id.gameLostTextView)
        gameWonTextView = findViewById(R.id.gameWonTextView)
        newGameButton = findViewById(R.id.newGameButton)
        lettersLayout = findViewById(R.id.lettersLayout)
        hintButton = findViewById(R.id.hintButton)
        timerTextView = findViewById(R.id.timerTextView)

        newGameButton.setOnClickListener {
            startNewGame()
        }

        hintButton.setOnClickListener {
            val gameState = gameManager.useHint()
            updateUI(gameState)
            hintButton.visibility = View.GONE  // Hide the button after use
        }

        lettersLayout.children.forEach { letterView ->
            if (letterView is TextView) {
                letterView.setOnClickListener {
                    val gameState = gameManager.play((letterView).text[0])
                    updateUI(gameState)
                }
            }
        }

        val gameState = gameManager.startNewGame()
        startTimer()
        updateUI(gameState)
    }

    private fun updateUI(gameState: GameState) {
        when (gameState) {
            is GameState.Lost -> {
                showGameLost(gameState.wordToGuess)
                stopTimer() // Stop timer when game is lost
            }
            is GameState.Running -> {
                updateWordDisplay(gameState.underscoreWord, gameState.selectedPosition)
                updateUsedLettersDisplay(gameState.lettersUsed)
                updateLetterVisibility(gameState.lettersUsed)
                imageView.setImageDrawable(ContextCompat.getDrawable(this, gameState.drawable))
            }
            is GameState.Won -> {
                showGameWon(gameState.wordToGuess)
                stopTimer() // Stop timer when game is won
            }
        }
    }

    private fun updateLetterVisibility(lettersUsed: Set<Char>) {
        lettersLayout.children.forEach { view ->
            if (view is TextView) {
                val letter = view.text[0]
                if (lettersUsed.contains(letter)) {
                    view.visibility = View.INVISIBLE
                } else {
                    view.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUsedLettersDisplay(lettersUsed: Set<Char>) {
        lettersUsedTextView.text = if (lettersUsed.isEmpty()) {
            "No guesses for this position yet"
        } else {
            "Letters tried at this position: ${lettersUsed.joinToString(", ")}"
        }
    }

    private fun updateWordDisplay(word: String, selectedPosition: Int) {
        if (charTextViews.isEmpty()) {
            wordContainer.removeAllViews()
            word.forEach { char ->
                TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = resources.getDimensionPixelSize(R.dimen.letter_spacing)
                        marginEnd = resources.getDimensionPixelSize(R.dimen.letter_spacing)
                    }
                    textSize = 26f
                    text = char.toString()
                    setTextColor(Color.BLACK)
                    charTextViews.add(this)
                    wordContainer.addView(this)
                }
            }
        } else {
            word.forEachIndexed { index, char ->
                charTextViews[index].text = char.toString()
            }
        }

        charTextViews.forEachIndexed { index, textView ->
            textView.setBackgroundColor(
                if (index == selectedPosition)
                    ContextCompat.getColor(this, R.color.selected_position)
                else
                    Color.TRANSPARENT
            )

            textView.setOnClickListener {
                gameManager.setSelectedPosition(index)
                // Show all letters first
                lettersLayout.children.forEach { letterView ->
                    if (letterView is TextView) {
                        letterView.visibility = View.VISIBLE
                    }
                }
                // Then hide the used ones for the selected position
                updateLetterVisibility(gameManager.getGuessesForPosition(index))
                updateUsedLettersDisplay(gameManager.getGuessesForPosition(index))
                charTextViews.forEachIndexed { i, tv ->
                    tv.setBackgroundColor(
                        if (i == index)
                            ContextCompat.getColor(this, R.color.selected_position)
                        else
                            Color.TRANSPARENT
                    )
                }
            }
        }
    }

    private fun startTimer(timeLeft: Long = 60000L) {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer display
                timerTimeLeft = millisUntilFinished
                secondsElapsed = ((millisUntilFinished / 1000).toInt())
                updateTimerDisplay()
            }
            override fun onFinish() {
                gameLostTextView.visibility = View.VISIBLE
                gameLostTextView.text = "Time's Up!"
                lettersLayout.visibility = View.GONE
                mediaPlayer.pause() // Stop the background music
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun updateTimerDisplay() {
        val minutes = secondsElapsed / 60
        val seconds = secondsElapsed % 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun showGameLost(wordToGuess: String) {
        updateWordDisplay(wordToGuess, gameManager.getSelectedPosition())
        gameLostTextView.visibility = View.VISIBLE
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.game7))
        lettersLayout.visibility = View.GONE
    }

    private fun showGameWon(wordToGuess: String) {
        updateWordDisplay(wordToGuess, gameManager.getSelectedPosition())
        gameWonTextView.visibility = View.VISIBLE
        lettersLayout.visibility = View.GONE
    }

    private fun startNewGame() {
        gameLostTextView.visibility = View.GONE
        gameWonTextView.visibility = View.GONE
        charTextViews.clear()
        val gameState = gameManager.startNewGame()
        hintButton.visibility = View.VISIBLE
        lettersLayout.visibility = View.VISIBLE
        startTimer()
        mediaPlayer.start() // Resume background music
        updateUI(gameState)
    }

    companion object {
        lateinit var soundPool: SoundPool
        var dingSound: Int = 0
        var buzzSound: Int = 0
        // Play ding sound (correct guess)
        fun playDing() {

            soundPool.play(dingSound, 1f, 1f, 0, 0, 1f)
        }

        // Play buzz sound (incorrect guess)
        fun playBuzz() {
            soundPool.play(buzzSound, 1f, 1f, 0, 0, 1f)
        }
    }

    // Override onPause to stop the music and cancel the timer when the app is paused
    override fun onPause() {
        super.onPause()
        mediaPlayer.pause() // Pause the background music
        timer?.cancel() // Cancel the timer
    }

    // Override onResume to resume the music and timer when the app comes back to the foreground
    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start() // Resume background music
        }
        startTimer(timerTimeLeft) // Start the timer again if needed
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("remainingTime", timerTimeLeft)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        timerTimeLeft = savedInstanceState.getLong("remainingTime", 60000L)
    }
    // Override onStop to stop the music when the app is stopped
    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop() // Stop the music when the app is stopped
        }
        stopTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop() // Stop background music when leaving the activity
        mediaPlayer.release() // Release resources
        soundPool.release() // Release sound effects resources
        stopTimer() // Clean up timer when activity is destroyed
    }
}