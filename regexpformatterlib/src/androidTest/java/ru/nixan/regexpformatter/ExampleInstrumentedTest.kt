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
        assertEquals("asd000asd", RegExpFormatter("asd\\d{3}asd").format("000"))
    }

    @Test
    fun testStaticFormatterStart() {
        assertEquals("asdwww", RegExpFormatter("asd\\w{3}").format("www"))
    }

    @Test
    fun testStaticFormatterMaskInput() {
        val formatter = RegExpFormatter("asdw\\w{3}")
        assertEquals("asdwww", formatter.format("www"))
        assertEquals("asdw123", formatter.format("123"))
        assertEquals("asd", formatter.format("asd"))
    }

    @Test
    fun testStaticInMiddle() {
        val formatter = RegExpFormatter("\\d{2}12\\d{2,4}")
        assertEquals("9", formatter.format("9"))
        assertEquals("99", formatter.format("99"))
        assertEquals("99129", formatter.format("999"))
        assertEquals("991299", formatter.format("9999"))
        assertEquals("9912999", formatter.format("99999"))
        assertEquals("99129999", formatter.format("999999"))
        assertEquals("99129999", formatter.format("9999999"))
    }

    @Test
    fun testStaticFormatterEnd() {
        assertEquals("asdwww", RegExpFormatter("\\w{3}www").format("asd"))
        assertEquals("123www", RegExpFormatter("\\d{3}www").format("123"))
    }

    @Test
    fun testEmptyFormatter() {
        val formatter = RegExpFormatter("")
        assertEquals("askdfjaosdifjas", formatter.format("askdfjaosdifjas"))
        assertEquals("j89f13fj134", formatter.format("j89f13fj134"))
        assertEquals("ajksdfnjaksdnfjkadsnfjkansdjkfnasdjkcnasjcnmadoscmnoadscadsocmadosicmasd", formatter.format("ajksdfnjaksdnfjkadsnfjkansdjkfnasdjkcnasjcnmadoscmnoadscadsocmadosicmasd"))
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

    @Test
    fun testChecks() {
        val formatter = RegExpFormatter("\\d{1,3}test")
        assertEquals(true, formatter.check("12test"))
        assertEquals(true, formatter.check("1test"))
        assertEquals(true, formatter.check("123test"))
        assertEquals(false, formatter.check("12tes"))
        assertEquals(false, formatter.check("123"))
        assertEquals(false, formatter.check("test"))
    }
}
