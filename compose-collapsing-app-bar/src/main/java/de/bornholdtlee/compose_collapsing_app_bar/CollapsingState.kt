package de.bornholdtlee.compose_collapsing_app_bar

import androidx.annotation.FloatRange

sealed class CollapsingState(
    @FloatRange(0.0, 1.0) open val progress: Float
) {

    companion object {

        const val PROGRESS_VALUE_COLLAPSED: Float = 0F
        const val PROGRESS_VALUE_EXPANDED: Float = 1F

        internal fun fromProgress(
            @FloatRange(0.0, 1.0) progress: Float
        ): CollapsingState {
            assert(progress in PROGRESS_VALUE_COLLAPSED..PROGRESS_VALUE_EXPANDED)
            return when (progress) {
                PROGRESS_VALUE_COLLAPSED -> Collapsed
                PROGRESS_VALUE_EXPANDED -> Expanded
                else -> InTransition(progress)
            }
        }
    }

    object Collapsed : CollapsingState(progress = PROGRESS_VALUE_COLLAPSED)

    object Expanded : CollapsingState(progress = PROGRESS_VALUE_EXPANDED)

    data class InTransition(override val progress: Float) : CollapsingState(progress) {

        enum class PreferredProgressDirection {
            EXPAND,
            COLLAPSE
        }

        fun determinePreferredProgressDirection(
            @FloatRange(0.0, 1.0) threshold: Float
        ): PreferredProgressDirection {
            assert(threshold in 0F..1F) { "'threshold' must be in range 0.0 .. 1.0" }
            return when {
                progress >= threshold -> PreferredProgressDirection.EXPAND
                else -> PreferredProgressDirection.COLLAPSE
            }
        }
    }
}
