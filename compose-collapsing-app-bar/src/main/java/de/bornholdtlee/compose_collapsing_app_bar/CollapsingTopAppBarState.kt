package de.bornholdtlee.compose_collapsing_app_bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberCollapsingTopAppBarState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scrollState: CustomScrollState = rememberCustomScrollState()
) = remember(scrollState) {
    CollapsingTopAppBarState(
        coroutineScope = coroutineScope,
        scrollState = scrollState
    )
}

@Stable
class CollapsingTopAppBarState(
    private val coroutineScope: CoroutineScope,
    internal val scrollState: CustomScrollState
) {

    private var barStaticContentHeightPx: Int by mutableStateOf(0)

    private var barCollapsibleContentExpandedHeightPx: Int by mutableStateOf(0)

    internal val totalBarHeightPx: Int by derivedStateOf {
        barStaticContentHeightPx + barCollapsibleContentExpandedHeightPx
    }

    internal val collapsibleHeightPx: Int by derivedStateOf {
        (barCollapsibleContentExpandedHeightPx - scrollState.value.toInt()).coerceAtLeast(0)
    }

    val collapsingState: CollapsingState
        get() {
            if (barCollapsibleContentExpandedHeightPx == 0) {
                return CollapsingState.Initialising
            }
            if (barCollapsibleContentExpandedHeightPx <= 0 || collapsibleHeightPx <= 0) {
                return CollapsingState.Collapsed
            }
            if (collapsibleHeightPx > barCollapsibleContentExpandedHeightPx) {
                return CollapsingState.Expanded
            }
            return CollapsingState.fromProgress(
                progress = collapsibleHeightPx.toFloat() / barCollapsibleContentExpandedHeightPx.toFloat()
            )
        }

    internal fun onStaticBarMeasureResult(heightPx: Int) {
        barStaticContentHeightPx = heightPx
    }

    internal fun onCollapsingBarMeasureResult(heightPx: Int) {
        barCollapsibleContentExpandedHeightPx = heightPx
    }

    fun collapse(animationSpec: AnimationSpec<Float> = SpringSpec()) {
        coroutineScope.launch {
            scrollState.animateScrollTo(
                value = barCollapsibleContentExpandedHeightPx,
                animationSpec = animationSpec
            )
        }
    }

    fun expand(animationSpec: AnimationSpec<Float> = SpringSpec()) {
        coroutineScope.launch {
            scrollState.animateScrollTo(
                value = 0,
                animationSpec = animationSpec
            )
        }
    }

    internal fun handleEndedInTransition(endedInPartialTransitionStrategy: EndedInPartialTransitionStrategy) {
        collapsingState.let { immutableCollapsingState ->
            if (immutableCollapsingState is CollapsingState.InTransition) {
                when (endedInPartialTransitionStrategy) {
                    EndedInPartialTransitionStrategy.Stay -> {
                        /* Do nothing */
                    }
                    is EndedInPartialTransitionStrategy.Collapse -> collapse(endedInPartialTransitionStrategy.animationSpec)
                    is EndedInPartialTransitionStrategy.Expand -> expand(endedInPartialTransitionStrategy.animationSpec)
                    is EndedInPartialTransitionStrategy.CollapseOrExpandToNearest -> {
                        when (
                            immutableCollapsingState.determinePreferredProgressDirection(
                                threshold = endedInPartialTransitionStrategy.threshold
                            )
                        ) {
                            CollapsingState.InTransition.PreferredProgressDirection.EXPAND -> expand(endedInPartialTransitionStrategy.animationSpec)
                            CollapsingState.InTransition.PreferredProgressDirection.COLLAPSE -> collapse(endedInPartialTransitionStrategy.animationSpec)
                        }
                    }
                }
            }
        }
    }
}
