package com.jzbrooks.splashdown.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import com.jzbrooks.splashdown.ui.theme.SplashdownTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashdownTheme {
                Scaffold {
                    ImageSearchScreen(it)
                }
            }
        }
    }
}
