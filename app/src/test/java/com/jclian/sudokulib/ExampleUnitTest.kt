package com.jclian.sudokulib

import com.jclian.libsudoku.Sudoku
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @ExperimentalStdlibApi
    @Test
    fun sudoku() {
        val s = "600400030200000000100000009920300000000800601000000000000000080002509000000700000"
//        val s = "601004095030700406000001800219030000300000000004200080000000004107400020000020050"
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                print("${s[i * 9 + j]}  ")
            }
            println()
        }
        Sudoku.needLog = false
        val map = Sudoku.solve(s)

        for (i in 0 until 9) {
            for (j in 0 until 9) {
                print("${map["$i,$j"]}  ")
            }
            println()
        }
    }
}