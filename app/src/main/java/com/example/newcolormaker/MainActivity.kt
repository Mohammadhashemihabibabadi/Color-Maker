// MainActivity.kt
package com.example.newcolormaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newcolormaker.ui.ColorMakerApp
import com.example.newcolormaker.viewmodel.ColorViewModel
import com.example.newcolormaker.viewmodel.ColorViewModelFactory

val ComponentActivity.dataStore by preferencesDataStore(name = "color_prefs")

class MainActivity : ComponentActivity() {
    private val viewModel: ColorViewModel by viewModels {
        ColorViewModelFactory(this, this.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColorMakerApp(viewModel)
        }
    }
}

// ColorViewModel.kt
package com.example.newcolormaker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ColorViewModel(
    private val state: SavedStateHandle,
    private val dataStore: DataStore<Preferences>
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
            dataStore.data.firstOrNull()?.let {
                state["red"] = it[redKey] ?: 1f
                state["green"] = it[greenKey] ?: 1f
                state["blue"] = it[blueKey] ?: 1f
                state["redActive"] = it[redActiveKey] ?: true
                state["greenActive"] = it[greenActiveKey] ?: true
                state["blueActive"] = it[blueActiveKey] ?: true
            }
        }
    }

    fun updateColor(color: String, value: Float) {
        state[color] = value
        saveToDataStore()
    }

    fun updateActive(color: String, value: Boolean) {
        state[color] = value
        saveToDataStore()
    }

    private fun saveToDataStore() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[redKey] = red.value
                prefs[greenKey] = green.value
                prefs[blueKey] = blue.value
                prefs[redActiveKey] = redActive.value
                prefs[greenActiveKey] = greenActive.value
                prefs[blueActiveKey] = blueActive.value
            }
        }
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
}

class ColorViewModelFactory(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColorViewModel(SavedStateHandle(), dataStore) as T
    }
}

// ColorMakerApp.kt
package com.example.newcolormaker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newcolormaker.viewmodel.ColorViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*

@Composable
fun ColorMakerApp(viewModel: ColorViewModel) {
    val config = LocalConfiguration.current
    val red = viewModel.red.collectAsStateWithLifecycle().value
    val green = viewModel.green.collectAsStateWithLifecycle().value
    val blue = viewModel.blue.collectAsStateWithLifecycle().value
    val redActive = viewModel.redActive.collectAsStateWithLifecycle().value
    val greenActive = viewModel.greenActive.collectAsStateWithLifecycle().value
    val blueActive = viewModel.blueActive.collectAsStateWithLifecycle().value

    val displayRed = if (redActive) red else 0f
    val displayGreen = if (greenActive) green else 0f
    val displayBlue = if (blueActive) blue else 0f
    val backgroundColor = Color(displayRed, displayGreen, displayBlue)

    if (config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
        Row(modifier = Modifier.fillMaxSize()) {
            ColorBox(backgroundColor, Modifier.weight(1f))
            ColorControls(viewModel)
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            ColorBox(backgroundColor, Modifier.weight(1f))
            ColorControls(viewModel)
        }
    }
}

// Add Composables for ColorBox, ColorControls, ColorRow as you had before, but use viewModel.updateColor and viewModel.updateActive instead of local state.

// Example in ColorRow (pseudocode):
// onValueChange = { viewModel.updateColor("red", newValue) }
// onCheckedChange = { viewModel.updateActive("redActive", newState) }
