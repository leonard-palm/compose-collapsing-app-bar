package de.bornholdtlee.compose_collapsing_app_bar

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun CollapsingTopAppBarLayout(
    modifier: Modifier = Modifier,
    scrollState: CustomScrollState,
    barStaticContent: @Composable (growthFactor: Float) -> Unit,
    barStaticBackgroundColor: Color = MaterialTheme.colors.primary,
    barCollapsingContent: @Composable (growthFactor: Float) -> Unit,
    barCollapsingBackgroundColor: Color = MaterialTheme.colors.primaryVariant,
    barCollapsingRadiusBottomStart: Dp = 0.dp,
    barCollapsingRadiusBottomEnd: Dp = 0.dp,
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

        val growthFactor: Float by remember(
            key1 = collapsibleHeightPx
        ) {
            derivedStateOf {
                calculateGrowthFactor(
                    totalExpandedHeight = barCollapsibleContentExpandedHeightPx,
                    currentHeight = collapsibleHeightPx
                )
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
                barStaticContent(growthFactor)
            }

            Measure(
                content = @Composable {
                    barCollapsingContent(0f)
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
                barCollapsingContent(growthFactor)
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

@Composable
private fun Measure(
    content: @Composable () -> Unit,
    onMeasured: (size: IntSize) -> Unit
) {
    SubcomposeLayout { constraints: Constraints ->
        val placeables: List<Placeable> = subcompose(
            slotId = 0,
            content = content
        ).map { measurable: Measurable ->
            measurable.measure(
                constraints = constraints.copy(minWidth = 0, minHeight = 0)
            )
        }
        val maxWidth: Int = placeables.sumOf { placeable -> placeable.width }
        val maxHeight: Int = placeables.sumOf { placeable -> placeable.height }
        onMeasured(IntSize(maxWidth, maxHeight))
        layout(0, 0) {}
    }
}

@FloatRange(from = 0.0, to = 1.0)
private fun calculateGrowthFactor(
    totalExpandedHeight: Int,
    currentHeight: Int
): Float {
    if (totalExpandedHeight <= 0 || currentHeight < 0 || currentHeight > totalExpandedHeight) return 0f
    return currentHeight.toFloat() / totalExpandedHeight.toFloat()
}

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) {
    toDp()
}
