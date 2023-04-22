package com.example.sudoku.ui

import android.text.format.DateUtils
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.R
import com.example.sudoku.ui.theme.Mint200
import com.example.sudoku.ui.theme.Mint700
import com.example.sudoku.viewmodel.Difficulty
import com.example.sudoku.viewmodel.SettingsViewModel
import com.example.sudoku.viewmodel.SudokuField
import com.example.sudoku.viewmodel.SudokuViewModel
import java.util.*

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuViewModel,
    onSettingsClicked: () -> Unit
) {
    var showHints by remember { mutableStateOf(false) }
    var takeNotes by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        GameInfo(
            modifier = modifier,
            sudokuGame = sudokuGame,
            onSettingsClicked = onSettingsClicked
        )
        Spacer(modifier = modifier.height(10.dp))
        SudokuBox(modifier = modifier, sudokuGame = sudokuGame, showHints = showHints)
        Spacer(modifier = modifier.height(10.dp))
        AssistBar(modifier = modifier,
            takeNotes = takeNotes,
            showHints = showHints,
            noteCallback = { takeNotes = !takeNotes },
            hintCallback = { showHints = !showHints },
            undoCallback = { sudokuGame.undo() }
        )
        Spacer(modifier = modifier.height(10.dp))
        NumPad(modifier = modifier, takeNotes = takeNotes, sudokuGame = sudokuGame)
    }
    GameFinished(sudokuGame = sudokuGame)
}

@Composable
fun GameFinished(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuViewModel
) {
    val isFinished by sudokuGame.isCompleted.observeAsState()
    val settings: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val initialNotes = settings.initialNotes.collectAsState().value

    if (isFinished == true) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {  },
            title = { Text("SUCCESS") },
            text = { Text("You did it!") },
            buttons = {
                Button(
                    modifier = modifier,
                    onClick = { sudokuGame.startNewGame(initialNotes = initialNotes) }
                ) {
                    Text("Start New Game!")
                }
            }
        )
    }
}

@Composable
fun NewGameConfirmation(
    modifier: Modifier = Modifier,
    confirm: (Boolean) -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { confirm(false) },
        title = { Text("New Game") },
        text = { Text("Do you want to start a new game?") },
        confirmButton = {
            Button(
                modifier = modifier,
                onClick = { confirm(true) }
            ) {
                Text("Start New Game!")
            }
        },
        dismissButton = {
            Button(
                modifier = modifier,
                onClick = { confirm(false) }
            ) {
                Text("Cancel")
            }

        }
    )
}

@Composable
fun GameTimer(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuViewModel
) {
    val isRunning by sudokuGame.isRunning.observeAsState()
    val timeElapsed by sudokuGame.timeElapsed.observeAsState()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            modifier = modifier.fillMaxHeight(),
            onClick = {
                if (isRunning == true)
                    sudokuGame.pauseGame()
                else
                    sudokuGame.resumeGame()
            }
        ) {
            if (isRunning == true)
                Icon(
                    painterResource(id = R.drawable.pause_24px),
                    contentDescription = "Pause Game",
                    modifier = modifier,
                )
            else
                Icon(
                    painterResource(id = R.drawable.play_24px),
                    contentDescription = "Pause Game",
                    modifier = modifier,
                )
        }
        Text(
            modifier = modifier,
            text = DateUtils.formatElapsedTime(timeElapsed ?: 0L)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameInfo(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuViewModel,
    onSettingsClicked: () -> Unit
) {
    val difficulty = mapOf(
        Difficulty.BEGINNER to "Beginner",
        Difficulty.EASY to "Easy",
        Difficulty.MEDIUM to "Medium",
        Difficulty.HARD to "Hard",
        Difficulty.EVIL to "Evil",
        Difficulty.DIABOLICAL to "Diabolical"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(sudokuGame.difficulty) }
    var showConfirm by remember { mutableStateOf(false) }

    val settings: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val confirmNewGame = settings.confirmNewGame.collectAsState().value
    val initialNotes = settings.initialNotes.collectAsState().value

    Row (
        modifier = modifier
            .height(55.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = modifier.weight(1f)
        ) {
            Button(
                modifier = modifier.fillMaxHeight(),
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "New Game",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                difficulty.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedDifficulty = item.key
                            expanded = false
                            if (confirmNewGame)
                                showConfirm = true
                            else
                                sudokuGame.startNewGame(selectedDifficulty, initialNotes)
                        }
                    ) {
                        Text(text = item.value)
                    }
                }
            }
        }
        GameTimer(modifier = modifier.weight(1f), sudokuGame = sudokuGame)
        Row (
            modifier = modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                modifier = modifier,
                onClick = onSettingsClicked
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    }
    if (showConfirm)
        NewGameConfirmation(
            modifier = modifier,
            confirm = {
                if (it)
                    sudokuGame.startNewGame(selectedDifficulty, initialNotes)
                else
                    selectedDifficulty = sudokuGame.difficulty
                showConfirm = false
            }
        )
}

@Composable
fun Grid(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colors.onBackground,
    cellColor: Color = MaterialTheme.colors.onSurface
){
    Canvas(modifier = modifier.size(360.dp)) {
        val borderDrawWidth = 5f
        val cellDrawWidth = 2f

        for (i in 1..8) {
            drawLine(
                color = cellColor,
                start = Offset(0f, i * size.height / 9),
                end = Offset(size.width, i * size.height / 9),
                strokeWidth = cellDrawWidth
            )

            drawLine(
                color = cellColor,
                start = Offset(i * size.width / 9, 0f),
                end = Offset(i * size.width / 9, size.height),
                strokeWidth = cellDrawWidth
            )
        }

        drawRect(
            color = borderColor,
            style = Stroke(width = borderDrawWidth)
        )

        for (i in 1..2) {
            drawLine(
                color = borderColor,
                start = Offset(0f, i * size.height / 3),
                end = Offset(size.width, i * size.height / 3),
                strokeWidth = borderDrawWidth
            )

            drawLine(
                color = borderColor,
                start = Offset(i * size.width / 3, 0f),
                end = Offset(i * size.width / 3, size.height),
                strokeWidth = borderDrawWidth
            )
        }
    }
}

@Composable
fun SudokuButton(
    modifier: Modifier = Modifier,
    fieldData: LiveData<SudokuField>,
    sudokuGame: SudokuViewModel,
    row: Int,
    col: Int,
    showHint: Boolean
) {
    val sudokuField by fieldData.observeAsState()

    val highlightedColor = MaterialTheme.colors.primary
    val selectedColor = MaterialTheme.colors.primaryVariant

    var buttonColor = MaterialTheme.colors.surface
    if (sudokuField!!.isSelected)
        buttonColor = selectedColor
    else if (sudokuField!!.isHighlighted)
        buttonColor = highlightedColor

    val contentColor = if (sudokuField!!.isSelected || sudokuField!!.isHighlighted) {
        if (sudokuField!!.isEnabled)
            Mint700
        else
            MaterialTheme.colors.onPrimary
    }
    else {
        if (sudokuField!!.isEnabled)
            Mint200
        else
            MaterialTheme.colors.onSurface
    }


    Button (
        modifier = modifier
            .height(40.dp)
            .width(40.dp),
        elevation = null,
        shape = GenericShape { size, _ ->
            addRect(
                Rect(
                    left = 0f ,
                    right = size.width,
                    top = 0f ,
                    bottom = size.height
                )
            )
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonColor,
            disabledBackgroundColor = buttonColor,
        ),
        enabled = true,
        onClick = {
            sudokuGame.fieldSelected(row, col)
        }
    ){
        Box() {
            if (sudokuField!!.number != null) {
                Text(
                    text = sudokuField!!.number ?: "",
                    color = if(sudokuField!!.solution == (sudokuField!!.number ?: "") || !showHint)
                        contentColor
                    else
                        Color.Red
                )
            } else {
                Column(modifier = modifier
                    .requiredWidth(40.dp)
                    .wrapContentSize(Alignment.Center)
                ) {
                    for (i in 0..2) {
                        Row (modifier = modifier
                            .wrapContentSize(Alignment.Center)
                        ) {
                            for (j  in 0..2) {
                                Column(modifier = modifier
                                    .requiredWidth(10.dp)
                                    .wrapContentSize(
                                        Alignment.Center
                                    )
                                ) {
                                    Text(
                                        text =
                                        if (sudokuField!!.notes and (1 shl (i * 3 + j)) > 0)
                                            (i * 3 + j + 1).toString()
                                        else
                                            "",
                                        fontSize = 6.sp,
                                        color = contentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HideGame(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuViewModel
) {
    val isRunning by sudokuGame.isRunning.observeAsState()
    val surface = MaterialTheme.colors.surface

    if (isRunning == false)
        Canvas(
            modifier = modifier.size(360.dp)
        ) {
            drawRect(
                color = surface,
            )
        }
}

@Composable
fun SudokuBox(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuViewModel,
    showHints: Boolean
) {
    Box()
    {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            for (i in 0..8){
                Row(
                    modifier = modifier
                ) {
                    for (j in 0..8){
                        SudokuButton(
                            fieldData = sudokuGame.getField(row = i, col = j),
                            sudokuGame = sudokuGame,
                            row = i,
                            col = j,
                            showHint = showHints
                        )
                    }
                }
            }
        }
        Grid()
        HideGame(modifier = modifier, sudokuGame = sudokuGame)
    }
}

@Composable
fun AssistBar(
    modifier: Modifier = Modifier,
    takeNotes: Boolean,
    showHints: Boolean,
    noteCallback: () -> Unit,
    hintCallback: () -> Unit,
    undoCallback: () -> Unit
) {
    Row (
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column (
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.width(60.dp),
                onClick = hintCallback,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (showHints)
                        MaterialTheme.colors.primaryVariant
                    else
                        MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    painterResource(id = R.drawable.done_all_48px),
                    contentDescription = "Show Mistakes",
                    modifier = modifier,
                )
            }
            Text("Show Mistakes", Modifier)
        }
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button (
                modifier = Modifier.width(60.dp),
                onClick = undoCallback
            ) {
                Icon(
                    painterResource(id = R.drawable.undo_48px),
                    contentDescription = "Undo",
                    modifier = modifier,
                )
            }
            Text("Undo", Modifier)
        }
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.width(60.dp),
                onClick = noteCallback,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (takeNotes)
                        MaterialTheme.colors.primaryVariant
                    else
                        MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    painterResource(id = R.drawable.app_registration_48px),
                    contentDescription = "Switch Notes",
                    modifier = modifier,
                )
            }
            Text(text = "Take Notes", modifier = Modifier)
        }
    }
}

@Composable
fun NumPad(
    modifier: Modifier = Modifier,
    takeNotes: Boolean,
    sudokuGame: SudokuViewModel
) {
    for (i in 0..2) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = modifier.width(10.dp))
            for (j in 0..2) {
                Button(
                    modifier= modifier
                        .height(55.dp)
                        .width(55.dp),
                    onClick = {
                        sudokuGame.setFieldNumber((i * 3 + j + 1).toString(), takeNotes)
                    }
                ) {
                    Text(
                        text = (i * 3 + j + 1).toString(),
                        fontSize = 25.sp
                    )
                }
                Spacer(modifier = modifier.width(10.dp))
            }
        }
        Spacer(modifier = modifier.height(10.dp))
    }
}