package de.bornholdtlee.compose_collapsing_app_bar

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

@Composable
fun Measure(
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
