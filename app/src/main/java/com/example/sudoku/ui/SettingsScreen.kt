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
import com.example.sudoku.ui.theme.SudokuTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit
) {
    val test = mapOf(
        "Night" to {},
        "Day" to {}
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
                settingName = "Hello",
                settingStatus = true,
                settingInfo = "What is love?",
                onSettingChange = {}
            )
            ToggleSetting(
                modifier = modifier,
                settingName = "World",
                settingStatus = false,
                settingInfo = "Baby don't hurt me",
                onSettingChange = {}
            )
            OptionSetting(
                modifier = modifier,
                settingName = "Yeah",
                currentSetting = "Night",
                settingInfo = "Baby don't hurt me",
                settingOptions = test
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
    var currOption by remember { mutableStateOf(currentSetting) }

    SettingTemplate(
        modifier = modifier,
        settingName = settingName,
        settingInfo = settingInfo
    ) {
        Box {
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(currOption)
            }
            DropdownMenu(
                modifier = modifier,
                expanded = expanded,
                onDismissRequest = { expanded = !expanded }
            ) {
                settingOptions.forEach {
                    DropdownMenuItem(
                        onClick = {
                            currOption = it.key
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
    var checked by remember { mutableStateOf(settingStatus) }

    SettingTemplate(
        modifier = modifier,
        settingName = settingName,
        settingInfo = settingInfo
    ) {
        Switch(
            modifier = modifier,
            checked = checked,
            onCheckedChange = {
                checked = !checked
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