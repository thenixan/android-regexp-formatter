package ru.nixan.regexpformatter

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test, which will execute on an Android device.

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("ru.nixan.regexpformatter.test", appContext.packageName)
    }

    @Test
    fun testStaticFormatterStartAndEnd() {
        assertEquals("asd000asd", RegExpFormatter("asd\\d{3}asd", 9).format("000"))
    }

    @Test
    fun testStaticFormatterStart() {
        assertEquals("asdwww", RegExpFormatter("asd\\w{3}", 6).format("www"))
    }

    @Test
    fun testStaticFormatterMaskInput() {
        val formatter = RegExpFormatter("asdw\\w{3}", 6)
        assertEquals("asdwww", formatter.format("www"))
        assertEquals("asdw123", formatter.format("123"))
        assertEquals("asd", formatter.format("asd"))
    }

    @Test
    fun testStaticFormatterEnd() {
        assertEquals("asdwww", RegExpFormatter("\\w{3}www", 6).format("asd"))
        assertEquals("123www", RegExpFormatter("\\d{3}www", 6).format("123"))
    }

    @Test
    fun testEmptyFormatter() {
        val formatter = RegExpFormatter("")
        assertEquals("askdfjaosdifjas", formatter.format("askdfjaosdifjas"))
        assertEquals("j89f13fj134", formatter.format("j89f13fj134"))
        assertEquals("ajksdfnjaksdnfjkadsnfjkansdjkfnasdjkcnasjcnmadoscmnoadscadsocmadosicmasd", formatter.format("ajksdfnjaksdnfjkadsnfjkansdjkfnasdjkcnasjcnmadoscmnoadscadsocmadosicmasd"))
    }

    @Test
    fun testEmptyFormatterWithLength() {
        val formatter = RegExpFormatter("", 3)
        assertEquals("ask", formatter.format("askdfjaosdifjas"))
        assertEquals("as", formatter.format("as"))
        assertEquals("a", formatter.format("a"))
        assertEquals(" as", formatter.format(" askdfjaosdifjas"))
        assertEquals("  a", formatter.format("  askdfjaosdifjas"))
    }

    @Test
    fun testVariableLength() {
        val formatter = RegExpFormatter("\\d{1,2}test")
        assertEquals("1", formatter.format("1"))
        assertEquals("12test", formatter.format("12"))
        assertEquals("1", formatter.format("1"))
        assertEquals("1test", formatter.format("1k"))
        assertEquals("1t", formatter.format("1t"))
        assertEquals("1test", formatter.format("1t2"))
        assertEquals("12test", formatter.format("12t2"))
    }
}
