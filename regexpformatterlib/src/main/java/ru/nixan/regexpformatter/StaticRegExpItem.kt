package ru.nixan.regexpformatter

import android.text.Editable

class StaticRegExpItem(val staticData: String) : RegExpItem {

    override val length = Length.Strict(staticData.length)

    override fun toString() = staticData

    override fun format(input: Editable, startPosition: Int, endPosition: Int): Int {
        var lastPosition = endPosition
        var position = 0
        while (position + startPosition < lastPosition && (position < length.length)) {
            if (input.length == position + startPosition || input[position + startPosition] != staticData[position]) {
                input.insert(startPosition + position, staticData[position].toString())
                lastPosition++
            }
            position++
        }
        return position
    }

    override fun matches(string: String, startPosition: Int, endPosition: Int): RegExpItem.MatchResult {
        if (string.length < startPosition)
            return RegExpItem.MatchResult.None()

        var currentNumberOfSymbolsMatched = 0
        var inputIsShorter = false

        if (string.length < endPosition) inputIsShorter = true
        if (endPosition - startPosition < length.length) inputIsShorter = true
        val lookup = string.substring(startPosition, Math.min(string.length, endPosition))
        val target = staticData.substring(0, Math.min(staticData.length, lookup.length))
        if (lookup != target) {
            (0 until lookup.length)
                    .filter { lookup[it] == target[it] }
                    .forEach { currentNumberOfSymbolsMatched++ }
            return RegExpItem.MatchResult.None(symbolsMatched = currentNumberOfSymbolsMatched)
        } else
            currentNumberOfSymbolsMatched = lookup.length
        if (inputIsShorter)
            return RegExpItem.MatchResult.Short(symbolsMatched = currentNumberOfSymbolsMatched)
        else
            return RegExpItem.MatchResult.Full(symbolsMatched = currentNumberOfSymbolsMatched)
    }

}
