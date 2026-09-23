package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.CrystalSkin
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CrystalBallView(
    skin: CrystalSkin,
    animationId: String,
    isDivining: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    onRubbed: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var touchAngleOffset by remember { mutableFloatStateOf(0f) }

    // Infinite transitions for ambient magical swirling & breathing
    val infiniteTransition = rememberInfiniteTransition(label = "crystal_magic")

    // Slow ambient rotation
    val baseRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isDivining) 2000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    // Pulsing inner radiance
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isDivining) 450 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Glowing aura alpha
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (isDivining) 0.85f else 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isDivining) 350 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // Helper for subtle haptics
    fun triggerSubtleHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("crystal_ball_canvas")
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        triggerSubtleHaptic()
                        onRubbed?.invoke()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    touchAngleOffset += (dragAmount.x - dragAmount.y) * 0.4f
                    triggerSubtleHaptic()
                    onRubbed?.invoke()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val centerX = canvasW / 2f
            // Place orb slightly above center to accommodate the ornate stand below
            val centerY = canvasH * 0.44f
            val orbRadius = canvasW * 0.36f

            // 1. Draw Ornate Mystical Pedestal
            drawPedestal(
                centerX = centerX,
                centerY = centerY + orbRadius * 0.85f,
                width = orbRadius * 1.6f,
                height = orbRadius * 0.65f,
                isPremium = skin.isPremiumOnly
            )

            // 2. Outer Ethereal Radial Glow
            val glowRadius = orbRadius * 1.45f * pulseScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        skin.glowColor.copy(alpha = auraAlpha),
                        skin.glowColor.copy(alpha = auraAlpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = Offset(centerX, centerY)
            )

            // 3. Main Crystal Sphere Body
            val sphereBrush = Brush.radialGradient(
                colors = listOf(
                    skin.accentColor.copy(alpha = 0.9f),
                    skin.secondaryColor.copy(alpha = 0.95f),
                    skin.primaryColor,
                    Color(0xFF070814)
                ),
                center = Offset(centerX - orbRadius * 0.3f, centerY - orbRadius * 0.35f),
                radius = orbRadius * 1.25f
            )
            drawCircle(
                brush = sphereBrush,
                radius = orbRadius,
                center = Offset(centerX, centerY)
            )

            // 4. Rotating Sacred Equator Rune Rings
            val currentRotation = baseRotation + touchAngleOffset
            rotate(currentRotation, pivot = Offset(centerX, centerY)) {
                drawRuneRing(
                    centerX = centerX,
                    centerY = centerY,
                    radius = orbRadius * 0.88f,
                    runeColor = skin.runeColor.copy(alpha = if (isDivining) 0.9f else 0.5f),
                    isDivining = isDivining
                )
            }

            // 5. Specific Animation Auras (sparks, galaxies, lightning, mist)
            drawAnimationEffect(
                animationId = animationId,
                centerX = centerX,
                centerY = centerY,
                radius = orbRadius,
                rotation = currentRotation,
                skin = skin,
                isDivining = isDivining
            )

            // 6. Swirling Core Nebula / Stardust Swirl
            rotate(-currentRotation * 1.4f, pivot = Offset(centerX, centerY)) {
                drawNebulaSwirl(
                    centerX = centerX,
                    centerY = centerY,
                    radius = orbRadius * 0.72f,
                    accentColor = skin.accentColor,
                    glowColor = skin.glowColor,
                    isDivining = isDivining
                )
            }

            // 7. Glass Sphere Curvature Border & Inner Depth
            drawCircle(
                color = skin.accentColor.copy(alpha = 0.25f),
                radius = orbRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 3.dp.toPx())
            )

            // 8. 3D Specular Highlight Arc (Crescent glass reflection)
            drawSpecularHighlight(
                centerX = centerX,
                centerY = centerY,
                radius = orbRadius
            )
        }
    }
}

private fun DrawScope.drawPedestal(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    isPremium: Boolean
) {
    val goldTop = if (isPremium) Color(0xFFFFF099) else Color(0xFFFFD54F)
    val goldMid = if (isPremium) Color(0xFFF59E0B) else Color(0xFFFFA000)
    val goldDark = if (isPremium) Color(0xFF78350F) else Color(0xFF4E2C05)

    // Base Tier
    val baseRect = Path().apply {
        moveTo(centerX - width * 0.5f, centerY + height)
        lineTo(centerX + width * 0.5f, centerY + height)
        lineTo(centerX + width * 0.38f, centerY + height * 0.4f)
        lineTo(centerX - width * 0.38f, centerY + height * 0.4f)
        close()
    }
    drawPath(
        path = baseRect,
        brush = Brush.linearGradient(
            colors = listOf(goldDark, goldMid, goldTop, goldDark),
            start = Offset(centerX - width * 0.5f, centerY),
            end = Offset(centerX + width * 0.5f, centerY + height)
        )
    )

    // Cup Stand Claw
    val cupPath = Path().apply {
        moveTo(centerX - width * 0.35f, centerY + height * 0.4f)
        quadraticTo(
            centerX - width * 0.42f, centerY - height * 0.1f,
            centerX - width * 0.28f, centerY - height * 0.4f
        )
        lineTo(centerX + width * 0.28f, centerY - height * 0.4f)
        quadraticTo(
            centerX + width * 0.42f, centerY - height * 0.1f,
            centerX + width * 0.35f, centerY + height * 0.4f
        )
        close()
    }
    drawPath(
        path = cupPath,
        brush = Brush.verticalGradient(
            colors = listOf(goldTop, goldMid, goldDark),
            startY = centerY - height * 0.4f,
            endY = centerY + height * 0.4f
        )
    )

    // Ornate center gem in the pedestal
    val gemColor = if (isPremium) Color(0xFF00E5FF) else Color(0xFFE11D48)
    drawCircle(
        color = gemColor,
        radius = width * 0.07f,
        center = Offset(centerX, centerY + height * 0.35f)
    )
}

private fun DrawScope.drawRuneRing(
    centerX: Float,
    centerY: Float,
    radius: Float,
    runeColor: Color,
    isDivining: Boolean
) {
    val runeCount = 8
    val angleStep = 360f / runeCount
    for (i in 0 until runeCount) {
        val angleRad = Math.toRadians((i * angleStep).toDouble())
        val rx = centerX + (radius * cos(angleRad)).toFloat()
        val ry = centerY + (radius * sin(angleRad) * 0.35f).toFloat() // Oval equator projection

        // Draw small celestial glyphs/stars
        drawCircle(
            color = runeColor,
            radius = if (isDivining) 4.5f else 3f,
            center = Offset(rx, ry)
        )
        // Cross lines for rune sparkle
        drawLine(
            color = runeColor,
            start = Offset(rx - 7f, ry),
            end = Offset(rx + 7f, ry),
            strokeWidth = 1.5f
        )
        drawLine(
            color = runeColor,
            start = Offset(rx, ry - 7f),
            end = Offset(rx, ry + 7f),
            strokeWidth = 1.5f
        )
    }
}

private fun DrawScope.drawNebulaSwirl(
    centerX: Float,
    centerY: Float,
    radius: Float,
    accentColor: Color,
    glowColor: Color,
    isDivining: Boolean
) {
    val strokeW = if (isDivining) 6.dp.toPx() else 3.5.dp.toPx()
    val swirlPath = Path().apply {
        moveTo(centerX - radius * 0.5f, centerY - radius * 0.2f)
        cubicTo(
            centerX - radius * 0.1f, centerY - radius * 0.6f,
            centerX + radius * 0.5f, centerY - radius * 0.1f,
            centerX + radius * 0.3f, centerY + radius * 0.4f
        )
        cubicTo(
            centerX + radius * 0.1f, centerY + radius * 0.6f,
            centerX - radius * 0.4f, centerY + radius * 0.3f,
            centerX, centerY
        )
    }
    drawPath(
        path = swirlPath,
        brush = Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = if (isDivining) 0.85f else 0.45f),
                glowColor.copy(alpha = if (isDivining) 0.9f else 0.5f),
                Color.Transparent
            )
        ),
        style = Stroke(width = strokeW, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawAnimationEffect(
    animationId: String,
    centerX: Float,
    centerY: Float,
    radius: Float,
    rotation: Float,
    skin: CrystalSkin,
    isDivining: Boolean
) {
    when (animationId) {
        "lightning" -> {
            // Crackling electric arcs across orb
            val lightningPath = Path().apply {
                moveTo(centerX - radius * 0.6f, centerY - radius * 0.3f)
                lineTo(centerX - radius * 0.2f, centerY - radius * 0.1f)
                lineTo(centerX - radius * 0.3f, centerY + radius * 0.1f)
                lineTo(centerX + radius * 0.2f, centerY + radius * 0.2f)
                lineTo(centerX + radius * 0.5f, centerY + radius * 0.4f)
            }
            drawPath(
                path = lightningPath,
                color = Color.White.copy(alpha = if (isDivining) 0.95f else 0.65f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        "sparks" -> {
            // Micro star clusters
            val sparkPositions = listOf(
                Offset(centerX - radius * 0.4f, centerY - radius * 0.4f),
                Offset(centerX + radius * 0.35f, centerY - radius * 0.25f),
                Offset(centerX - radius * 0.25f, centerY + radius * 0.35f),
                Offset(centerX + radius * 0.45f, centerY + radius * 0.3f),
                Offset(centerX, centerY - radius * 0.1f)
            )
            for (pos in sparkPositions) {
                drawCircle(
                    color = Color.White,
                    radius = 3.5f,
                    center = pos
                )
                drawCircle(
                    color = skin.accentColor.copy(alpha = 0.5f),
                    radius = 8f,
                    center = pos
                )
            }
        }
        "galaxies" -> {
            // Dual galactic spiral arms
            val armPath = Path().apply {
                moveTo(centerX - radius * 0.6f, centerY)
                cubicTo(
                    centerX - radius * 0.3f, centerY - radius * 0.5f,
                    centerX + radius * 0.3f, centerY + radius * 0.5f,
                    centerX + radius * 0.6f, centerY
                )
            }
            drawPath(
                path = armPath,
                color = skin.accentColor.copy(alpha = 0.55f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        "runes" -> {
            // Outer rotating glyph points
            rotate(rotation * 0.8f, pivot = Offset(centerX, centerY)) {
                for (angle in listOf(45f, 135f, 225f, 315f)) {
                    val rad = Math.toRadians(angle.toDouble())
                    val x = centerX + (radius * 0.7f * cos(rad)).toFloat()
                    val y = centerY + (radius * 0.7f * sin(rad)).toFloat()
                    drawCircle(color = skin.runeColor, radius = 4f, center = Offset(x, y))
                }
            }
        }
        else -> {
            // "mist" default: soft smoke waves
            val mistOval = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        centerX - radius * 0.5f,
                        centerY - radius * 0.25f,
                        centerX + radius * 0.5f,
                        centerY + radius * 0.25f
                    )
                )
            }
            drawPath(
                path = mistOval,
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(centerX, centerY),
                    radius = radius * 0.5f
                )
            )
        }
    }
}

private fun DrawScope.drawSpecularHighlight(
    centerX: Float,
    centerY: Float,
    radius: Float
) {
    // Top-left curved glassy reflection
    val highlightPath = Path().apply {
        val startX = centerX - radius * 0.65f
        val startY = centerY - radius * 0.65f
        moveTo(startX, startY)
        quadraticTo(
            centerX - radius * 0.15f, centerY - radius * 0.78f,
            centerX + radius * 0.25f, centerY - radius * 0.55f
        )
        quadraticTo(
            centerX - radius * 0.15f, centerY - radius * 0.62f,
            startX, startY
        )
        close()
    }
    drawPath(
        path = highlightPath,
        color = Color.White.copy(alpha = 0.45f)
    )

    // Small pinpoint glint
    drawCircle(
        color = Color.White.copy(alpha = 0.65f),
        radius = radius * 0.045f,
        center = Offset(centerX - radius * 0.42f, centerY - radius * 0.45f)
    )
}
