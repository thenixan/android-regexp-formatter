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
        assertEquals("asd000asd", RegularExpression.parseRegularExpression("asd\\d{3}asd").formatString("000"))
    }

    @Test
    fun testStaticFormatterStart() {
        assertEquals("asdwww", RegularExpression.parseRegularExpression("asd\\w{3}").formatString("www"))
    }

    @Test
    fun testStaticFormatterMaskInput() {
        val formatter = RegularExpression.parseRegularExpression("asdw\\w{3}")
        assertEquals("asdwww", formatter.formatString("www"))
        assertEquals("asdw123", formatter.formatString("123"))
        assertEquals("asd", formatter.formatString("asd"))
    }

    @Test
    fun testStaticInMiddle() {
        val formatter = RegularExpression.parseRegularExpression("\\d{2}12\\d{2,4}")
        assertEquals("9", formatter.formatString("9"))
        assertEquals("99", formatter.formatString("99"))
        assertEquals("99129", formatter.formatString("999"))
        assertEquals("991299", formatter.formatString("9999"))
        assertEquals("9912999", formatter.formatString("99999"))
        assertEquals("99129999", formatter.formatString("999999"))
        assertEquals("99129999", formatter.formatString("9999999"))
    }

    @Test
    fun testStaticFormatterEnd() {
        assertEquals("asdwww", RegularExpression.parseRegularExpression("\\w{3}www").formatString("asd"))
        assertEquals("123www", RegularExpression.parseRegularExpression("\\d{3}www").formatString("123"))
    }

    @Test
    fun testEmptyFormatter() {
        val formatter = RegularExpression.parseRegularExpression("")
        assertEquals("askdfjaosdifjas", formatter.formatString("askdfjaosdifjas"))
        assertEquals("j89f13fj134", formatter.formatString("j89f13fj134"))
        assertEquals("ajksdfnjaksdnfjkadsnfjkansdjkfnasdjkcnasjcnmadoscmnoadscadsocmadosicmasd", formatter.formatString("ajksdfnjaksdnfjkadsnfjkansdjkfnasdjkcnasjcnmadoscmnoadscadsocmadosicmasd"))
    }

    @Test
    fun testVariableLength() {
        val formatter = RegularExpression.parseRegularExpression("\\d{1,2}test")
        assertEquals("1", formatter.formatString("1"))
        assertEquals("12test", formatter.formatString("12"))
        assertEquals("1", formatter.formatString("1"))
        assertEquals("1test", formatter.formatString("1k"))
        assertEquals("1t", formatter.formatString("1t"))
        assertEquals("1test", formatter.formatString("1t2"))
        assertEquals("12test", formatter.formatString("12t2"))
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
