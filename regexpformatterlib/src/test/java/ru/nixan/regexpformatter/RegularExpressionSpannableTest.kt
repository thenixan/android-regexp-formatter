package ru.nixan.regexpformatter

import org.junit.Test

import org.junit.Assert.*

class RegularExpressionSpannableTest {

    @Test
    fun getLength() {
        assertEquals(0, RegularExpressionSpannable.FromString("").length)
        assertEquals(4, RegularExpressionSpannable.FromString("asdf").length)
    }

    @Test
    fun get() {
        val a = RegularExpressionSpannable.FromString("asdf")
        assertEquals('a', a[0])
        assertEquals('s', a[1])
        assertEquals('d', a[2])
        assertEquals('f', a[3])
    }

    @Test
    fun delete() {
        val a = RegularExpressionSpannable.FromString("asdf")
        a.delete(0, 1)
        assertEquals("sdf", a.toString())
    }

    @Test
    fun insert() {
        val a = RegularExpressionSpannable.FromString("asdf")
        a.insert(0, "as")
        assertEquals("asasdf", a.toString())
        a.insert(1, "ffff")
        assertEquals("affffsasdf", a.toString())
    }
}