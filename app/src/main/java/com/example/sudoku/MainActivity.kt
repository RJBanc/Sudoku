package com.example.sudoku

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LiveData
import com.example.sudoku.data.backup.BackupManager
import com.example.sudoku.ui.theme.Mint200
import com.example.sudoku.ui.theme.Mint700
import com.example.sudoku.ui.theme.SudokuTheme
import java.io.FileNotFoundException

class MainActivity : ComponentActivity() {
    val sudoku = SudokuLogic()
    lateinit var backupManager: BackupManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backupManager = BackupManager(this, sudoku)
        setContent {
            SudokuTheme {
                // A surface container using the 'background' color from the theme
                this.window.statusBarColor = MaterialTheme.colors.background.toArgb()
                WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = !isSystemInDarkTheme()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Game(sudokuGame = sudoku)
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        if (!sudoku.isRunning)
            try {
                backupManager.restoreBackup()
            } catch(e: FileNotFoundException) {
                sudoku.newGame()
            }
    }

    override fun onPause() {
        super.onPause()
        if (sudoku.isRunning) {
            backupManager.createBackup()
        }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameInfo(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuLogic
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

    Row (
        modifier = modifier
            .height(55.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Button(
            modifier = modifier.fillMaxHeight(),
            onClick = { sudokuGame.newGame() }
        ) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "New Game",
            )
        }
        Spacer(modifier = modifier.width(10.dp))
        Box(
            modifier = modifier
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = difficulty[selectedDifficulty]!!,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    difficulty.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                if (item.key != selectedDifficulty) {
                                    showConfirm = true
                                }
                                selectedDifficulty = item.key
                                expanded = false
                            }
                        ) {
                            Text(text = item.value)
                        }
                    }
                }
            }
        }
    }
    if (showConfirm)
        NewGameConfirmation(
            modifier = modifier,
            confirm = {
                if (it)
                    sudokuGame.newGame(selectedDifficulty)
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
    sudokuGame: SudokuLogic,
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
        colors = buttonColors(
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
fun SudokuBox(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuLogic,
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
                colors = buttonColors(
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
                colors = buttonColors(
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
    sudokuGame: SudokuLogic
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

@Composable
fun Game(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuLogic
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
        GameInfo(modifier = modifier, sudokuGame = sudokuGame)
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


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuTheme {
        val sudoku = SudokuLogic()

        sudoku.fieldSelected(2, 6)
        sudoku.setFieldNumber("2", false)
        sudoku.fieldSelected(2, 5)
        sudoku.setFieldNumber("2", false)
        sudoku.fieldSelected(3, 5)
        sudoku.setFieldNumber("2", false)

        Game(sudokuGame = sudoku)
        //sudoku.newGame()
    }
}