package ru.nixan.regexpformatter

import java.util.*

class LogicalRegExpItem : RegExpItem {

    private val variants = arrayListOf<RegularExpression>()

    private val longVariants = arrayListOf<RegularExpression>()
    private val shortVariants = arrayListOf<RegularExpression>()

    private var lastSelectedVariant: RegularExpression? = null

    fun addVariant(regularExpression: RegularExpression) = variants.add(regularExpression)

    override val length = variants.map { it.length }.reduce { total, length -> total + length }

    override fun format(input: RegularExpressionSpannable, startPosition: Int, endPosition: Int) =
            findMatched(variants, input.toString(), startPosition, endPosition)?.format(input, startPosition, endPosition)
                    ?: 0

    private fun findMatched(variants: ArrayList<RegularExpression>, input: String,
                            startPosition: Int, endPosition: Int): RegularExpression? {

        variants.firstOrNull { it.matches(input, startPosition, endPosition) is RegExpItem.MatchResult.Full }
                ?.let {
                    lastSelectedVariant = it
                    return lastSelectedVariant
                }

        shortVariants.filter { it.items.isNotEmpty() }.apply {
            return if (contains(lastSelectedVariant)) {
                lastSelectedVariant
            } else {
                lastSelectedVariant = get(0)
                lastSelectedVariant
            }
        }

        longVariants.filter { it.items.isNotEmpty() }.apply {
            return if (contains(lastSelectedVariant)) {
                lastSelectedVariant
            } else {
                lastSelectedVariant = get(0)
                lastSelectedVariant
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
