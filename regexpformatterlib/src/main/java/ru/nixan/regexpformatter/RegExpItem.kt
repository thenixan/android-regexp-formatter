package ru.nixan.regexpformatter

import android.text.Editable

interface RegExpItem {

    val length: Length

    fun format(input: Editable, startPosition: Int = 0, endPosition: Int = input.length): Int

    fun matches(string: String, startPosition: Int = 0, endPosition: Int = string.length): MatchResult

    sealed class MatchResult(val score: Int) {
        class None(score: Int = 0) : MatchResult(score)
        class Full(score: Int = 0) : MatchResult(score)
        class Short(score: Int = 0) : MatchResult(score)
        class Long(score: Int = 0) : MatchResult(score)
    }

}

