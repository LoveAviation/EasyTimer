package com.example.timerrxjava

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.timerrxjava.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TimerVM by viewModels()

    private var seconds = 0
    private var isPaused = true
    private var isRunning = false
    private var timerJob: Job? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.choosingTime.setOnClickListener {
            showCustomTimePickerDialog()
        }

        binding.startButton.setOnClickListener {
            if (isPaused && seconds != 0) {
                isPaused = false
                binding.pauseButton.text = "Pause"
                binding.pauseButton.isEnabled = true

                if (!isRunning) {
                    isRunning = true
                    startNewTimer()
                } else {
                    viewModel.continueTimer()
                }
            } else if (seconds == 0) {
                Toast.makeText(this, "Choose time", Toast.LENGTH_LONG).show()
            }
        }

        binding.pauseButton.setOnClickListener {
            if (!isPaused) {
                viewModel.pauseTimer()
                isPaused = true
                binding.pauseButton.text = "Delete"
            } else {
                seconds = 0
                viewModel.deleteTimer()
                isRunning = false
                binding.time.text = "00:00"
                binding.pauseButton.isEnabled = false
                binding.pauseButton.text = "Pause"
                timerJob?.cancel()
                binding.progressTime.progress = 0
                binding.progressTime.visibility = View.GONE
            }
        }
    }

    private fun startNewTimer() {
        timerJob?.cancel()

        timerJob = lifecycleScope.launch {
            viewModel.startTimer(seconds).collect { time ->
                binding.time.text = String.format("%02d:%02d", time / 60, time % 60)
                binding.progressTime.progress = time
                if (time == 0) {
                    isPaused = true
                    isRunning = false
                    binding.pauseButton.text = "Pause"
                    binding.pauseButton.isEnabled = false
                    binding.progressTime.visibility = View.GONE
                    seconds = 0
                }
            }
        }
    }

    private fun showCustomTimePickerDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_time_picker, null)
        val minutePicker = view.findViewById<NumberPicker>(R.id.minutePicker)
        val secondPicker = view.findViewById<NumberPicker>(R.id.secondPicker)

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        AlertDialog.Builder(this)
            .setTitle("Choose minutes and seconds")
            .setView(view)
            .setPositiveButton("OK") { _, _ ->
                binding.time.text = String.format("%02d:%02d", minutePicker.value, secondPicker.value)
                val secondTotal = (minutePicker.value * 60) + secondPicker.value
                binding.progressTime.max = secondTotal
                binding.progressTime.visibility = View.VISIBLE
                binding.progressTime.progress = secondTotal
                seconds = secondTotal
            }
            .setNegativeButton("Back", null)
            .show()
    }
}