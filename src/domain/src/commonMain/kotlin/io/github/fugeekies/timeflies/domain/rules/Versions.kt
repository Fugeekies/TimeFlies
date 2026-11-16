package io.github.fugeekies.timeflies.domain.rules

/** Versions "X.Y.Z" of the releases (docs/ARCHITECTURE.md, "Distribution"). */
object Versions {
    /** "1.2.3" or "v1.2.3" as three numbers; null for anything else. */
    fun parse(text: String): List<Int>? {
        val parts = text.trim().removePrefix("v").split('.')
        if (parts.size != 3) return null
        return parts.map { it.toIntOrNull()?.takeIf { n -> n >= 0 } ?: return null }
    }

    /** True when [candidate] comes after [current]; false when either cannot be read. */
    fun isNewer(candidate: String, current: String): Boolean {
        val a = parse(candidate) ?: return false
        val b = parse(current) ?: return false
        for (i in 0..2) if (a[i] != b[i]) return a[i] > b[i]
        return false
    }
}
