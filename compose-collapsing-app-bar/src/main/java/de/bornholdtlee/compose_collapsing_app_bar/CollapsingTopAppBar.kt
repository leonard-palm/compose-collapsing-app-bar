package de.bornholdtlee.compose_collapsing_app_bar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun CollapsingTopAppBarLayout(
    modifier: Modifier = Modifier,
    state: CollapsingTopAppBarState = rememberCollapsingTopAppBarState(),
    barStaticContent: @Composable (collapsingState: CollapsingState) -> Unit,
    barStaticBackgroundColor: Color = MaterialTheme.colors.primary,
    barCollapsingContent: @Composable (collapsingState: CollapsingState) -> Unit,
    barCollapsingBackgroundColor: Color = MaterialTheme.colors.primaryVariant,
    barCollapsingRadiusBottomStart: Dp = 0.dp,
    barCollapsingRadiusBottomEnd: Dp = 0.dp,
    endedInPartialTransitionStrategy: EndedInPartialTransitionStrategy = EndedInPartialTransitionStrategy.CollapseOrExpandToNearest(),
    screenContent: @Composable ColumnScope.() -> Unit
) {

    LaunchedEffect(key1 = state.scrollState.isScrollInProgress) {
        if (!state.scrollState.isScrollInProgress) {
            state.handleEndedInTransition(
                endedInPartialTransitionStrategy = endedInPartialTransitionStrategy
            )
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {

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
                        state.onStaticBarMeasureResult(layoutCoordinates.size.height)
                    }
            ) {
                barStaticContent(state.collapsingState)
            }

            Measure(
                content = @Composable {
                    barCollapsingContent(CollapsingState.Initialising)
                },
                onMeasured = { size ->
                    state.onCollapsingBarMeasureResult(size.height)
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = state.collapsibleHeightPx.toDp()
                    )
                    .background(
                        color = barCollapsingBackgroundColor,
                        shape = RoundedCornerShape(
                            bottomStart = barCollapsingRadiusBottomStart,
                            bottomEnd = barCollapsingRadiusBottomEnd
                        )
                    )

            ) {
                barCollapsingContent(state.collapsingState)
            }
        }

        Column(
            modifier = Modifier
                .zIndex(0f)
                .fillMaxSize()
                .verticalScroll(state.scrollState),
        ) {
            Spacer(
                modifier = Modifier.height(
                    height = state.totalBarHeightPx.toDp()
                )
            )
            this.screenContent()
        }
    }
}

sealed class EndedInPartialTransitionStrategy {

    object Stay : EndedInPartialTransitionStrategy()

    data class Collapse(val animationSpec: AnimationSpec<Float> = SpringSpec()) : EndedInPartialTransitionStrategy()

    data class Expand(val animationSpec: AnimationSpec<Float> = SpringSpec()) : EndedInPartialTransitionStrategy()

    data class CollapseOrExpandToNearest(
        val animationSpec: AnimationSpec<Float> = SpringSpec(),
        val threshold: Float = 0.5f
    ) : EndedInPartialTransitionStrategy()
}
