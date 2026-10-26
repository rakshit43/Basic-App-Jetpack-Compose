package com.example.ui.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import com.example.ui.main.jetpack.AnimatedScreen
import com.example.ui.main.sharedpreference.PreferManager
import kotlin.jvm.java

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme{
                AnimatedScreen {
                    val Loged_in = PreferManager.getLogin(this)
                    if(Loged_in)
                    {
                        val intent = Intent(this, TimerActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}