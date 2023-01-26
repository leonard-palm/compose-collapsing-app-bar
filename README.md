# Compose Collapsing App Bar
![JitPack](https://img.shields.io/jitpack/version/com.github.leonard-palm/compose-collapsing-app-bar?color=%2523%25233cdb83&style=for-the-badge)
![GitHub](https://img.shields.io/github/license/leonard-palm/compose-collapsing-app-bar?color=%234185f3&style=for-the-badge)
![GitHub top language](https://img.shields.io/github/languages/top/leonard-palm/compose-collapsing-app-bar?color=%237f52ff&style=for-the-badge)

## Example
<img src="app/example_1.gif" alt="example_1" width="300"/>

## Usage

There are two implementations of this layout. The fitting implementation depends on your root composeable for the content below the top app bar. If you only need `Column` as your root, use `CollapsingTopAppBarLayout`. If your screen content is a `LazyColumn` than use the `CollapsingTopAppBarLazyLayout` implementation.

See the exact usage for your needs:

<details>
  <summary>Column implementation</summary>
  
  ```kotlin
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
        MainContent()
    }
)
  ```

### Parameters
- `state`: Optional. Can be used to pass an own `CustomScrollState` or `CoroutineScope`.
- `barStaticContent`: The composable that defines the top part (that will never scroll away). You receive the current `CollapsingState` if the scrolling is in progress. This can be used to animate your content fluently according to the current scrolling progress.
- `barStaticBackgroundColor`: Optional. Defaults to  `MaterialTheme.colors.primary`.
- `barCollapsingContent`: The composable that defines the collapsing part of the top app bar. This will shrink on scrolling down the `screenContent`. You receive the current `CollapsingState` if the scrolling is in progress. This can be used to animate your content fluently according to the current scrolling progress.
- `barCollapsingBackgroundColor`: Optional. Defaults to `MaterialTheme.colors.primaryVariant`.
- `barCollapsingRadiusBottomStart`: Optional. Defaults to `0dp`.
- `barCollapsingRadiusBottomEnd`: Optional. Defaults to `0dp`.
- `endedInPartialTransitionStrategy`: Optional. Defaults to `EndedInPartialTransitionStrategy.CollapseOrExpandToNearest()`. This defines how the `barCollapsingContent` behaves when the scrolling ended in state where it is not fully collapsed or expanded. Have a look into the java docs from `EndedInPartialTransitionStrategy` to see all possible strategies.
- `screenContent`: This is the main screen content composable. Your Content is being composed in a `ColumnScope` on root level.

</details>

<details>
  <summary>Lazy Column implementation</summary>
  
  ```kotlin
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
  ```

### Parameters
- `state`: Optional. Can be used to pass an own `LazyListState` or `CoroutineScope`.
- `barStaticContent`: The composable that defines the top part (that will never scroll away). You receive the current `CollapsingState` if the scrolling is in progress. This can be used to animate your content fluently according to the current scrolling progress.
- `barStaticBackgroundColor`: Optional. Defaults to  `MaterialTheme.colors.primary`.
- `barCollapsingContent`: The composable that defines the collapsing part of the top app bar. This will shrink on scrolling down the `screenContent`. You receive the current `CollapsingState` if the scrolling is in progress. This can be used to animate your content fluently according to the current scrolling progress.
- `barCollapsingBackgroundColor`: Optional. Defaults to `MaterialTheme.colors.primaryVariant`.
- `barCollapsingRadiusBottomStart`: Optional. Defaults to `0dp`.
- `barCollapsingRadiusBottomEnd`: Optional. Defaults to `0dp`.
- `endedInPartialTransitionStrategy`: Optional. Defaults to `EndedInPartialTransitionStrategy.CollapseOrExpandToNearest()`. This defines how the `barCollapsingContent` behaves when the scrolling ended in state where it is not fully collapsed or expanded. Have a look into the java docs from `EndedInPartialTransitionStrategy` to see all possible strategies.
- `screenContent`: This is the main screen content. Your Content is being composed in a `LazyListScope` on root level.

</details>

## Installation

Add it in your root build.gradle at the end of repositories
```gradle
allprojects {
   repositories {
       ...
       maven { url "https://jitpack.io" }
   }
}

```

Add the dependency to your module
```gradle
dependencies {
   implementation 'com.github.leonard-palm:compose-collapsing-app-bar:1.1.0'
}
``` 
