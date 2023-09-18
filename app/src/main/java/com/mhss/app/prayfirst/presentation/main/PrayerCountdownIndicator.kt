package com.mhss.app.prayfirst.presentation.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import com.mhss.app.prayfirst.domain.model.PrayerTime
import com.mhss.app.prayfirst.domain.model.PrayerTimeType
import com.mhss.app.prayfirst.ui.theme.PrayFirstTheme

@Composable
fun PrayerCountdownIndicator(
    modifier: Modifier = Modifier,
    prayerData: MainViewModel.NextPrayerData
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val measurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.headlineLarge
    val prayerName = stringResource(prayerData.prayer.nameResId)
    val animatable = remember {
        Animatable(1f)
    }
    val progress = animatable.value
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    val level by remember(progress, size.height) {
        derivedStateOf {
            progress * size.height
        }
    }

    var textPosition by remember {
        mutableStateOf(Offset.Zero)
    }
    var timerPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    LaunchedEffect(prayerData.progress) {
        animatable.animateTo(
            prayerData.progress,
            animationSpec = tween(1000)
        )
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
                        prayerName = prayerName,
                        timerText = prayerData.remainingTimeString,
                        measurer = measurer,
                        inverseColor = Color.White,
                        primaryColor = primaryColor,
                        textOffset = textPosition,
                        timerOffset = timerPosition,
                        textStyle = textStyle
                    )
                }
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(prayerData.prayer.nameResId),
                    style = textStyle,
                    modifier = Modifier.onGloballyPositioned {
                        textPosition = it.positionInParent()
                    }
                )
                Text(
                    prayerData.remainingTimeString,
                    style = textStyle,
                    modifier = Modifier.onGloballyPositioned {
                        timerPosition = it.positionInParent()
                    }
                )
            }
        }
    }
}

private fun DrawScope.drawBlendedText(
    level: Float,
    primaryColor: Color,
    inverseColor: Color,
    measurer: TextMeasurer,
    textOffset: Offset,
    prayerName: String,
    timerText: String,
    timerOffset: Offset,
    textStyle: TextStyle
) {
    drawText(
        textMeasurer = measurer,
        topLeft = textOffset,
        text = prayerName,
        style = textStyle.copy(
            color = primaryColor
        )
    )

    drawText(
        textMeasurer = measurer,
        topLeft = timerOffset,
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

@Preview(
    showBackground = true
)
@Composable
fun PrayerIndicatorPreview() {
    PrayFirstTheme {
        PrayerCountdownIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            prayerData = MainViewModel.NextPrayerData(
                PrayerTime("", 0, R.string.maghrib, PrayerTimeType.MAGHRIB),
                0.5f,
                "01:34:56"
            )
        )
    }
}