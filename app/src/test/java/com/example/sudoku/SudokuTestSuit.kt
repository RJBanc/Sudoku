package com.example.sudoku

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