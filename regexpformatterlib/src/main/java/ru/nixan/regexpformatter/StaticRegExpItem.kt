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

        val lookup = string.substring(startPosition, Math.min(string.length, endPosition))
        val target = staticData.substring(0, Math.min(staticData.length, lookup.length))
        if (lookup != target) {
            return RegExpItem.MatchResult.None()
        }

        return if (string.length < endPosition || endPosition - startPosition < length.length)
            RegExpItem.MatchResult.Short()
        else
            RegExpItem.MatchResult.Full()
    }

}
