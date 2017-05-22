package ru.nixan.regexpformatter

/**
 * Created by nixan on 22.02.17.
 */

sealed class Length {

    class Unlimited : Length() {

        override fun compareWithPosition(position: Int): Int {
            return 0
        }

        override fun toString() = "*"

        override operator fun plus(b: Length): Length =
                when (b) {
                    is Unlimited -> Unlimited()
                    is AtLeast -> AtLeast(b.length)
                    is Strict -> Strict(b.length)
                    is Varying -> Varying(b.minLength, b.maxLength)
                }

    }

    class AtLeast(val length: Int) : Length() {

        override fun compareWithPosition(position: Int): Int {
            if (position + 1 < length) {
                return -1
            } else {
                return 0
            }
        }

        override fun toString() = if (length == 1) "+" else "{$length,}"

        override operator fun plus(b: Length): Length =
                when (b) {
                    is Unlimited -> AtLeast(length)
                    is AtLeast -> AtLeast(length + b.length)
                    is Strict -> AtLeast(length + b.length)
                    is Varying -> AtLeast(length + b.minLength)
                }
    }

    class Strict(val length: Int) : Length() {

        override fun compareWithPosition(position: Int): Int {
            if (position + 1 < length) {
                return -1
            } else if (position + 1 > length) {
                return 1
            } else {
                return 0
            }
        }

        override fun toString() = "{$length}"

        override operator fun plus(b: Length): Length =
                when (b) {
                    is Unlimited -> AtLeast(length)
                    is AtLeast -> AtLeast(length + b.length)
                    is Strict -> Strict(length + b.length)
                    is Varying -> Varying(length + b.minLength, length + b.maxLength)
                }
    }

    class Varying(val minLength: Int, val maxLength: Int) : Length() {

        override fun toString() = "{$minLength, $maxLength}"

        override fun compareWithPosition(position: Int): Int {
            if (position + 1 < minLength) {
                return -1
            } else if (position + 1 > maxLength) {
                return 1
            } else {
                return 0
            }
        }

        override operator fun plus(b: Length): Length =
                when (b) {
                    is Unlimited -> AtLeast(minLength)
                    is AtLeast -> AtLeast(minLength + b.length)
                    is Strict -> Varying(minLength + b.length, maxLength + b.length)
                    is Varying -> Varying(minLength + b.minLength, maxLength + b.maxLength)
                }
    }

    abstract fun compareWithPosition(position: Int): Int

    abstract operator fun plus(b: Length): Length
}