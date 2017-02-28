package ru.nixan.regexpformatter

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils

class RegExpFormatter(regExpMask: String) {

    private val regularExpression: RegularExpression = RegularExpression.parseRegularExpression(regExpMask)

    fun check(value: String): Boolean {
        var start = 0
        var itemPosition = 0
        while (start < value.length && itemPosition < regularExpression.items.size) {
            var endPosition = -1
            if (itemPosition + 1 < regularExpression.items.size && regularExpression.items[itemPosition + 1] is StaticRegExpItem) {
                endPosition = value.indexOf(regularExpression.items[itemPosition + 1].toString(), start)
            } else if (itemPosition + 1 < regularExpression.items.size && regularExpression.items[itemPosition].length is Length.Strict) {
                endPosition = start + (regularExpression.items[itemPosition].length as Length.Strict).length
            } else if (itemPosition + 1 == regularExpression.items.size) {
                endPosition = value.length
            }
            if (endPosition == -1) {
                return false
            } else {
                if (regularExpression.items[itemPosition].matches(value, start, endPosition) !is RegExpItem.MatchResult.Full) {
                    return false
                } else {
                    start = endPosition
                }
            }
            itemPosition++
        }
        regularExpression.items.singleOrNull()?.length?.let {
            if (it.compareWithPosition(0) == 0 && TextUtils.isEmpty(value)) {
                return true
            }
        }
        return itemPosition == regularExpression.items.size
    }

    fun format(input: String) = SpannableStringBuilder(input).apply { format(this) }.toString()

    fun format(input: Editable) = regularExpression.format(input, 0)

    val inputType = regularExpression.inputType

    override fun toString() = regularExpression.toString()

}
