package com.jclian.libsudoku

class CrossCycleLinkNode(val value: String, val row: String) {
    var up: CrossCycleLinkNode = this
    var down: CrossCycleLinkNode = this
    var left: CrossCycleLinkNode = this
    var right: CrossCycleLinkNode = this
    var col: CrossCycleLinkNode = this

    init {
        this.col = this
        this.up = this
        this.down = this
        this.left = this
        this.right = this
    }

    fun removeColumn() {
        var node = this
        while (true) {
            node.left.right = node.right
            node.right.left = node.left
            node = node.down
            if (node == this) break
        }
    }

    fun restoreColumn(){
        var node = this
        while (true) {
            node.left.right = node
            node.right.left = node
            node = node.down
            if (node == this) {
                break
            }
        }
    }

    fun removeRow() {
        var node = this
        while (true) {
            node.up?.down = node.down
            node.down.up = node.up
            node = node.right
            if (node == this) {
                break
            }
        }
    }


    fun restoreRow() {
        var node = this
        while (true) {
            node.up.down = node
            node.down.up = node
            node = node.right
            if (node == this) break
        }
    }

    override fun toString(): String {
       return col.value.toString()
//        return super.toString()
    }
}