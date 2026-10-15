package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.CelebrationCoral
import com.example.ui.theme.CelebrationGreen
import com.example.ui.theme.CelebrationPurple
import com.example.ui.theme.CelebrationSky
import com.example.ui.theme.PrimaryPink
import kotlin.random.Random

/**
 * Custom illustrated Balloon Icon with knot and subtle highlight string.
 */
@Composable
fun BalloonIcon(
  modifier: Modifier = Modifier,
  color: Color = AccentGold,
  size: Dp = 24.dp
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Balloon Oval
    drawOval(
      color = color,
      topLeft = Offset(w * 0.15f, h * 0.05f),
      size = Size(w * 0.7f, h * 0.72f)
    )

    // Balloon knot
    val knotPath = Path().apply {
      moveTo(w * 0.42f, h * 0.75f)
      lineTo(w * 0.58f, h * 0.75f)
      lineTo(w * 0.5f, h * 0.82f)
      close()
    }
    drawPath(knotPath, color)

    // Highlight sheen
    drawOval(
      color = Color.White.copy(alpha = 0.45f),
      topLeft = Offset(w * 0.28f, h * 0.18f),
      size = Size(w * 0.18f, h * 0.26f)
    )

    // String
    val stringPath = Path().apply {
      moveTo(w * 0.5f, h * 0.82f)
      quadraticTo(w * 0.42f, h * 0.90f, w * 0.52f, h * 0.98f)
    }
    drawPath(stringPath, color = Color.Gray.copy(alpha = 0.5f), style = Stroke(width = 2.dp.toPx()))
  }
}

/**
 * Custom illustrated Gift Box Icon with ribbon and bow.
 */
@Composable
fun GiftIcon(
  modifier: Modifier = Modifier,
  boxColor: Color = PrimaryPink,
  ribbonColor: Color = AccentGold,
  size: Dp = 24.dp
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Main Box
    drawRoundRect(
      color = boxColor,
      topLeft = Offset(w * 0.18f, h * 0.38f),
      size = Size(w * 0.64f, h * 0.52f),
      cornerRadius = CornerRadius(6.dp.toPx())
    )

    // Box Lid
    drawRoundRect(
      color = boxColor,
      topLeft = Offset(w * 0.12f, h * 0.28f),
      size = Size(w * 0.76f, h * 0.14f),
      cornerRadius = CornerRadius(4.dp.toPx())
    )

    // Vertical Ribbon
    drawRect(
      color = ribbonColor,
      topLeft = Offset(w * 0.44f, h * 0.28f),
      size = Size(w * 0.12f, h * 0.62f)
    )

    // Horizontal Ribbon on Lid
    drawRect(
      color = ribbonColor,
      topLeft = Offset(w * 0.12f, h * 0.33f),
      size = Size(w * 0.76f, h * 0.05f)
    )

    // Bow Loops
    val bowPath = Path().apply {
      // Left loop
      moveTo(w * 0.5f, h * 0.28f)
      cubicTo(w * 0.3f, h * 0.12f, w * 0.25f, h * 0.22f, w * 0.5f, h * 0.28f)
      // Right loop
      cubicTo(w * 0.7f, h * 0.12f, w * 0.75f, h * 0.22f, w * 0.5f, h * 0.28f)
    }
    drawPath(bowPath, color = ribbonColor, style = Stroke(width = 2.5.dp.toPx()))
  }
}

/**
 * Custom illustrated Cake Icon with layers, frosting and lit candle.
 */
@Composable
fun CakeIcon(
  modifier: Modifier = Modifier,
  cakeColor: Color = Color(0xFFFDE68A),
  frostingColor: Color = PrimaryPink,
  candleFlameColor: Color = AccentGold,
  size: Dp = 24.dp
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Cake base plate
    drawRoundRect(
      color = Color.LightGray.copy(alpha = 0.5f),
      topLeft = Offset(w * 0.08f, h * 0.88f),
      size = Size(w * 0.84f, h * 0.06f),
      cornerRadius = CornerRadius(4.dp.toPx())
    )

    // Cake body
    drawRoundRect(
      color = cakeColor,
      topLeft = Offset(w * 0.18f, h * 0.48f),
      size = Size(w * 0.64f, h * 0.40f),
      cornerRadius = CornerRadius(6.dp.toPx())
    )

    // Frosting top
    drawRoundRect(
      color = frostingColor,
      topLeft = Offset(w * 0.16f, h * 0.44f),
      size = Size(w * 0.68f, h * 0.15f),
      cornerRadius = CornerRadius(5.dp.toPx())
    )

    // Candle stem
    drawRect(
      color = Color(0xFF38BDF8),
      topLeft = Offset(w * 0.46f, h * 0.25f),
      size = Size(w * 0.08f, h * 0.20f)
    )

    // Flame
    val flamePath = Path().apply {
      moveTo(w * 0.5f, h * 0.10f)
      cubicTo(w * 0.58f, h * 0.18f, w * 0.55f, h * 0.25f, w * 0.5f, h * 0.25f)
      cubicTo(w * 0.45f, h * 0.25f, w * 0.42f, h * 0.18f, w * 0.5f, h * 0.10f)
      close()
    }
    drawPath(flamePath, candleFlameColor)
  }
}

private data class ConfettiParticle(
  val xRatio: Float,
  val initialYRatio: Float,
  val size: Float,
  val color: Color,
  val speed: Float,
  val rotation: Float,
  val isCircle: Boolean
)

/**
 * Animated cheerful floating confetti particles for celebrations.
 */
@Composable
fun ConfettiOverlay(
  modifier: Modifier = Modifier,
  particleCount: Int = 30
) {
  val confettiColors = listOf(
    AccentGold,
    PrimaryPink,
    CelebrationSky,
    CelebrationPurple,
    CelebrationGreen,
    CelebrationCoral
  )

  val particles = remember {
    val rng = Random(42)
    List(particleCount) {
      ConfettiParticle(
        xRatio = rng.nextFloat(),
        initialYRatio = rng.nextFloat(),
        size = rng.nextFloat() * 8f + 6f,
        color = confettiColors[rng.nextInt(confettiColors.size)],
        speed = rng.nextFloat() * 0.5f + 0.5f,
        rotation = rng.nextFloat() * 360f,
        isCircle = rng.nextBoolean()
      )
    }
  }

  val progress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 4000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      )
    )
  }

  Box(modifier = modifier) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      particles.forEach { p ->
        val currentY = ((p.initialYRatio + progress.value * p.speed) % 1.0f) * h
        val currentX = (p.xRatio * w) + kotlin.math.sin((progress.value * 6.28f) + p.rotation) * 20f

        if (p.isCircle) {
          drawCircle(
            color = p.color.copy(alpha = 0.85f),
            radius = p.size,
            center = Offset(currentX, currentY)
          )
        } else {
          drawRoundRect(
            color = p.color.copy(alpha = 0.85f),
            topLeft = Offset(currentX, currentY),
            size = Size(p.size * 1.5f, p.size * 0.9f),
            cornerRadius = CornerRadius(2.dp.toPx())
          )
        }
      }
    }
  }
}
