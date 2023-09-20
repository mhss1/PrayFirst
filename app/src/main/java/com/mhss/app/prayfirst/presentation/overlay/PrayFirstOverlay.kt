package com.mhss.app.prayfirst.presentation.overlay

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
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
                Spacer(Modifier.height(32.dp))
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
    val animatable = remember {
        Animatable(0f)
    }

    LaunchedEffect(progress) {
        animatable.animateTo(
            progress,
            animationSpec = tween(1000)
        )
    }

    val darkTheme = isSystemInDarkTheme()
    Box(
        modifier.aspectRatio(1f)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawArc(
                color = if (darkTheme) Color.Gray else Color.LightGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx())
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = animatable.value * 360f,
                useCenter = false,
                style = Stroke(
                    width = 24.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        Text(
            text = timerText,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.Center),
            color = primaryColor
        )
    }
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