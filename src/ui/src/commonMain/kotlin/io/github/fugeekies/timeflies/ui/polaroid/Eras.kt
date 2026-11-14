package io.github.fugeekies.timeflies.ui.polaroid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * The decade of an event, which gives the Polaroïd frame its colour and the colour of the decade stamp
 * written in the corner of the bottom band (decision of 14/11/2026, docs/UI_DESIGN_SPEC.md, § 1).
 * Frame colours stay light so the handwriting keeps a dark ink.
 */
enum class Era(val frame: Color) {
    BEFORE_1960(Color(0xFFEFE3C4)),
    SIXTIES(Color(0xFFCFE8DC)),
    SEVENTIES(Color(0xFFF2D3A0)),
    EIGHTIES(Color(0xFFF6C6D9)),
    NINETIES(Color(0xFFBFE6E3)),
    TWO_THOUSANDS(Color(0xFFDADDE2)),
    TWENTY_TENS(Color(0xFFFBFAF7)),
    TWENTY_TWENTIES(Color(0xFFDDD3F0)),
    ;

    /** Colour of the decade stamp: the frame, darkened towards the ink. */
    val stamp: Color get() = lerp(frame, Color(0xFF2B2420), 0.45f)

    companion object {
        fun of(year: Int): Era = when {
            year < 1960 -> BEFORE_1960
            year < 1970 -> SIXTIES
            year < 1980 -> SEVENTIES
            year < 1990 -> EIGHTIES
            year < 2000 -> NINETIES
            year < 2010 -> TWO_THOUSANDS
            year < 2020 -> TWENTY_TENS
            else -> TWENTY_TWENTIES
        }

        /**
         * The decade as people say it: "70" for 1979, "30" for 1938, but "2000" and "2010" from 2000 on
         * ("les années 70", "les années 2000").
         */
        fun decadeNumber(year: Int): String {
            val decade = year / 10 * 10
            return if (decade >= 2000) decade.toString() else (decade % 100).toString().padStart(2, '0')
        }
    }
}
