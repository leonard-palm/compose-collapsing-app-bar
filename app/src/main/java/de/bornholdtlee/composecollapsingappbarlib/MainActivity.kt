package de.bornholdtlee.composecollapsingappbarlib

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bornholdtlee.compose_collapsing_app_bar.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeCollapsingAppBarLibTheme {
                ExampleLazyColumn()
            }
        }
    }
}

@Composable
private fun ExampleColumn() {

    val collapsingTopAppBarState = rememberCollapsingTopAppBarColumnState()

    CollapsingTopAppBarLayout(
        modifier = Modifier.fillMaxSize(),
        state = collapsingTopAppBarState,
        barStaticContent = { collapsingState: CollapsingState ->
            BarStaticContent(collapsingState)
        },
        barStaticBackgroundColor = MaterialTheme.colors.primary,
        barCollapsingContent = { collapsingState: CollapsingState ->
            BarCollapsingContent(collapsingState)
        },
        barCollapsingBackgroundColor = MaterialTheme.colors.primaryVariant,
        barCollapsingRadiusBottomStart = 16.dp,
        barCollapsingRadiusBottomEnd = 16.dp,
        endedInPartialTransitionStrategy = EndedInPartialTransitionStrategy.CollapseOrExpandToNearest(),
        screenContent = {
            MainContentColumn()
        }
    )
}

@Composable
private fun ExampleLazyColumn() {

    val collapsingTopAppBarState = rememberCollapsingTopAppBarLazyColumnState()

    CollapsingTopAppBarLazyLayout(
        modifier = Modifier.fillMaxSize(),
        state = collapsingTopAppBarState,
        barStaticContent = { collapsingState: CollapsingState ->
            BarStaticContent(collapsingState)
        },
        barStaticBackgroundColor = MaterialTheme.colors.primary,
        barCollapsingContent = { collapsingState: CollapsingState ->
            BarCollapsingContent(collapsingState)
        },
        barCollapsingBackgroundColor = MaterialTheme.colors.primaryVariant,
        barCollapsingRadiusBottomStart = 16.dp,
        barCollapsingRadiusBottomEnd = 16.dp,
        endedInPartialTransitionStrategy = EndedInPartialTransitionStrategy.CollapseOrExpandToNearest(),
        screenContent = {
            mainContentLazyColumn()
        }
    )
}

fun LazyListScope.mainContentLazyColumn() {

    item {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = "Upcoming Matches",
            style = LocalTextStyle.current.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }

    items(
        count = 20,
    ) {
        ListItemNextGame(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun BarStaticContent(collapsingState: CollapsingState) {

    val elevation: Dp = 8.dp * (1f - collapsingState.progress)

    TopAppBar(
        elevation = elevation,
    ) {

        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = LocalContentColor.current
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
                .height(56.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            this@TopAppBar.AnimatedVisibility(
                visible = collapsingState is CollapsingState.Collapsed,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {


                Row {

                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.barcelona),
                        contentDescription = null
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "3 : 2",
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(40.dp)
                                .clip(RoundedCornerShape(percent = 50)),
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }

                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.bayern_muenchen),
                        contentDescription = null
                    )
                }
            }

            this@TopAppBar.AnimatedVisibility(
                visible = collapsingState !is CollapsingState.Collapsed,
                enter = slideInVertically(
                    initialOffsetY = { -it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { -it }
                ) + fadeOut()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Champions League",
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_notifications_active),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = LocalContentColor.current
                )
            )
        }
    }
}

@Composable
private fun BarCollapsingContent(collapsingState: CollapsingState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .alpha(1F - ((1F - collapsingState.progress) * 1.5F)),
        horizontalAlignment = Alignment.End
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {

                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.barcelona),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FC\nBarcelona",
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = "3 : 2",
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Live 56'",
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    modifier = Modifier
                        .width(40.dp)
                        .clip(RoundedCornerShape(percent = 50))
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {

                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.bayern_muenchen),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bayern München",
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            modifier = Modifier.alpha(
                alpha = if (collapsingState is CollapsingState.Expanded) 1f else 0f
            ),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Transparent,
                contentColor = LocalTextStyle.current.color
            ),
            border = BorderStroke(
                width = ButtonDefaults.OutlinedBorderSize,
                color = MaterialTheme.colors.primary
            ),
            onClick = { },
        ) {

            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_live_tv),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Watch",
                style = LocalTextStyle.current.copy(
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MainContentColumn() {

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "Upcoming Matches",
        style = LocalTextStyle.current.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    )

    for (i in 0..9) {
        ListItemNextGame(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ListItemNextGame(
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Row {

                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.bayern_muenchen),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Bayern München",
                    style = LocalTextStyle.current.copy(
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {

                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.barcelona),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "FC Barcelona",
                    style = LocalTextStyle.current.copy(
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

        }

        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxHeight(fraction = 0.8F)
                .width(1.dp)
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = "Fr., 20.1.\n20:30",
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                fontSize = 13.sp
            )
        )
    }

}
