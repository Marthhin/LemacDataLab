package com.example.lemacdatalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lemacdatalab.ui.pantallas.PantallaInicio
import com.example.lemacdatalab.ui.theme.LemacDataLabTheme
import com.example.lemacdatalab.viewmodel.InicioViewModel

class MainActivity : ComponentActivity() {

    private val inicioViewModel = InicioViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LemacDataLabTheme {
                PantallaInicio(
                    viewModel = inicioViewModel
                )
            }
        }
    }
}