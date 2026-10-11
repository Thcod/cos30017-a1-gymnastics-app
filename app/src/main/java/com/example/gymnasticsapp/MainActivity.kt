package com.example.gymnasticsapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Tracks a gymnast's floor routine score. The screen is drawn from three
 * values ([score], [completed], [deducted]) in [updateUi].
 */
class MainActivity : AppCompatActivity() {

    private var score = 0
    private var completed = 0 // elements finished, 0..10
    private var deducted = false

    private lateinit var scoreText: TextView
    private lateinit var elementText: TextView
    private lateinit var statusText: TextView
    private lateinit var performButton: Button
    private lateinit var deductionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        elementText = findViewById(R.id.elementText)
        statusText = findViewById(R.id.statusText)
        performButton = findViewById(R.id.performButton)
        deductionButton = findViewById(R.id.deductionButton)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score")
            completed = savedInstanceState.getInt("completed")
            deducted = savedInstanceState.getBoolean("deducted")
        }

        performButton.setOnClickListener { perform() }
        deductionButton.setOnClickListener { deduct() }
        findViewById<Button>(R.id.resetButton).setOnClickListener { reset() }

        updateUi()
    }

    // Keeps the routine when the screen rotates
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("completed", completed)
        outState.putBoolean("deducted", deducted)
    }

    private fun perform() {
        completed++
        val points = when (completed) {
            in 1..3 -> 1
            in 4..7 -> 2
            else -> 3
        }
        score += points
        Log.d(TAG, "Perform: element $completed, +$points, score $score")
        updateUi()
    }

    private fun deduct() {
        score = maxOf(score - 2, 0)
        deducted = true
        Log.d(TAG, "Deduction: score $score")
        updateUi()
    }

    private fun reset() {
        score = 0
        completed = 0
        deducted = false
        Log.d(TAG, "Reset")
        updateUi()
    }

    private fun updateUi() {
        val element = minOf(completed + 1, 10) // stays at 10 once complete
        val colour = when (element) {
            in 1..3 -> R.color.zone_basic
            in 4..7 -> R.color.zone_intermediate
            else -> R.color.zone_advanced
        }
        scoreText.text = score.toString()
        scoreText.setTextColor(ContextCompat.getColor(this, colour))
        elementText.text = getString(R.string.element_format, element)
        statusText.text = if (completed == 10) getString(R.string.routine_complete) else ""

        // No performing after a deduction or once all 10 elements are done
        performButton.isEnabled = !deducted && completed < 10
        // No deduction before element 1, after one is taken, or after element 10
        deductionButton.isEnabled = completed in 1..9 && !deducted
    }

    companion object {
        private const val TAG = "GymnasticsApp"
    }
}