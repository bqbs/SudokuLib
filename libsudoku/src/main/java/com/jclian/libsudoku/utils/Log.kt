package com.jclian.libsudoku.utils

/**
 * created by lianjc on 2021/8/5
 */
object Log {
    @JvmOverloads
    fun d(tag: String, message: String, print: Boolean = false) {
        if (print) {
            println("$tag---------$message")
        }
    }
}