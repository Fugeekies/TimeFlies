package io.github.fugeekies.timeflies.ui.theme

import androidx.compose.runtime.Composable

/**
 * Whether the system asks for fewer animations (docs/UI_DESIGN_SPEC.md, "Accessibilité"): the photo is then shown
 * at once, without developing. Null while the platform has not answered yet.
 */
@Composable
expect fun rememberReducedMotion(): Boolean?
