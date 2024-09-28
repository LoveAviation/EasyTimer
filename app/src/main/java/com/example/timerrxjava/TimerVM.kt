package com.example.timerrxjava

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow

class TimerVM: ViewModel() {
    private var saveTime = 0
    private var isPaused = false

    @OptIn(FlowPreview::class)
    fun startTimer(seconds: Int): Flow<Int> = flow {
        var currentValue = seconds
        isPaused = false

        while (currentValue >= 0) {
            if (!isPaused) {
                emit(currentValue)
                delay(1000)
                currentValue -= 1
                saveTime = currentValue
            } else {
                delay(100)
            }
        }
    }.debounce(100)

    fun pauseTimer() {
        isPaused = true
    }

    fun deleteTimer(){
        isPaused = true
        saveTime = 0
    }

    fun continueTimer(){
        isPaused = false
    }

}