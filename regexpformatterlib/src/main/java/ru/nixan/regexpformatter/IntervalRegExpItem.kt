package ru.nixan.regexpformatter

import android.text.Editable
import java.util.*

class IntervalRegExpItem(data: String, override val length: Length) : RegExpItem {

    private val conditions = parseConditions(data)

    private fun parseConditions(data: String): ArrayList<Condition> {
        val result = ArrayList<Condition>()
        var i = 0
        while (i < data.length) {
            val thisChar = data[i]
            if (data.length > i + 1 && data[i] == '\\' && data[i + 1] == 's') {
                result.add(StaticCondition(' '))
                i += 2
            } else if (data.length > i + 1 && data[i] == '\\') {
                result.add(StaticCondition(data[i + 1]))
                i += 2
            } else if (data.length > i + 1 && data[i + 1] == '-'
                    && data.length > i + 2) {
                val nextChar = data[i + 2]
                result.add(IntervalCondition(thisChar, nextChar))
                i += 3
            } else {
                result.add(StaticCondition(thisChar))
                i++
            }
        }
        return result
    }

    override fun toString() = conditions.joinToString(prefix = "[", postfix = "]$length")

    private fun validate(item: Char) = conditions.any { it.validate(item) }

    override fun format(input: Editable, startPosition: Int, endPosition: Int): Int {
        var lastPosition = endPosition
        var position = 0
        while (position + startPosition < lastPosition && length.compareWithPosition(position) <= 0) {
            val check = validate(input[position + startPosition])
            if (check) {
                position++
            } else if (!check && length.compareWithPosition(position) < 0) {
                input.delete(startPosition + position, startPosition + position + 1)
                lastPosition--
            } else {
                return position
            }
        }
        return position
    }

    override fun matches(string: String, startPosition: Int, endPosition: Int): RegExpItem.MatchResult {
        if (string.length < startPosition ||
                string.length < endPosition && !validate(
                        string.substring(startPosition, string.length)) ||
                string.length >= endPosition && !validate(
                        string.substring(startPosition, endPosition))) {
            return RegExpItem.MatchResult.None()
        }
        var currentNumberOfSymbolsMatched = 0
        var inputIsShorter = false
        var inputIsLonger = false
        if (string.length < endPosition) {
            inputIsShorter = true
        }
        if (length.compareWithPosition(endPosition - startPosition) < 0) {
            inputIsShorter = true
        }
        if (length.compareWithPosition(endPosition - startPosition) > 0) {
            inputIsLonger = true
        }
        for (i in startPosition..Math.min(string.length, endPosition) - 1) {
            if (!validate(string[i])) {
                return RegExpItem.MatchResult.Short(symbolsMatched = currentNumberOfSymbolsMatched)
            } else {
                currentNumberOfSymbolsMatched++
            }
        }
        if (inputIsLonger) {
            return RegExpItem.MatchResult.Long(symbolsMatched = currentNumberOfSymbolsMatched)
        } else if (inputIsShorter) {
            return RegExpItem.MatchResult.Short(symbolsMatched = currentNumberOfSymbolsMatched)
        } else {
            return RegExpItem.MatchResult.Full(symbolsMatched = currentNumberOfSymbolsMatched)
        }
    }

    private fun validate(string: String) = string.none { validate(it).not() }

    private interface Condition {

        fun validate(item: Char): Boolean
    }

    private class IntervalCondition(private val from: Char, private val to: Char) : Condition {

        override fun toString() = "$from-$to"

        override fun validate(item: Char) = from <= item && item <= to

    }

    private class StaticCondition(private val c: Char) : Condition {

        override fun toString() = c.toString()

        override fun validate(item: Char) = c == item
    }
}

