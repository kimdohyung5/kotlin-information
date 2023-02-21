

1) animation관련 내용
   아래와 같이 설정을 하면 값이 변경이 된다.

    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if(animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )

    
  2) onGloballyPositioned 예제 코드
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var screen by mutableStateOf(0)
        val pages = listOf("Page 1", "Page 2", "Page 3", "Page 4" )

        setContent {
            NewCanvasTheme {
                CustomTabs(
                    pages = pages,
                    screen = screen,
                    onClick = { screen = it }
                )
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun CustomTabsPreview() {
        val pages = listOf("Page 1", "Page 2", "Page 3", "Page 4" )
        NewCanvasTheme() {
            CustomTabs(
                screen = 3,
                onClick = {},
                pages = pages
            )
        }
    }

    @Composable
    fun CustomTabs(screen: Int, onClick: (Int) -> Unit, pages: List<String>) {
        val blackColor = MaterialTheme.colors.onBackground
        var endPosition by remember { mutableStateOf(Pair(0f,0f)) }
        var startPosition by remember { mutableStateOf(Pair(0f,0f)) }

        Canvas( modifier = Modifier.fillMaxWidth()) {
            drawLine(
                strokeWidth = 4f,
                color = blackColor,
                end = Offset(x = endPosition.first, y = endPosition.second),
                start = Offset(x = startPosition.first, y = startPosition.second)
            )
        }

        Row( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {


            pages.forEachIndexed { index, page ->
                Column( horizontalAlignment = Alignment.CenterHorizontally) {
                    HeaderBox(title = page, active = screen == index, visible = screen >= index )

                    Spacer( modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier
                        .size(12.dp)
                        .onGloballyPositioned {
                            when(index) {
                                0 -> startPosition = it.getCenter()
                                pages.lastIndex -> endPosition = it.getCenter()
                            }
                        }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = blackColor,
                                radius = if( screen == index) 12f else 8f
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HeaderBox( title: String, active: Boolean, visible: Boolean) {
        Box( modifier = Modifier.size(width = 50.dp, height = 25.dp)) {
            AnimatedVisibility(visible = visible) {
                Text(
                    text = title,
                    fontWeight = if(active) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }

    private fun LayoutCoordinates.getCenter(): Pair<Float, Float> {
        val bounds = this.boundsInRoot()

        val radius = (bounds.right - bounds.left) /2
        val yCenter = bounds.top + radius
        val xCenter = bounds.left + radius

        return Pair( xCenter, yCenter )
    }
}
