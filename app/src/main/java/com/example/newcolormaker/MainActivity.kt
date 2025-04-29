// ✅ MainActivity.kt – Final Version with Framed ColorBox and Switch Styling
package com.example.newcolormaker

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

val Context.dataStore by preferencesDataStore("color_prefs")

class MainActivity : ComponentActivity() {
    private val viewModel: ColorViewModel by viewModels {
        ColorViewModelFactory(applicationContext, applicationContext.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ColorMakerApp(viewModel)
            }
        }
    }
}

class ColorViewModel(
    private val state: SavedStateHandle,
    private val dataStore: androidx.datastore.core.DataStore<Preferences>
) : ViewModel() {
    private val redKey = floatPreferencesKey("red")
    private val greenKey = floatPreferencesKey("green")
    private val blueKey = floatPreferencesKey("blue")
    private val redActiveKey = booleanPreferencesKey("redActive")
    private val greenActiveKey = booleanPreferencesKey("greenActive")
    private val blueActiveKey = booleanPreferencesKey("blueActive")

    val red = state.getStateFlow("red", 1f)
    val green = state.getStateFlow("green", 1f)
    val blue = state.getStateFlow("blue", 1f)
    val redActive = state.getStateFlow("redActive", true)
    val greenActive = state.getStateFlow("greenActive", true)
    val blueActive = state.getStateFlow("blueActive", true)

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.firstOrNull()
            state["red"] = prefs?.get(redKey) ?: 1f
            state["green"] = prefs?.get(greenKey) ?: 1f
            state["blue"] = prefs?.get(blueKey) ?: 1f
            state["redActive"] = prefs?.get(redActiveKey) ?: true
            state["greenActive"] = prefs?.get(greenActiveKey) ?: true
            state["blueActive"] = prefs?.get(blueActiveKey) ?: true
        }
    }

    fun updateColor(key: String, value: Float) {
        state[key] = value
        saveToDataStore()
    }

    fun updateActive(key: String, value: Boolean) {
        state[key] = value
        saveToDataStore()
    }

    fun resetAll() {
        state["red"] = 1f
        state["green"] = 1f
        state["blue"] = 1f
        state["redActive"] = true
        state["greenActive"] = true
        state["blueActive"] = true
        saveToDataStore()
    }

    private fun saveToDataStore() {
        viewModelScope.launch {
            dataStore.edit {
                it[redKey] = red.value
                it[greenKey] = green.value
                it[blueKey] = blue.value
                it[redActiveKey] = redActive.value
                it[greenActiveKey] = greenActive.value
                it[blueActiveKey] = blueActive.value
            }
        }
    }
}

class ColorViewModelFactory(
    private val context: Context,
    private val dataStore: androidx.datastore.core.DataStore<Preferences>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColorViewModel(SavedStateHandle(), dataStore) as T
    }
}

@Composable
fun ColorMakerApp(viewModel: ColorViewModel) {
    val config = LocalConfiguration.current
    val red by viewModel.red.collectAsState()
    val green by viewModel.green.collectAsState()
    val blue by viewModel.blue.collectAsState()
    val redActive by viewModel.redActive.collectAsState()
    val greenActive by viewModel.greenActive.collectAsState()
    val blueActive by viewModel.blueActive.collectAsState()

    val displayRed = if (redActive) red else 0f
    val displayGreen = if (greenActive) green else 0f
    val displayBlue = if (blueActive) blue else 0f

    val backgroundColor = Color(displayRed, displayGreen, displayBlue)

    Column(modifier = Modifier.fillMaxSize()) {
        ColorBox(backgroundColor, Modifier.weight(1f))
        ColorControls(viewModel)
    }
}

@Composable
fun ColorBox(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(width = 3.dp, color = Color.Black, shape = RoundedCornerShape(24.dp))
            .background(color)
    )
}

@Composable
fun ColorControls(viewModel: ColorViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ColorRow("red", Color.Red, viewModel.red.collectAsState().value, viewModel.redActive.collectAsState().value, viewModel, context)
        ColorRow("green", Color.Green, viewModel.green.collectAsState().value, viewModel.greenActive.collectAsState().value, viewModel, context)
        ColorRow("blue", Color.Blue, viewModel.blue.collectAsState().value, viewModel.blueActive.collectAsState().value, viewModel, context)
        Button(onClick = { viewModel.resetAll() }, modifier = Modifier.fillMaxWidth()) {
            Text("Reset")
        }
    }
}

@Composable
fun ColorRow(
    label: String,
    trackColor: Color,
    currentValue: Float,
    isActive: Boolean,
    viewModel: ColorViewModel,
    context: Context
) {
    val textValue = remember(currentValue) {
        mutableStateOf(String.format(Locale.US, "%.2f", currentValue))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isActive) trackColor else Color.LightGray)
                .border(2.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Switch(
                checked = isActive,
                onCheckedChange = {
                    viewModel.updateActive("${label}Active", it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Transparent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Transparent
                )
            )
        }

        Slider(
            value = currentValue,
            onValueChange = {
                viewModel.updateColor(label, it)
            },
            valueRange = 0f..1f,
            steps = 100,
            enabled = isActive,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                activeTrackColor = trackColor,
                inactiveTrackColor = trackColor,
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
                        viewModel.updateColor(label, num)
                    } else {
                        Toast.makeText(context, "Enter value 0 to 1.0", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.width(80.dp),
            enabled = isActive,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = if (isActive) Color.Black else Color.Gray)
        )
    }
}