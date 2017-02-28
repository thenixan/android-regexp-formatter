package ru.nixan.regexpformatter

import android.text.Editable
import android.text.TextUtils

class DigitRegExpItem(override val length: Length) : RegExpItem {

    private fun validate(s: String) = TextUtils.isDigitsOnly(s)

    override fun toString() = "\\d$length"

    override fun format(input: Editable, startPosition: Int, endPosition: Int): Int {
        var currentEndPosition = endPosition
        var position = 0
        while (length.compareWithPosition(position) <= 0 && startPosition + position < currentEndPosition) {
            val isDigit = Character.isDigit(input[startPosition + position])
            if (isDigit) {
                position++
            } else if (!isDigit && length.compareWithPosition(position) < 0) {
                input.delete(startPosition + position, startPosition + position + 1)
                currentEndPosition--
            } else {
                return position
            }
        }
        return position
    }

    override fun matches(string: String, startPosition: Int, endPosition: Int): RegExpItem.MatchResult {
        if (string.length < startPosition || string.length < endPosition && !validate(string.substring(startPosition, string.length)) || string.length >= endPosition && !validate(string.substring(startPosition, endPosition))) {
            return RegExpItem.MatchResult.None()
        }

        string.filterIndexed { i, c -> i >= startPosition && i < endPosition }
                .forEach {
                    if (it !in '0'..'9') {
                        return RegExpItem.MatchResult.Short()
                    }
                }


        if (length.compareWithPosition(endPosition - startPosition) < 0) {
            return RegExpItem.MatchResult.Short()
        } else if (length.compareWithPosition(endPosition - startPosition) > 0) {
            return RegExpItem.MatchResult.Long()
        } else {
            return RegExpItem.MatchResult.Full()
        }
    }

}

