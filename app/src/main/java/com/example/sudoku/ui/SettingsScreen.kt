package com.example.sudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.ui.theme.SudokuTheme
import com.example.sudoku.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit
) {
    val settings: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)

    val nightMode = mapOf(
        "Dark" to { settings.selectNightMode("on") },
        "Light" to { settings.selectNightMode("off") },
        "System" to { settings.selectNightMode("system") }
    )



    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                modifier = modifier,
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Return to game"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = modifier
                .padding(it)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ToggleSetting(
                modifier = modifier,
                settingName = "Keep Screen On",
                settingStatus = settings.screenOn.collectAsState().value,
                settingInfo = "Keep the screen permanently on while playing the game.",
                onSettingChange = { set -> settings.selectScreenOn(set) }
            )
            ToggleSetting(
                modifier = modifier,
                settingName = "Take Initial Notes",
                settingStatus = settings.initialNotes.collectAsState().value,
                settingInfo = "When starting a new game, automatically write down all initial notes. Change takes effect in new game.",
                onSettingChange = { set -> settings.selectInitialNotes(set) }
            )
            ToggleSetting(
                modifier = modifier,
                settingName = "Confirm New Game",
                settingStatus = settings.confirmNewGame.collectAsState().value,
                settingInfo = "Ask for confirmation before starting a new game.",
                onSettingChange = { set -> settings.selectNewGameConfirm(set) }
            )
            OptionSetting(
                modifier = modifier,
                settingName = "Dark Mode",
                currentSetting = when(settings.nightMode.collectAsState().value) {
                  "on" -> "Dark"
                  "off" -> "Light"
                  "system" -> "System"
                  else -> "Error"
                },
                settingInfo = "Set the dark mode of this app.",
                settingOptions = nightMode
            )
        }
    }
}

@Composable
fun OptionSetting(
    modifier: Modifier = Modifier,
    settingName: String,
    settingInfo: String = "",
    currentSetting: String,
    settingOptions: Map<String, () -> Unit>
) {
    var expanded by remember { mutableStateOf(false) }

    SettingTemplate(
        modifier = modifier,
        settingName = settingName,
        settingInfo = settingInfo
    ) {
        Box {
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(currentSetting)
            }
            DropdownMenu(
                modifier = modifier,
                expanded = expanded,
                onDismissRequest = { expanded = !expanded }
            ) {
                settingOptions.forEach {
                    DropdownMenuItem(
                        onClick = {
                            expanded = !expanded
                            it.value()
                        }
                    ) {
                        Text(text = it.key)
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleSetting(
    modifier: Modifier = Modifier,
    settingName: String,
    settingInfo: String = "",
    settingStatus: Boolean,
    onSettingChange: ((Boolean) -> Unit)?
) {

    SettingTemplate(
        modifier = modifier,
        settingName = settingName,
        settingInfo = settingInfo
    ) {
        Switch(
            modifier = modifier,
            checked = settingStatus,
            onCheckedChange = {
                onSettingChange?.invoke(it) }
        )
    }
}

@Composable
fun SettingTemplate(
    modifier: Modifier = Modifier,
    settingName: String,
    settingInfo: String = "",
    setting: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(settingName)
        Spacer(modifier.width(10.dp))
        SettingDetails(
            modifier = modifier,
            settingInfo = settingInfo
        )
        Spacer(modifier.weight(1f))
        setting()
    }
}

@Composable
fun SettingDetails (
    modifier: Modifier = Modifier,
    settingInfo: String = ""
) {
    var showDetails by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero)}

    IconButton(
        modifier = modifier,
        onClick = { showDetails = !showDetails }
    ) {
        Icon(
            Icons.Filled.Info,
            contentDescription = "Settings Info"
        )
        if (showDetails)
            Popup(
                alignment = Alignment.BottomEnd,
                offset = IntOffset(size.width, size.height),
                onDismissRequest = { showDetails = !showDetails }
            ) {
                Text(
                    modifier = modifier
                        .onGloballyPositioned { size = it.size }
                        .background(MaterialTheme.colors.surface),
                    text = settingInfo
                )
            }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SudokuTheme {
        SettingsScreen(onBackClicked = {})
    }
}