package ru.nixan.regexpformatter

import android.text.Editable

sealed class RegularExpressionSpannable {

    abstract val length: Int

    abstract operator fun get(index: Int): Char

    abstract fun delete(start: Int, end: Int)

    abstract fun insert(position: Int, s: String)

    class FromString(private var value: String) : RegularExpressionSpannable() {

        override fun insert(position: Int, s: String) {
            value = "${value.take(position)}$s${value.drop(position)}"
        }

        override fun delete(start: Int, end: Int) {
            value = value.removeRange(start, end)
        }

        override fun get(index: Int): Char = value[index]

        override val length: Int
            get() = value.length

        override fun toString(): String = value

    }

    class FromEditable(private val value: Editable) : RegularExpressionSpannable() {

        override fun insert(position: Int, s: String) {
            value.insert(position, s)
        }

        override fun delete(start: Int, end: Int) {
            value.delete(start, end)
        }

        override fun get(index: Int): Char = value[index]

        override val length: Int
            get() = value.length

        override fun toString(): String = value.toString()

    }
}