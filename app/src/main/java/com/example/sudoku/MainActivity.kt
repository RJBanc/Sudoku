package com.example.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sudoku.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuTheme {
                // A surface container using the 'background' color from the theme
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
fun Grid(modifier: Modifier = Modifier
//    .fillMaxSize()
//    .wrapContentSize(Alignment.Center)
){
    Canvas(modifier = modifier.size(360.dp)) {
        val borderDrawWidth = 5f
        val borderColor = Color.Black
        val cellDrawWidth = 2f
        val cellColor = Color.Gray

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
    col: Int
) {
    val sudokuField by fieldData.observeAsState()
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
            backgroundColor = if (sudokuField!!.isHighlighted) Color.Cyan else Color.White,
            contentColor = Color.Black,
            disabledBackgroundColor = if (sudokuField!!.isHighlighted) Color.Cyan else Color.White,
            disabledContentColor = Color.Black
        ),
        enabled = sudokuField!!.isEnabled,
        onClick = {
            sudokuGame.fieldSelected(row, col)
        }
    ){
        Text(
            text = sudokuField!!.number ?: "",
            color = if(sudokuField!!.solution == (sudokuField!!.number ?: "")) Color.Black else Color.Red
        )
    }
}

@Composable
fun Game(
    modifier: Modifier = Modifier,
    sudokuGame: SudokuLogic
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            //modifier = modifier.fillMaxSize()
        )
        {
            Row(
                modifier = modifier
            ){
                for (i in 0..8){
                    Column(
                        modifier = modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (j in 0..8){
                            SudokuButton(
                                fieldData = sudokuGame.getField(row = i, col = j),
                                sudokuGame = sudokuGame,
                                row = i,
                                col = j
                            )
                        }
                    }
                }
            }
            Grid()
        }
        Spacer(modifier = modifier.height(16.dp))
        Row(
            modifier = Modifier
               // .wrapContentSize(Alignment.Center)
                //.padding(start=5.dp, end=5.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..9) {
                Button(
                    modifier= modifier
                        .height(40.dp)
                        .width(40.dp),
                    onClick = {
                        sudokuGame.setFieldNumber(i.toString())
                    }
                ) {
                    Text(i.toString())
                }
            }
        }
    }


}

class Something {
    var t by mutableStateOf("Hello")
}

data class Dc(
    val st: String
)

@Composable
fun Fuck(s: MutableLiveData<Dc>, modifier: Modifier = Modifier)
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
//        var s = Something()
//        var d = MutableLiveData<Dc>(Dc("Hello"))
//        Fuck(d)
//        d.postValue(Dc("Goodbye"))
        val sudoku = SudokuLogic()
        Game(sudokuGame = sudoku)
        sudoku.newGame()
    }
}