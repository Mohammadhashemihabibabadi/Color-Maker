package com.example.newcolormaker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ColorMakerApp()
            }
        }
    }
}

@Composable
fun ColorMakerApp() {
    val config = LocalConfiguration.current

    val red = remember { mutableFloatStateOf(1f) }
    val green = remember { mutableFloatStateOf(1f) }
    val blue = remember { mutableFloatStateOf(1f) }

    val redActive = remember { mutableStateOf(true) }
    val greenActive = remember { mutableStateOf(true) }
    val blueActive = remember { mutableStateOf(true) }

    val prevRed = remember { mutableFloatStateOf(1f) }
    val prevGreen = remember { mutableFloatStateOf(1f) }
    val prevBlue = remember { mutableFloatStateOf(1f) }

    fun resetValues() {
        red.floatValue = 1f; green.floatValue = 1f; blue.floatValue = 1f
        prevRed.floatValue = 1f; prevGreen.floatValue = 1f; prevBlue.floatValue = 1f
        redActive.value = true; greenActive.value = true; blueActive.value = true
    }

    val displayRed = if (redActive.value) red.floatValue else 0f
    val displayGreen = if (greenActive.value) green.floatValue else 0f
    val displayBlue = if (blueActive.value) blue.floatValue else 0f

    val backgroundColor = Color(displayRed, displayGreen, displayBlue)
    val isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            ColorBox(backgroundColor, Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.33f)
                .padding(16.dp))
            ColorControls(
                red, green, blue,
                redActive, greenActive, blueActive,
                prevRed, prevGreen, prevBlue,
                onReset = { resetValues() }
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            ColorBox(backgroundColor, Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp))
            ColorControls(
                red, green, blue,
                redActive, greenActive, blueActive,
                prevRed, prevGreen, prevBlue,
                onReset = { resetValues() }
            )
        }
    }
}

@Composable
fun ColorBox(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
            .background(color)
    )
}

@Composable
fun ColorControls(
    red: MutableFloatState,
    green: MutableFloatState,
    blue: MutableFloatState,
    redActive: MutableState<Boolean>,
    greenActive: MutableState<Boolean>,
    blueActive: MutableState<Boolean>,
    prevRed: MutableFloatState,
    prevGreen: MutableFloatState,
    prevBlue: MutableFloatState,
    onReset: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ColorRow("Red", red, redActive, prevRed, context, Color.Red)
        ColorRow("Green", green, greenActive, prevGreen, context, Color.Green)
        ColorRow("Blue", blue, blueActive, prevBlue, context, Color.Blue)

        Button(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Reset")
        }
    }
}

@Composable
fun ColorRow(
    label: String,
    value: MutableFloatState,
    active: MutableState<Boolean>,
    prev: MutableFloatState,
    context: android.content.Context,
    trackColor: Color
) {
    val textValue = remember { mutableStateOf(String.format(Locale.US, "%.2f", value.floatValue)) }

    LaunchedEffect(value.floatValue) {
        textValue.value = String.format(Locale.US, "%.2f", value.floatValue)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 32.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Switch(
                checked = active.value,
                onCheckedChange = {
                    active.value = it
                    if (it) value.floatValue = prev.floatValue
                    else {
                        prev.floatValue = value.floatValue
                        value.floatValue = 0f
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = trackColor,
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.White
                )
            )
        }

        Slider(
            value = value.floatValue,
            onValueChange = {
                value.floatValue = it
                if (active.value) prev.floatValue = it
            },
            valueRange = 0f..1f,
            steps = 100,
            enabled = active.value,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                activeTrackColor = trackColor,
                inactiveTrackColor = trackColor.copy(alpha = 0.3f),
                thumbColor = trackColor
            )
        )

        TextField(
            value = textValue.value,
            onValueChange = {
                try {
                    val num = it.toFloat()
                    if (num in 0f..1f) {
                        textValue.value = it
                        value.floatValue = num
                        if (active.value) prev.floatValue = num
                    } else {
                        Toast.makeText(context, "Enter value 0 to 1.0", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.width(80.dp),
            enabled = active.value,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = if (active.value) Color.Black else Color.Gray)
        )
    }
}
