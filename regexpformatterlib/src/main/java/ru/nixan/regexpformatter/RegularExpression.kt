package ru.nixan.regexpformatter

import android.text.InputType
import java.util.*

class RegularExpression private constructor(vararg val items: RegExpItem) : RegExpItem {

    override val length: Length = items.map { it.length }.reduce { total, length -> total + length }

    fun formatString(input: String): String = RegularExpressionSpannable.FromString(input).also { format(it) }.toString()

    override fun format(input: RegularExpressionSpannable, startPosition: Int, endPosition: Int): Int {
        var lastPosition = endPosition
        var start = startPosition
        var regularExpressionItemPosition = 0
        var lastFormatFinishedWithSize = false
        while (start < lastPosition && regularExpressionItemPosition < items.size) {
            var end = -1
            if ((items[regularExpressionItemPosition].length is Length.Unlimited || items[regularExpressionItemPosition].length is Length.AtLeast) && regularExpressionItemPosition + 1 < items.size && items[regularExpressionItemPosition + 1] is StaticRegExpItem) {
                end = input.toString().indexOf((items[regularExpressionItemPosition + 1] as StaticRegExpItem).staticData)
                if (end == -1) {
                    var lookupString = (items[regularExpressionItemPosition + 1] as StaticRegExpItem).staticData
                    while (lookupString.isNotEmpty() && end == -1) {
                        if (input.toString().endsWith(lookupString)) {
                            end = input.length - lookupString.length
                        } else {
                            lookupString = lookupString.substring(0, lookupString.length - 1)
                        }
                    }
                }
            }
            val formattingSize =
                    if (end == -1) {
                        items[regularExpressionItemPosition].format(input, start)
                    } else {
                        items[regularExpressionItemPosition].format(input, start, end)
                    }

            lastFormatFinishedWithSize = when (items[regularExpressionItemPosition].length) {
                is Length.Unlimited -> false
                is Length.AtLeast -> false
                is Length.Strict -> formattingSize == (items[regularExpressionItemPosition].length as Length.Strict).length
                is Length.Varying -> formattingSize == (items[regularExpressionItemPosition].length as Length.Varying).maxLength
            }
            start += formattingSize
            regularExpressionItemPosition++
            lastPosition = input.length
        }
        if (lastFormatFinishedWithSize && items.size - regularExpressionItemPosition == 1 && items[regularExpressionItemPosition] is StaticRegExpItem) {
            with(items[regularExpressionItemPosition] as StaticRegExpItem) {
                start += format(input, start, start + length.length)
            }
            regularExpressionItemPosition++
        }
        if (length.compareWithPosition(input.length) > 0 && length is Length.Strict) {
            input.delete(length.length, input.length)
        }
        if (length.compareWithPosition(input.length) > 0 && length is Length.Varying) {
            input.delete(length.maxLength, input.length)
        }
        if (regularExpressionItemPosition == items.size && start < input.length) {
            input.delete(start, input.length)
        }
        return start - startPosition
    }

    override fun matches(string: String, startPosition: Int, endPosition: Int): RegExpItem.MatchResult {
        var itemPosition = 0
        while (startPosition < string.length && itemPosition < items.size) {
            var end = -1
            if (itemPosition + 1 < items.size && items[itemPosition + 1] is StaticRegExpItem) {
                end = string.indexOf((items[itemPosition + 1] as StaticRegExpItem).staticData, startPosition)
            } else if (itemPosition + 1 < items.size && items[itemPosition].length is Length.Strict) {
                end = startPosition + (items[itemPosition].length as Length.Strict).length
            } else if (itemPosition + 1 == items.size) {
                end = string.length
            }
            if (end == -1) {
                return RegExpItem.MatchResult.None(score = itemPosition)
            } else {
                val subResult = items[itemPosition].matches(string, startPosition, end)
                when (subResult) {
                    is RegExpItem.MatchResult.Full -> startPosition - end
                    is RegExpItem.MatchResult.Short -> return RegExpItem.MatchResult.Short(score = itemPosition)
                    is RegExpItem.MatchResult.Long -> return RegExpItem.MatchResult.Long(score = itemPosition)
                    is RegExpItem.MatchResult.None -> return RegExpItem.MatchResult.None(score = itemPosition)
                }
            }
            itemPosition++
        }
        return if (itemPosition == items.size) {
            RegExpItem.MatchResult.Full(score = itemPosition)
        } else {
            RegExpItem.MatchResult.Short(score = itemPosition)
        }
    }

    val inputType: Int
        get() =
            if (items.size == 1 && items[0] is WordRegExpItem) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            } else {
                val onlyDigits = items.isNotEmpty() && !items.any { it !is DigitRegExpItem && it !is StaticRegExpItem }
                if (onlyDigits)
                    InputType.TYPE_CLASS_NUMBER
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            }

    override fun toString(): String =
            if (items.isEmpty()) {
                "\\w+"
            } else {
                items.joinToString(separator = "")
            }

    companion object {

        @JvmStatic
        fun parseRegularExpression(regularExpression: String): RegularExpression {
            val currentRegularExpression =
                    if (regularExpression.isEmpty()) {
                        "\\w*"
                    } else {
                        regularExpression
                    }

            val variantsOfExpression = currentRegularExpression.split("|").dropLastWhile(String::isEmpty).toTypedArray()

            variantsOfExpression.singleOrNull()?.let {
                return RegularExpression(*parseNonLogicalRegularExpression(it))
            }

            val logicalRegExpItem = LogicalRegExpItem()
            for (variant in variantsOfExpression) {
                logicalRegExpItem.addVariant(RegularExpression(*parseNonLogicalRegularExpression(variant)))
            }
            return RegularExpression(logicalRegExpItem)
        }

        private fun parseNonLogicalRegularExpression(
                regularExpressionString: String): Array<RegExpItem> {
            val currentRegularExpressionString = regularExpressionString.trimStart { it == '^' }.trimEnd { it == '$' }
            var position = 0
            val result = ArrayList<RegExpItem>()
            while (position < currentRegularExpressionString.length) {
                var c = currentRegularExpressionString[position]
                when (c) {
                    '\\' -> {
                        position++
                        val typeCharacter = currentRegularExpressionString[position]
                        var length: Length = Length.Strict(1)
                        position++
                        if (position < currentRegularExpressionString.length) {
                            c = currentRegularExpressionString[position]
                            if (c == '*') {
                                length = Length.Unlimited
                            } else if (c == '+') {
                                length = Length.AtLeast(1)
                            } else if (c == '{') {
                                val lengthString = StringBuilder()
                                position++
                                while (position < currentRegularExpressionString.length && currentRegularExpressionString[position] != '}') {
                                    lengthString.append(currentRegularExpressionString[position])
                                    position++
                                }
                                val lengths = lengthString.toString().split(",").dropLastWhile(String::isEmpty).toTypedArray()
                                if (lengths.size == 1) {
                                    length = Length.Strict(Integer.parseInt(lengths[0].trim()))
                                } else if (lengths.size == 2 && lengths[1].trim { it <= ' ' }.isEmpty()) {
                                    length = Length.AtLeast(Integer.parseInt(lengths[0].trim()))
                                } else if (lengths.size == 2) {
                                    length = Length.Varying(Integer.parseInt(lengths[0].trim()), Integer.parseInt(lengths[1].trim()))
                                }
                            }
                            position++
                        }
                        when (typeCharacter) {
                            'd' -> result.add(DigitRegExpItem(length))
                            'w' -> result.add(WordRegExpItem(length))
                            else -> result.add(StaticRegExpItem(typeCharacter.toString()))
                        }
                    }
                    '[' -> {
                        position++
                        val variantConditions = StringBuilder()
                        while (position < currentRegularExpressionString.length && currentRegularExpressionString[position] != ']') {
                            variantConditions.append(currentRegularExpressionString[position])
                            position++
                        }
                        var length: Length = Length.Strict(1)
                        if (position < currentRegularExpressionString.length) {
                            c = currentRegularExpressionString[++position]
                            if (c == '*') {
                                length = Length.Unlimited
                                position++
                            } else if (c == '+') {
                                length = Length.AtLeast(1)
                                position++
                            } else if (c == '{') {
                                val lengthString = StringBuilder()
                                position++
                                while (position < currentRegularExpressionString.length && currentRegularExpressionString[position] != '}') {
                                    lengthString.append(currentRegularExpressionString[position])
                                    position++
                                }
                                val lengths = lengthString.toString().split(",").dropLastWhile(String::isEmpty).toTypedArray()
                                if (lengths.size == 1) {
                                    length = Length.Strict(Integer.parseInt(lengths[0].trim()))
                                } else if (lengths.size == 2 && lengths[1].trim().isEmpty()) {
                                    length = Length.AtLeast(Integer.parseInt(lengths[0].trim()))
                                } else if (lengths.size == 2) {
                                    length = Length.Varying(Integer.parseInt(lengths[0].trim()), Integer.parseInt(lengths[1].trim()))
                                }
                                if (currentRegularExpressionString[position] == '}') {
                                    position++
                                }
                            }
                        }
                        result.add(IntervalRegExpItem(variantConditions.toString(), length))
                    }
                    else -> {
                        val constantString = StringBuilder()
                        constantString.append(c)
                        position++
                        while (position < currentRegularExpressionString.length
                                && currentRegularExpressionString[position] != '\\'
                                && currentRegularExpressionString[position] != '[') {
                            constantString.append(currentRegularExpressionString[position])
                            position++
                        }
                        result.add(StaticRegExpItem(constantString.toString()))
                    }
                }
            }
            return result.toTypedArray()
        }
    }

}
