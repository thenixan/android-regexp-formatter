package ru.nixan.regexpformatter

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class LibraryUnitTest {

    @Test
    @Throws(Exception::class)
    fun empty_isCorrect() {
        val mask = RegularExpression.parseRegularExpression("")
        assertEquals("asdf", mask.formatString("asdf"))
        assertEquals("1234", mask.formatString("1234"))
        assertEquals("", mask.formatString(""))
    }
}