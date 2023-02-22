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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    Test()
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
    Canvas(modifier = Modifier.size(360.dp)) {
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
fun SudokuButton(modifier: Modifier = Modifier)
{
    Button (
        modifier = Modifier
            .height(40.dp)
            .width(40.dp),
        colors = buttonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black,
            disabledBackgroundColor = Color.White,
            disabledContentColor = Color.Black
        ),
        //border = BorderStroke(1.dp, Color.Black),
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
        onClick = {}
    ){
        Text("8")
    }
}

@Composable
fun Test(modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize())
    {
        Row(
            modifier = modifier
        ){
            for (i in 1..9){
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (j in 1..9){
                        SudokuButton()
                    }
                }
            }
        }

        Grid()
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuTheme {
        Test()
    }
}