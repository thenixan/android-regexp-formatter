package ru.nixan.regexpformatter

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher

class RegExpFormatter(regExpMask: String) : TextWatcher {

    private var isEditing = false

    override fun afterTextChanged(input: Editable?) {
        if (!isEditing) {
            isEditing = true
            input?.let {
                format(it)
            }
            isEditing = false
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private val regularExpression: RegularExpression = RegularExpression.parseRegularExpression(regExpMask)

    public fun check(value: String): Boolean {
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

    @Deprecated(message = "Please use the `formatString` function of RegularExpression class.")
    fun format(input: String) = regularExpression.formatString(input)

    fun format(input: Editable) = regularExpression.format(RegularExpressionSpannable.FromEditable(input), 0)

    public val inputType = regularExpression.inputType

    override fun toString() = regularExpression.toString()

}
