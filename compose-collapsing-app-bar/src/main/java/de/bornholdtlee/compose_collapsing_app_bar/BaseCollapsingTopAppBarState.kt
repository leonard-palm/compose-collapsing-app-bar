package de.bornholdtlee.compose_collapsing_app_bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberCollapsingTopAppBarColumnState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scrollState: CustomScrollState = rememberCustomScrollState()
) = remember(key1 = coroutineScope, key2 = scrollState) {
    CollapsingTopAppBarColumnState(
        coroutineScope = coroutineScope,
        scrollState = scrollState
    )
}

@Stable
class CollapsingTopAppBarColumnState(
    coroutineScope: CoroutineScope,
    internal val scrollState: CustomScrollState
) : BaseCollapsingTopAppBarState(
    coroutineScope = coroutineScope
) {
    override val scrollingOffsetPx: Float
        get() = scrollState.value

    override val collapsibleHeightPx: State<Int>
        get() = derivedStateOf {
            (barCollapsibleContentExpandedHeightPx - scrollingOffsetPx.toInt()).coerceAtLeast(0)
        }

    override fun collapse(animationSpec: AnimationSpec<Float>) {
        coroutineScope.launch {
            scrollState.animateScrollTo(
                value = barCollapsibleContentExpandedHeightPx,
                animationSpec = animationSpec
            )
        }
    }

    override fun expand(animationSpec: AnimationSpec<Float>) {
        coroutineScope.launch {
            scrollState.animateScrollTo(
                value = 0,
                animationSpec = animationSpec
            )
        }
    }
}

@Composable
fun rememberCollapsingTopAppBarLazyColumnState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState()
) = remember(key1 = coroutineScope, key2 = lazyListState) {
    CollapsingTopAppBarLazyColumnState(
        coroutineScope = coroutineScope,
        lazyListState = lazyListState
    )
}

@Stable
class CollapsingTopAppBarLazyColumnState(
    coroutineScope: CoroutineScope,
    internal val lazyListState: LazyListState
) : BaseCollapsingTopAppBarState(
    coroutineScope = coroutineScope
) {
    override val scrollingOffsetPx: Float
        get() = lazyListState.firstVisibleItemScrollOffset.toFloat()

    override val collapsibleHeightPx: State<Int>
        get() = derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                (barCollapsibleContentExpandedHeightPx - scrollingOffsetPx.toInt()).coerceAtLeast(0)
            } else 0
        }

    override fun collapse(animationSpec: AnimationSpec<Float>) {
        coroutineScope.launch {
            lazyListState.animateScrollToItem(
                index = 1,
                scrollOffset = -barStaticContentHeightPx
            )
        }
    }

    override fun expand(animationSpec: AnimationSpec<Float>) {
        coroutineScope.launch {
            lazyListState.animateScrollToItem(
                index = 0
            )
        }
    }
}

@Stable
abstract class BaseCollapsingTopAppBarState(
    internal val coroutineScope: CoroutineScope
) {

    internal abstract val scrollingOffsetPx: Float

    internal var barStaticContentHeightPx: Int by mutableStateOf(0)

    internal var barCollapsibleContentExpandedHeightPx: Int by mutableStateOf(0)
        private set

    internal val totalBarHeightPx: Int by derivedStateOf {
        barStaticContentHeightPx + barCollapsibleContentExpandedHeightPx
    }

    internal abstract val collapsibleHeightPx: State<Int>

    val collapsingState: CollapsingState
        get() {
            if (barCollapsibleContentExpandedHeightPx == 0) {
                return CollapsingState.Initialising
            }
            if (barCollapsibleContentExpandedHeightPx <= 0 || collapsibleHeightPx.value <= 0) {
                return CollapsingState.Collapsed
            }
            if (collapsibleHeightPx.value > barCollapsibleContentExpandedHeightPx) {
                return CollapsingState.Expanded
            }
            return CollapsingState.fromProgress(
                progress = collapsibleHeightPx.value.toFloat() / barCollapsibleContentExpandedHeightPx.toFloat()
            )
        }

    internal fun onStaticBarMeasureResult(heightPx: Int) {
        barStaticContentHeightPx = heightPx
    }

    internal fun onCollapsingBarMeasureResult(heightPx: Int) {
        barCollapsibleContentExpandedHeightPx = heightPx
    }

    abstract fun collapse(animationSpec: AnimationSpec<Float> = SpringSpec())

    abstract fun expand(animationSpec: AnimationSpec<Float> = SpringSpec())

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
