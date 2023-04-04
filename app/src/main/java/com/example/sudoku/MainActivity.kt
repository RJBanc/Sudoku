package com.example.sudoku

import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sudoku.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val sudoku = SudokuLogic()
                    Game(sudokuGame = sudoku)
                    sudoku.newGame()
                }
            }
        }
    }
}

@Composable
fun GameInfo(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuLogic
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Button(
            onClick = { sudokuGame.newGame() }
        ) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "New Game",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
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

    val contentColor = if (sudokuField!!.isSelected || sudokuField!!.isHighlighted)
        MaterialTheme.colors.onPrimary
    else
        MaterialTheme.colors.onSurface

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
        enabled = sudokuField!!.isEnabled,
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
                                        text = sudokuField!!.notes[i * 3 + j] ?: "",
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
    noteCallback: (Boolean) -> Unit,
    hintCallback: (Boolean) -> Unit
) {
    Row (
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text("Show Mistakes")
        Switch(
            modifier = Modifier,
            checked = showHints,
            onCheckedChange = hintCallback
        )
        Spacer(modifier = modifier.width(100.dp))
        Text("Notes")
        Switch(
            modifier = Modifier,
            checked = takeNotes,
            onCheckedChange = noteCallback
        )
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
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (j in 0..2) {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier= modifier
                            .height(60.dp)
                            .width(60.dp),
                        onClick = {
                            sudokuGame.setFieldNumber((i * 3 + j + 1).toString(), takeNotes)
                        }
                    ) {
                        Text(
                            text = (i * 3 + j + 1).toString(),
                            fontSize = 30.sp
                        )
                    }
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
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        GameInfo(modifier = modifier, sudokuGame = sudokuGame)
        SudokuBox(modifier = modifier, sudokuGame = sudokuGame, showHints = showHints)
        Spacer(modifier = modifier.height(32.dp))
        AssistBar(modifier = modifier,
            takeNotes = takeNotes,
            showHints = showHints,
            noteCallback = { takeNotes = it },
            hintCallback = { showHints = it }
        )
        Spacer(modifier = modifier.height(32.dp))
        NumPad(modifier = modifier, takeNotes = takeNotes, sudokuGame = sudokuGame)
    }


}

class Something {
    var t by mutableStateOf("Hello")
}

data class Dc(
    val st: String
)

@Composable
fun TestingStuff(s: MutableLiveData<Dc>, modifier: Modifier = Modifier)
{
    var fu by remember { mutableStateOf(SudokuLogic()) }
    val f by s.observeAsState()
    val m = arrayOfNulls<Int>(9)
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            modifier = modifier,
            colors = buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Black,
                disabledBackgroundColor = Color.White,
                disabledContentColor = Color.Black
            ),
            onClick = {
                //f.t = "World"
                s.postValue(f!!.copy("World"))
            }
        ){
            Text(f!!.st, color=Color.Red)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuTheme {
        val sudoku = SudokuLogic()
        Game(sudokuGame = sudoku)
        sudoku.newGame()
    }
}