package com.jclian.sudokulib

import com.jclian.libsudoku.Sudoku
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

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

    @Test
    fun genSudokuTest() {
//        Sudoku.needLog = false
        var start = System.currentTimeMillis()
        val list = LinkedList<SudokuDelegate>()
        for (i in 0..10) {
            val s = System.currentTimeMillis()
            val map = Sudoku.gen(isFull = false, blankCount = 50)
            list.add(SudokuDelegate(map).also {
                it.genTime = System.currentTimeMillis() - s
            })
            println("gen $i ${System.currentTimeMillis() - s}")
        }
        val genCost = System.currentTimeMillis() - start

        start = System.currentTimeMillis()
        for (sd: SudokuDelegate in list) {
            val s = System.currentTimeMillis()
            Sudoku.gen(sd.map, isFull = true)
            sd.solvedTime = System.currentTimeMillis() - s
        }

        for (sd in list) {
            println("${sd.map} , Solved Time ${sd.solvedTime} ms, gen time:${sd.genTime} ratio: ${sd.solvedTime / sd.genTime.toDouble()}")
        }
        println("cost: gen $genCost ms   solved ${System.currentTimeMillis() - start}")

    }

}

data class SudokuDelegate(var map: HashMap<String, Int>) {
    var solvedTime: Long = 0
    var genTime: Long = 0

    override fun equals(other: Any?): Boolean {
        if (other is SudokuDelegate) {
            return map == other.map
        }
        return false
    }

}
