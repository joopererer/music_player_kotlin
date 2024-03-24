package com.jiawei.musicplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun RandomJumpingSpectrum(modifier: Modifier = Modifier) {
    var spectrumValues by remember { mutableStateOf(listOf(0f, 0f, 0f)) }

    LaunchedEffect(Unit) {
        while (isActive) {
            spectrumValues = List(3) { Random.nextFloat() * 100 % 55f }
            kotlinx.coroutines.delay(150)
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = modifier,
            onDraw = {
                drawSpectrum(this, spectrumValues, size.width)
            }
        )
    }
}

fun DrawScope.drawSpectrum(drawScope: DrawScope, values: List<Float>, size: Float) {
    val barWidth = size/4f
    val barSpacing = size/10f
    val barMaxHeight = size/2f

    values.forEachIndexed { index, value ->
        val x = (index * (barWidth + barSpacing)).toFloat()
        val y = size + (barMaxHeight - value)
        drawRect(
            color = Color.Blue,
            topLeft = Offset(x, y),
            size = Size(barWidth, value)
        )
    }
}

@Preview
@Composable
fun PreviewUI() {
    RandomJumpingSpectrum(Modifier.size(25.dp))
}