package com.mhss.app.prayfirst.presentation.overlay

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mhss.app.prayfirst.R
import com.mhss.app.prayfirst.ui.theme.PrayFirstTheme

@Composable
fun PrayFirstOverlay(
    prayerNameRes: Int,
    remainingTimeString: String,
    progress: Float
) {
    PrayFirstTheme(inOverLay = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.pray_first),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    stringResource(R.string.prayer_x_time_started, stringResource(prayerNameRes)),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    stringResource(R.string.prayer_on_time_hadeeth),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    stringResource(R.string.time_to_unlock),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(10.dp))
                TimerOverlayIndicator(
                    modifier = Modifier.fillMaxWidth(0.65f),
                    progress = progress,
                    timerText = remainingTimeString
                )
            }
        }
    }
}

@Composable
fun TimerOverlayIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    timerText: String
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val measurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.headlineLarge
    val animatable = remember {
        Animatable(1f)
    }
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    val level by remember(animatable.value, size.height) {
        derivedStateOf {
            animatable.value * size.height
        }
    }

    LaunchedEffect(progress) {
        animatable.animateTo(
            progress,
            animationSpec = tween(1000)
        )
    }
    var textOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    Box(
        modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(4.dp, primaryColor, CircleShape)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            size = this.size
            drawRect(
                color = primaryColor,
                topLeft = Offset(0f, level),
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawBlendedText(
                        level = level,
                        timerText = timerText,
                        measurer = measurer,
                        inverseColor = Color.White,
                        primaryColor = primaryColor,
                        textOffset = textOffset,
                        textStyle = textStyle
                    )
                }
        ) {
            Text(
                timerText,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.Center)
                    .onGloballyPositioned {
                        textOffset = it.positionInParent()
                    }
            )
        }
    }
}

private fun DrawScope.drawBlendedText(
    level: Float,
    primaryColor: Color,
    inverseColor: Color,
    measurer: TextMeasurer,
    textOffset: Offset,
    timerText: String,
    textStyle: TextStyle
) {
    drawText(
        textMeasurer = measurer,
        topLeft = textOffset,
        text = timerText,
        style = textStyle.copy(
            color = primaryColor
        )
    )

    drawRect(
        color = inverseColor,
        topLeft = Offset(0f, level),
        blendMode = BlendMode.SrcIn
    )
}

@Preview
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "ar",
)
@Composable
fun PrayFirstOverlayPreview() {
    PrayFirstOverlay(
        prayerNameRes = R.string.fajr,
        remainingTimeString = "09:32",
        progress = 0.5f
    )
}