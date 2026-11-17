package io.github.fugeekies.timeflies.domain.model

/**
 * The display style of the whole app and of the widget (docs/UI_DESIGN_SPEC.md, § 1), chosen in Settings ›
 * Affichage. Polaroïd by default; VHS since milestone 14 (decision of 17/11/2026), Éphéméride since milestone 15.
 *
 * @property id the value written in `settings.json`, readable by hand.
 */
enum class DisplayStyle(val id: String) {
    POLAROID("polaroid"),
    VHS("vhs"),
    EPHEMERIS("ephemeride");

    companion object {
        /** The style of [id], or null for an unknown value (a file edited by hand, or written by a newer version). */
        fun of(id: String): DisplayStyle? = entries.firstOrNull { it.id == id }
    }
}
