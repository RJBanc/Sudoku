package com.example.sudoku

import com.example.sudoku.core.SudokuSolverUnitTest
import com.example.sudoku.util.BitUtilUnitTest
import com.example.sudoku.util.SudokuUtilUnitTest
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)

@Suite.SuiteClasses(
    BitUtilUnitTest::class,
    SudokuUtilUnitTest::class,
    SudokuSolverUnitTest::class
)

class SudokuTestSuit {}