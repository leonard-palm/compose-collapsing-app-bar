package de.bornholdtlee.composecollapsingappbarlib

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.bornholdtlee.compose_collapsing_app_bar.CollapsingState
import de.bornholdtlee.compose_collapsing_app_bar.CollapsingTopAppBarLayout
import de.bornholdtlee.compose_collapsing_app_bar.EndedInPartialTransitionStrategy
import de.bornholdtlee.compose_collapsing_app_bar.rememberCollapsingTopAppBarState

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            ComposeCollapsingAppBarLibTheme {

                val collapsingTopAppBarState = rememberCollapsingTopAppBarState()

                CollapsingTopAppBarLayout(
                    modifier = Modifier.fillMaxSize(),
                    state = collapsingTopAppBarState,
                    barStaticContent = { collapsingState: CollapsingState ->
                        BarStaticContent(
                            growthFactor = collapsingState.progress
                        )
                    },
                    barCollapsingContent = { collapsingState: CollapsingState ->
                        BarCollapsingContent(
                            growthFactor = collapsingState.progress
                        )
                    },
                    barCollapsingRadiusBottomStart = 12.dp,
                    barCollapsingRadiusBottomEnd = 12.dp,
                    endedInPartialTransitionStrategy = EndedInPartialTransitionStrategy.Stay,
                    screenContent = {
                        MainContent()
                    }
                )
            }
        }
    }
}

@Composable
private fun BarStaticContent(growthFactor: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Static $growthFactor")
    }
}

@Composable
private fun BarCollapsingContent(growthFactor: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(growthFactor)
    ) {
        Text("Collapsing")
        Text("Collapsing")
        Text("Collapsing")
        Text("Collapsing")
        Text("Collapsing")
        Text("Collapsing")
    }
}

@Composable
private fun MainContent() {
    for (i in 0..99) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Content $i"
        )
    }
}
