package com.example.ui.main.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import com.example.ui.main.jetpack.CheckInUi
import com.example.ui.main.jetpack.Theme

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                CheckInUi()
            }
        }
    }
}
