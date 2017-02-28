package ru.nixan.regexpformatter

import android.text.Editable
import java.util.*

class LogicalRegExpItem : RegExpItem {

    private val variants = ArrayList<RegularExpression>()

    private val longVariants = ArrayList<RegularExpression>()
    private val shortVariants = ArrayList<RegularExpression>()

    private var lastSelectedVariant: RegularExpression? = null

    fun addVariant(regularExpression: RegularExpression) = variants.add(regularExpression)

    override val length = variants.map { it.length }.reduce { total, length -> total + length }

    override fun format(input: Editable, startPosition: Int, endPosition: Int) =
            findMatched(variants, input.toString(), startPosition, endPosition)
                    ?.let { it.format(input, startPosition, endPosition) }
                    ?: 0

    private fun findMatched(variants: ArrayList<RegularExpression>, input: String,
                            startPosition: Int, endPosition: Int): RegularExpression? {

        variants.firstOrNull { it.matches(input, startPosition, endPosition) is RegExpItem.MatchResult.Full }
                ?.let {
                    lastSelectedVariant = it
                    return lastSelectedVariant
                }

        shortVariants.filter { it.items.isNotEmpty() }.apply {
            if (contains(lastSelectedVariant)) {
                return lastSelectedVariant
            } else {
                lastSelectedVariant = get(0)
                return lastSelectedVariant
            }
        }

        longVariants.filter { it.items.isNotEmpty() }.apply {
            if (contains(lastSelectedVariant)) {
                return lastSelectedVariant
            } else {
                lastSelectedVariant = get(0)
                return lastSelectedVariant
            }
        }

        return lastSelectedVariant
    }

    override fun matches(string: String, startPosition: Int, endPosition: Int): RegExpItem.MatchResult {
        var maximumFullOccuranciesNumber = -1
        var maximumFullOccuranciesIndex = -1
        variants.map { it.matches(string, startPosition, endPosition) }.forEachIndexed { i, matchResult ->
            if (matchResult is RegExpItem.MatchResult.Full) {
                return RegExpItem.MatchResult.Full()
            } else if ((matchResult is RegExpItem.MatchResult.Long || matchResult is RegExpItem.MatchResult.Short || matchResult is RegExpItem.MatchResult.None && matchResult.score > 0) && maximumFullOccuranciesNumber < matchResult.score) {
                maximumFullOccuranciesIndex = i
                maximumFullOccuranciesNumber = matchResult.score
            }
        }
        if (maximumFullOccuranciesIndex == -1) {
            return RegExpItem.MatchResult.None()
        } else {
            return RegExpItem.MatchResult.Short()
        }
    }

    override fun toString() = variants.joinToString(separator = "|")

}
