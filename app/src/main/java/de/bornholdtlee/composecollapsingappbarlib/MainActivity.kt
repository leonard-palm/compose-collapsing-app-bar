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
import de.bornholdtlee.compose_collapsing_app_bar.CollapsingTopAppBarLayout
import de.bornholdtlee.compose_collapsing_app_bar.rememberCustomScrollState

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val scrollState = rememberCustomScrollState()

            CollapsingTopAppBarLayout(
                modifier = Modifier.fillMaxSize(),
                scrollState = scrollState,
                barStaticContent = { growthFactor: Float ->
                    BarStaticContent(
                        growthFactor = growthFactor
                    )
                },
                barCollapsingContent = { growthFactor: Float ->
                    BarCollapsingContent(
                        growthFactor = growthFactor
                    )
                },
                barCollapsingRadiusBottomStart = 12.dp,
                barCollapsingRadiusBottomEnd = 12.dp,
                screenContent = {
                    MainContent()
                }
            )
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
        Text("Static")
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
    for (i in 1..100) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Content"
        )
    }
}