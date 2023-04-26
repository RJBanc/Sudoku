package com.example.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun play() = baselineProfileRule.collectBaselineProfile(
        packageName = "com.example.sudoku",
        profileBlock = {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.res("43")), 5_000)
            device.findObject(By.res("43")).click()
            device.findObject(By.res("newGame")).click()
            device.findObject(By.res("Hard")).click()
            device.findObject(By.res("start"))?.click()
        }
    )
}