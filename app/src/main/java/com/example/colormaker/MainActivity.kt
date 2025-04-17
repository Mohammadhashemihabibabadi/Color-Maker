package com.example.colormaker

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.res.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

    var red by remember { mutableStateOf(1.0f) }
    var green by remember { mutableStateOf(1.0f) }
    var blue by remember { mutableStateOf(1.0f) }

    var redEnabled by remember { mutableStateOf(true) }
    var greenEnabled by remember { mutableStateOf(true) }
    var blueEnabled by remember { mutableStateOf(true) }

    var redBackup by remember { mutableStateOf(1.0f) }
    var greenBackup by remember { mutableStateOf(1.0f) }
    var blueBackup by remember { mutableStateOf(1.0f) }

    val displayColor = Color(
        if (redEnabled) red else 0f,
        if (greenEnabled) green else 0f,
        if (blueEnabled) blue else 0f
    )

    val layoutModifier = Modifier
        .fillMaxSize()
        .padding(16.dp)

    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            modifier = layoutModifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorBox(displayColor, Modifier.weight(1f))
            ControlsColumn(
                red, green, blue,
                redEnabled, greenEnabled, blueEnabled,
                onRedChange = {
                    red = it; if (redEnabled) redBackup = it
                },
                onGreenChange = {
                    green = it; if (greenEnabled) greenBackup = it
                },
                onBlueChange = {
                    blue = it; if (blueEnabled) blueBackup = it
                },
                onRedToggle = {
                    redEnabled = it; red = if (it) redBackup else 0f
                },
                onGreenToggle = {
                    greenEnabled = it; green = if (it) greenBackup else 0f
                },
                onBlueToggle = {
                    blueEnabled = it; blue = if (it) blueBackup else 0f
                },
                onReset = {
                    red = 1.0f; green = 1.0f; blue = 1.0f
                    redBackup = 1.0f; greenBackup = 1.0f; blueBackup = 1.0f
                    redEnabled = true; greenEnabled = true; blueEnabled = true
                },
                context = context,
                modifier = Modifier.weight(2f)
            )
        }
    } else {
        Column(
            modifier = layoutModifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ColorBox(displayColor, Modifier.weight(1f))
            ControlsColumn(
                red, green, blue,
                redEnabled, greenEnabled, blueEnabled,
                onRedChange = {
                    red = it; if (redEnabled) redBackup = it
                },
                onGreenChange = {
                    green = it; if (greenEnabled) greenBackup = it
                },
                onBlueChange = {
                    blue = it; if (blueEnabled) blueBackup = it
                },
                onRedToggle = {
                    redEnabled = it; red = if (it) redBackup else 0f
                },
                onGreenToggle = {
                    greenEnabled = it; green = if (it) greenBackup else 0f
                },
                onBlueToggle = {
                    blueEnabled = it; blue = if (it) blueBackup else 0f
                },
                onReset = {
                    red = 1.0f; green = 1.0f; blue = 1.0f
                    redBackup = 1.0f; greenBackup = 1.0f; blueBackup = 1.0f
                    redEnabled = true; greenEnabled = true; blueEnabled = true
                },
                context = context,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ColorBox(color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .border(4.dp, Color.Black, shape = MaterialTheme.shapes.medium)
            .padding(4.dp)
            .background(color, shape = MaterialTheme.shapes.medium)
    )
}
