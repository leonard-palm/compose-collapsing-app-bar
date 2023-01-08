package de.bornholdtlee.compose_collapsing_app_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import de.bornholdtlee.compose_collapsing_app_bar.CollapsingState.InTransition.PreferredProgressDirection

@Composable
fun CollapsingTopAppBarLayout(
    modifier: Modifier = Modifier,
    scrollState: CustomScrollState,
    barStaticContent: @Composable (collapsingState: CollapsingState) -> Unit,
    barStaticBackgroundColor: Color = MaterialTheme.colors.primary,
    barCollapsingContent: @Composable (collapsingState: CollapsingState) -> Unit,
    barCollapsingBackgroundColor: Color = MaterialTheme.colors.primaryVariant,
    barCollapsingRadiusBottomStart: Dp = 0.dp,
    barCollapsingRadiusBottomEnd: Dp = 0.dp,
    endedInPartialTransitionStrategy: EndedInPartialTransitionStrategy = EndedInPartialTransitionStrategy.COLLAPSE_OR_EXPAND_TO_NEAREST,
    screenContent: @Composable ColumnScope.() -> Unit
) {

    Box(
        modifier = modifier.fillMaxWidth()
    ) {

        // Measured height of the static top part
        var barStaticContentHeightPx: Int by remember { mutableStateOf(0) }

        // Measured height of the collapsible part but in its expanded state
        var barCollapsibleContentExpandedHeightPx: Int by remember { mutableStateOf(0) }

        // Total app bar height in its expanded state
        val totalBarHeightPx: Int by remember(barStaticContentHeightPx) {
            derivedStateOf {
                barStaticContentHeightPx + barCollapsibleContentExpandedHeightPx
            }
        }

        // Calculated height of the collapsible content depending on the scroll offset
        val collapsibleHeightPx: Int by remember(
            key1 = totalBarHeightPx,
            key2 = scrollState.value
        ) {
            derivedStateOf {
                (barCollapsibleContentExpandedHeightPx - scrollState.value.toInt())
                    .coerceAtLeast(0)
            }
        }

        // TODO: Move to dedicated state holder
        val collapsingState: CollapsingState by remember(
            key1 = collapsibleHeightPx
        ) {
            derivedStateOf {
                determineCollapsingState(
                    totalExpandedHeight = barCollapsibleContentExpandedHeightPx,
                    currentHeight = collapsibleHeightPx
                )
            }
        }

        // TODO: Move to dedicated state holder
        suspend fun collapse() {
            scrollState.animateScrollTo(value = barCollapsibleContentExpandedHeightPx)

        }

        // TODO: Move to dedicated state holder
        suspend fun expand() {
            scrollState.animateScrollTo(value = 0)
        }

        // TODO: Move to dedicated state holder
        suspend fun onHandleEndedInTransition(inTransitionState: CollapsingState.InTransition) {
            when (endedInPartialTransitionStrategy) {
                EndedInPartialTransitionStrategy.STAY -> {}
                EndedInPartialTransitionStrategy.COLLAPSE -> collapse()
                EndedInPartialTransitionStrategy.EXPAND -> expand()
                EndedInPartialTransitionStrategy.COLLAPSE_OR_EXPAND_TO_NEAREST -> {
                    when (inTransitionState.preferredProgressDirection) {
                        PreferredProgressDirection.EXPAND -> expand()
                        PreferredProgressDirection.COLLAPSE -> collapse()
                    }
                }
            }
        }

        LaunchedEffect(key1 = scrollState.isScrollInProgress) {
            if (!scrollState.isScrollInProgress) {
                collapsingState.let { immutableCollapsingState ->
                    if (immutableCollapsingState is CollapsingState.InTransition) {
                        onHandleEndedInTransition(
                            inTransitionState = immutableCollapsingState
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.zIndex(1F)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = barStaticBackgroundColor
                    )
                    .onGloballyPositioned { layoutCoordinates ->
                        barStaticContentHeightPx = layoutCoordinates.size.height
                    }
            ) {
                barStaticContent(collapsingState)
            }

            Measure(
                content = @Composable {
                    barCollapsingContent(CollapsingState.Collapsed)
                },
                onMeasured = { size ->
                    barCollapsibleContentExpandedHeightPx = size.height
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = collapsibleHeightPx.toDp()
                    )
                    .background(
                        color = barCollapsingBackgroundColor,
                        shape = RoundedCornerShape(
                            bottomStart = barCollapsingRadiusBottomStart,
                            bottomEnd = barCollapsingRadiusBottomEnd
                        )
                    )

            ) {
                barCollapsingContent(collapsingState)
            }
        }

        Column(
            modifier = Modifier
                .zIndex(0f)
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Spacer(
                modifier = Modifier.height(
                    height = totalBarHeightPx.toDp()
                )
            )
            this.screenContent()
        }
    }
}

enum class EndedInPartialTransitionStrategy {
    STAY,
    COLLAPSE,
    EXPAND,
    COLLAPSE_OR_EXPAND_TO_NEAREST
}

private fun determineCollapsingState(
    totalExpandedHeight: Int,
    currentHeight: Int
): CollapsingState {
    if (totalExpandedHeight <= 0 || currentHeight < 0 || currentHeight > totalExpandedHeight) return CollapsingState.Collapsed
    return CollapsingState.fromProgress(
        progress = currentHeight.toFloat() / totalExpandedHeight.toFloat()
    )
}
