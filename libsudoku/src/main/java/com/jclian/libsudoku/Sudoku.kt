package com.jclian.libsudoku

import com.jclian.libsudoku.utils.Log
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


/**
 * 该算法根据知乎大神写的生成算法详解
 * https://zhuanlan.zhihu.com/p/67447747
 * 数独的求解用上了Dance Link X算法，知乎大神也做了解释
 * https://zhuanlan.zhihu.com/p/67324277
 * *
 * 下面使用kotlin，转写了一下
 */
object Sudoku {

    var needLog = true

    fun initLocationDict(initCount: Int): HashMap<String, Int> {
        val dict = HashMap<String, Int>()
        val s = HashSet<Int>()
        while (dict.keys.size < initCount) {
            val i: Int = (Math.random() * 9).toInt()
            val j: Int = (Math.random() * 9).toInt()
            val k: Int = 1 + (Math.random() * 9).toInt()

            val a = i * 9 + j
            if (s.contains(a)) {
                continue
            }
            val b = i * 9 + k + 80
            if (s.contains(b)) {
                continue
            }
            val c = j * 9 + k + 161
            if (s.contains(c)) {
                continue
            }
            val d = ((i / 3).toInt() * 3 + (j / 3).toInt()) * 9 + k + 242
            if (s.contains(d)) {
                continue
            }
            s.add(a)
            s.add(b)
            s.add(c)
            s.add(d)
            dict["$i,$j"] = k
        }
        return dict
    }

    fun getFormattedAnswer(ans: ArrayList<String>): Array<IntArray> {
        ans.sort()
        val arr = Array(9) { IntArray(9) }
        for (row_id in ans) {
            val loc = row_id.toInt() / 9
            val i = (loc / 9).toInt()
            val j = loc % 9
            val k = row_id.toInt() % 9 + 1
            arr[i][j] = k
        }
        return arr
    }

    /**
     * [blankCount] 留白个数
     */
    fun getSudokuMap(
        ans: ArrayList<String>,
        isFull: Boolean = false,
        blankCount: Int = 30
    ): HashMap<String, Int> {
        Log.d(
            "sudoku", "ans=${ans.joinToString(",")} \n isFull=$isFull blankCount=$blankCount",
            needLog
        )

        val map = HashMap<String, Int>()
        ans.sort()
        val arr = Array(9) { IntArray(9) }
        for (row_id in ans) {
            val loc = row_id.toInt() / 9
            val i = (loc / 9).toInt()
            val j = loc % 9
            val k = row_id.toInt() % 9 + 1
            map["$i,$j"] = k
        }

        // 返回完整的map
        if (isFull) {
            return map
        }
        Log.d("sudoku", "Make blank for Puzzle")
        // 随机挖空
        while (map.size > 81 - blankCount) {
            val i: Int = (Math.random() * 9).toInt()
            val j: Int = (Math.random() * 9).toInt()
            val key = "$i,$j"
            map.remove(key)
        }

        return map
    }

    fun getSudokuLinkedList(map: HashMap<String, Int>): CrossCycleLinkNode {
        val head = initCol(324)
        for (i in 0..8) {
            for (j in 0..8) {
                val key = "$i,$j"
                if (map.contains(key)) {

                    val k = map[key]!!
                    // 条件一：max 8×9+8
                    val a = i * 9 + j
                    // 所以这里加 80,实际上共81个条件
                    //
                    val b = i * 9 + k + 80
                    val c = j * 9 + k + 161
                    val d = ((i / 3) * 3 + (j / 3)) * 9 + k + 242
                    val rowId = (i * 9 + j) * 9 + k - 1
                    appendRow(
                        head,
                        rowId.toString(),
                        arrayListOf(a.toString(), b.toString(), c.toString(), d.toString())
                    )
                } else {

                    for (k in 1..9) {
                        val a = i * 9 + j
                        val b = i * 9 + k + 80
                        val c = j * 9 + k + 161
                        val d = ((i / 3) * 3 + (j / 3)) * 9 + k + 242
                        val rowId = (i * 9 + j) * 9 + k - 1
                        appendRow(
                            head,
                            rowId.toString(),
                            arrayListOf(a.toString(), b.toString(), c.toString(), d.toString())
                        )
                    }
                }
            }

        }
        return head
    }

    fun initCol(col_count: Int): CrossCycleLinkNode {
        val head = CrossCycleLinkNode("head", "column")
        for (i in 0 until col_count) {
            val colNode = CrossCycleLinkNode(i.toString(), head.row)
            colNode.right = head
            colNode.left = head.left
            colNode.right.left = colNode
            colNode.left.right = colNode
        }
        return head
    }


    fun appendRow(head: CrossCycleLinkNode, row_id: String, list: List<String>) {

        var last: CrossCycleLinkNode? = null
        var col = head.right
        for (num in list) {
            while (col != head) {
                if (col.value == num) {
                    val node = CrossCycleLinkNode(1.toString(), row_id)
                    node.col = col
                    node.down = col
                    node.up = col.up
                    node.down.up = node
                    node.up.down = node
                    if (last != null) {
                        node.left = last
                        node.right = last.right
                        node.left.right = node
                        node.right.left = node

                    }
                    last = node
                    break
                }

                col = col.right
            }

        }

    }

    /**
     * [count] 增加一个计数器，强制结束
     */
    fun danceLinkX(
        head: CrossCycleLinkNode,
        answers: ArrayList<String>,
        count: Int = 0
    ): Boolean {
        Log.d("Sudoku#dance_link_x", "ans=$answers \n ans.size=${answers.size}", needLog)

        if (head.right == head) return true

        var node = head.right
        while (node != head) {
            if (node.down == node) {
                return false
            }
            node = node.right
        }

        val restores = ArrayList<(() -> Unit)>()
        val firstCol = head.right
        firstCol.removeColumn()
        val restoreColumn: () -> Unit = firstCol::restoreColumn
        restores.add(restoreColumn)

        node = firstCol.down
        while (node != firstCol) {
            if (node.right != node) {
                node.right.removeRow()
                restores.add(node.right::restoreRow)
            }
            node = node.down
        }
        val curRestoresCount = restores.size
        var selectedRow = firstCol.down
        while (selectedRow != firstCol) {
            answers.add(selectedRow.row)
            if (selectedRow.right != selectedRow) {
                var rowNode = selectedRow.right
                while (true) {
                    var colNode = rowNode.col
                    colNode.removeColumn()
                    restores.add(colNode::restoreColumn)
                    colNode = colNode.down
                    while (colNode != colNode.col) {
                        if (colNode.right != colNode) {
                            colNode.right.removeRow()
                            restores.add(colNode.right::restoreRow)
                        }
                        colNode = colNode.down
                    }
                    rowNode = rowNode.right
                    if (rowNode == selectedRow.right) {
                        break
                    }
                }
            }
            if (danceLinkX(head, answers, count + 1)) {
                return true
            }
            answers.removeAt(answers.size - 1)
            while (restores.size > curRestoresCount) {
                val method = restores[restores.size - 1]
                method.invoke()
                restores.remove(method)
            }
            selectedRow = selectedRow.down
        }
        while (restores.size > 0) {
            val method = restores[restores.size - 1]
            method.invoke()
            restores.remove(method)
        }
        return false
    }

    fun gen(
        initMap: HashMap<String, Int>? = null,
        isFull: Boolean = false,
        blankCount: Int = 0
    ): HashMap<String, Int> {
        val initData = initMap ?: initLocationDict(11)
        var head = getSudokuLinkedList(initData)
        val ans = ArrayList<String>()
        danceLinkX(head, ans)
        if (ans.size > 0) {
            val map: HashMap<String, Int> = getSudokuMap(ans, isFull, blankCount)
            head = getSudokuLinkedList(initData)
            ans.clear()
            danceLinkX(head, ans, 0)
            Log.d("Sudoku", "${ans.size} details=$ans", needLog)
            if (ans.size > 0) {
                return map
            }
            println("Can't gen puzzle.Retry")
            return gen(initData, isFull, blankCount)
        }
        println("No ans.Retry")
        return gen(isFull = isFull, blankCount = blankCount)

    }

    /**
     * 用于检查答案
     * [data] 传入一个完整数独
     *
     * Checking the answer
     * [data] Pass a full map of Sudoku。
     */
    fun check(data: HashMap<String, Int>): Boolean {
        if (data.size < 81) {
            return false
        }
        val head = getSudokuLinkedList(data)
        val ans = ArrayList<String>()
        danceLinkX(head, ans)
        return ans.size > 0
    }

    fun check(initMap: HashMap<String, Int>, answerMap: HashMap<String, Int>): Boolean {
        val hashMap = hashMapOf<String, Int>()
        hashMap.putAll(initMap)
        hashMap.putAll(answerMap)
        return check(hashMap)
    }

    @ExperimentalStdlibApi
    fun solve(str: String): HashMap<String, Int> {
        val hashMap = HashMap<String, Int>()

        if (str.isBlank()) {
            return hashMap
        }

        str.replace(",", "", false).let {
            if (it.length != 81) {
                return hashMap
            }

            it.forEachIndexed { index, c ->
                val j = index % 9
                val i = index / 9
                if (c.digitToInt() != 0) {
                    hashMap["$i,$j"] = c.digitToInt()
                }
            }
            Log.d("sudoku", "initmap = ${hashMap.toString()}", needLog)
            return gen(hashMap, isFull = true)
        }
    }

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) {
        val s = "600400030200000000100000009920300000000800601000000000000000080002509000000700000"
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                print("${s[i * 9 + j]}  ")
            }
            println()
        }
        println()
        needLog = false
        val map = solve(s)
        println("Solved")
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                print("${map["$i,$j"]}  ")
            }
            println()
        }
    }

}