package io.github.fugeekies.timeflies.domain.model

import kotlin.jvm.JvmInline

/** A country, as an ISO 3166-1 alpha-2 code in upper case ("FR", "US"). */
@JvmInline
value class Region private constructor(val code: String) {
    override fun toString(): String = code

    companion object {
        private val pattern = Regex("^[A-Z]{2}$")

        /** The region for this code, or null if the code is not two upper-case letters. */
        fun of(code: String): Region? = if (pattern.matches(code)) Region(code) else null

        val FRANCE = Region("FR")
    }
}
