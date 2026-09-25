package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.BtebResultTheme
import com.example.viewmodel.BtebViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BtebViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BtebResultTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
