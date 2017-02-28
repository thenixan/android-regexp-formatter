package ru.nixan.regexpformatter

import android.text.Editable

class WordRegExpItem(override val length: Length) : RegExpItem {

    override fun toString() = "\\w$length"

    override fun format(input: Editable, startPosition: Int, endPosition: Int) = (0 until endPosition - startPosition).takeWhile { length.compareWithPosition(it) <= 0 }.count()

    override fun matches(string: String, startPosition: Int, endPosition: Int): RegExpItem.MatchResult {
        if (string.length < startPosition) {
            return RegExpItem.MatchResult.None()
        }
        if (string.length < endPosition) {
            return RegExpItem.MatchResult.Short(symbolsMatched = string.length - startPosition)
        }
        if (length.compareWithPosition(endPosition - startPosition) < 0) {
            return RegExpItem.MatchResult.Short(symbolsMatched = endPosition - startPosition)
        }
        if (length.compareWithPosition(endPosition - startPosition) > 0) {
            return RegExpItem.MatchResult.Long(symbolsMatched = endPosition - startPosition)
        }
        return RegExpItem.MatchResult.Full()
    }
}
