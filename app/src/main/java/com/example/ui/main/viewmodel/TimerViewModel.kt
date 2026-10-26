package com.example.ui.main.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

class TimerViewModel : ViewModel() {


    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String> get() = _timer

    private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private var updatedTime = 0L
    private var savedTime = 0L


    private val runnable = object : Runnable{
        override fun run() {
            elapsedTime = System.currentTimeMillis() - startTime
            updatedTime = savedTime + elapsedTime

            val totalSeconds = (updatedTime / 1000).toInt()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            _timer.value = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            handler.postDelayed(this,1000)
        }
    }


    fun checkIn()
    {
        if(!isTimerRunning)
        {
            isTimerRunning = true
            startTime = System.currentTimeMillis()
            handler.postDelayed(runnable,0)
        }
    }

    fun checkOut()
    {
        if(isTimerRunning)
        {
            isTimerRunning = false
            handler.removeCallbacks(runnable)
            savedTime = System.currentTimeMillis() - startTime
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(runnable)
        super.onCleared()
    }
}