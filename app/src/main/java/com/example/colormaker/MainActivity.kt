package com.example.color

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Force light theme for visibility
            MaterialTheme(colorScheme = lightColorScheme()) {
                ColorMakerApp()
            }
        }
    }
}

@Composable
fun ColorMakerApp() {
    val context = LocalContext.current
    val config = LocalConfiguration.current

    var red by remember { mutableFloatStateOf(1.0f) }
    var green by remember { mutableFloatStateOf(1.0f) }
    var blue by remember { mutableFloatStateOf(1.0f) }

    var redEnabled by remember { mutableStateOf(true) }
    var greenEnabled by remember { mutableStateOf(true) }
    var blueEnabled by remember { mutableStateOf(true) }

    var redBackup by remember { mutableFloatStateOf(1.0f) }
    var greenBackup by remember { mutableFloatStateOf(1.0f) }
    var blueBackup by remember { mutableFloatStateOf(1.0f) }

    val displayColor = Color(
        if (redEnabled) red else 0f,
        if (greenEnabled) green else 0f,
        if (blueEnabled) blue else 0f
    )

    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .border(4.dp, Color.Black, shape = MaterialTheme.shapes.medium)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(displayColor, shape = MaterialTheme.shapes.medium)
                )
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Adjust Colors (0 to 1.0)", style = MaterialTheme.typography.titleLarge)

                    ColorControl("Red", red, {
                        red = it
                        if (redEnabled) redBackup = it
                    }, redEnabled, {
                        redEnabled = it
                        red = if (it) redBackup else 0f
                    }, context)

                    ColorControl("Green", green, {
                        green = it
                        if (greenEnabled) greenBackup = it
                    }, greenEnabled, {
                        greenEnabled = it
                        green = if (it) greenBackup else 0f
                    }, context)

                    ColorControl("Blue", blue, {
                        blue = it
                        if (blueEnabled) blueBackup = it
                    }, blueEnabled, {
                        blueEnabled = it
                        blue = if (it) blueBackup else 0f
                    }, context)
                }

                Button(
                    onClick = {
                        red = 1.0f; green = 1.0f; blue = 1.0f
                        redBackup = 1.0f; greenBackup = 1.0f; blueBackup = 1.0f
                        redEnabled = true; greenEnabled = true; blueEnabled = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .border(4.dp, Color.Black, shape = MaterialTheme.shapes.medium)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(displayColor, shape = MaterialTheme.shapes.medium)
                )
            }

