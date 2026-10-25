package com.example.myexpressiveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myexpressiveapp.presentation.home.HomeScreen
import com.example.myexpressiveapp.presentation.theme.ExpressiveUi
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @ExpressiveUi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyExpressiveTheme {
                HomeScreen()
            }
        }
    }
}
