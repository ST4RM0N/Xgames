package com.example.ui.canvas

import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.entity.GardenEdgeEntity
import com.example.data.entity.GardenNodeEntity
import com.example.ui.theme.BioAmber
import com.example.ui.theme.BioCyan
import com.example.ui.theme.BioCyanGlow
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.BioEmeraldGlow
import com.example.ui.theme.BioRose
import com.example.ui.theme.BioSurface
import com.example.ui.theme.BioSurfaceHigh
import com.example.ui.theme.BioViolet
import com.example.ui.theme.BioVioletGlow
import kotlin.math.hypot

@Composable
fun GardenCanvas(
    nodes: List<GardenNodeEntity>,
    edges: List<GardenEdgeEntity>,
    selectedNodeId: Long?,
    canvasState: GardenCanvasState,
    onNodeSelected: (GardenNodeEntity) -> Unit,
    onCanvasDoubleTapped: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse transition for glowing filaments and photon energy dots
    val infiniteTransition = rememberInfiniteTransition(label = "gardenPulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    val breathingGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingGlow"
    )

    // Pre-calculate node positions map
    val nodeMap = remember(nodes) { nodes.associateBy { it.id } }

    // Pre-configure Android native Paint for crisp, fast text labels
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 34f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            setShadowLayer(8f, 0f, 0f, android.graphics.Color.BLACK)
        }
    }

    val badgePaint = remember {
        Paint().apply {
            color = android.graphics.Color.parseColor("#00F0FF")
            textSize = 24f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(canvasState) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    canvasState.panBy(pan.x, pan.y)
                    canvasState.zoomBy(zoom, centroid, Offset(size.width.toFloat(), size.height.toFloat()))
                }
            }
            .pointerInput(nodes, canvasState) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        val worldPos = canvasState.toWorld(
                            tapOffset.x,
                            tapOffset.y,
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                        onCanvasDoubleTapped(worldPos)
                    },
                    onTap = { tapOffset ->
                        // Hit-test nodes
                        val canvasW = size.width.toFloat()
                        val canvasH = size.height.toFloat()
                        val hitNode = nodes.findLast { node ->
                            val screenPos = canvasState.toScreen(node.posX, node.posY, canvasW, canvasH)
                            val radiusPx = (getNodeRadius(node.level) + 16f) * canvasState.zoom
                            val dist = hypot(tapOffset.x - screenPos.x, tapOffset.y - screenPos.y)
                            dist <= radiusPx
                        }
                        if (hitNode != null) {
                            onNodeSelected(hitNode)
                        }
                    }
                )
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. Draw subtle ambient space background with glowing nebulae
        val centerScreen = Offset(canvasWidth / 2f, canvasHeight / 2f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BioEmerald.copy(alpha = 0.07f * breathingGlow),
                    BioViolet.copy(alpha = 0.04f),
                    Color.Transparent
                ),
                center = centerScreen,
                radius = canvasWidth * 0.9f
            )
        )

        // 2. Draw background neural constellation grid dots
        val gridStep = 120f * canvasState.zoom
        if (gridStep > 30f) {
            val offsetX = (canvasState.panX * canvasState.zoom) % gridStep
            val offsetY = (canvasState.panY * canvasState.zoom) % gridStep
            var x = offsetX
            while (x < canvasWidth) {
                var y = offsetY
                while (y < canvasHeight) {
                    drawCircle(
                        color = BioCyan.copy(alpha = 0.08f),
                        radius = 1.5f * canvasState.zoom.coerceIn(0.8f, 2.0f),
                        center = Offset(x, y)
                    )
                    y += gridStep
                }
                x += gridStep
            }
        }

        // 3. Draw Synaptic Filaments (Edges)
        edges.forEach { edge ->
            val sourceNode = nodeMap[edge.sourceNodeId]
            val targetNode = nodeMap[edge.targetNodeId]
            if (sourceNode != null && targetNode != null) {
                val p1 = canvasState.toScreen(sourceNode.posX, sourceNode.posY, canvasWidth, canvasHeight)
                val p2 = canvasState.toScreen(targetNode.posX, targetNode.posY, canvasWidth, canvasHeight)

                val isHighlighted = sourceNode.id == selectedNodeId || targetNode.id == selectedNodeId
                val strokeColor = when {
                    isHighlighted -> BioCyanGlow
                    edge.relationType == "AUTOMATES" -> BioEmerald
                    edge.relationType == "SPROUTS_FROM" -> BioViolet
                    else -> BioCyan.copy(alpha = 0.6f)
                }

                val strokeWidth = if (isHighlighted) 3.5f * canvasState.zoom else 1.8f * canvasState.zoom

                // Draw filament connection
                drawLine(
                    color = strokeColor.copy(alpha = if (isHighlighted) 0.85f else 0.45f),
                    start = p1,
                    end = p2,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )

                // Draw animated travelling photon energy dot
                val photonOffset = (pulseProgress + (edge.id % 5) * 0.2f) % 1f
                val photonX = p1.x + (p2.x - p1.x) * photonOffset
                val photonY = p1.y + (p2.y - p1.y) * photonOffset

                drawCircle(
                    color = if (isHighlighted) Color.White else BioCyanGlow,
                    radius = (3.5f * canvasState.zoom).coerceIn(2f, 7f),
                    center = Offset(photonX, photonY)
                )

                // Photon soft glow
                drawCircle(
                    color = strokeColor.copy(alpha = 0.4f),
                    radius = (8f * canvasState.zoom).coerceIn(4f, 16f),
                    center = Offset(photonX, photonY)
                )
            }
        }

        // 4. Draw Garden Nodes & Blooms
        nodes.forEach { node ->
            val screenPos = canvasState.toScreen(node.posX, node.posY, canvasWidth, canvasHeight)
            val isSelected = node.id == selectedNodeId
            val baseRadius = getNodeRadius(node.level) * canvasState.zoom

            val (primaryColor, glowColor) = getNodeColors(node.category, node.level)

            // Outer bioluminescent halo bloom
            val haloRadius = baseRadius * if (isSelected) 2.6f else (1.8f * breathingGlow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = if (isSelected) 0.45f else 0.25f),
                        primaryColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = screenPos,
                    radius = haloRadius
                ),
                radius = haloRadius,
                center = screenPos
            )

            // Outer orbital ring for Level 3+ or selected
            if (node.level >= 3 || isSelected) {
                drawCircle(
                    color = glowColor.copy(alpha = if (isSelected) 0.9f else 0.5f),
                    radius = baseRadius + (6f * canvasState.zoom),
                    center = screenPos,
                    style = Stroke(
                        width = 2f * canvasState.zoom,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), pulseProgress * 20f)
                    )
                )
            }

            // Node core background disc
            drawCircle(
                color = BioSurfaceHigh,
                radius = baseRadius,
                center = screenPos
            )

            // Node perimeter stroke
            drawCircle(
                color = if (isSelected) Color.White else primaryColor,
                radius = baseRadius,
                center = screenPos,
                style = Stroke(width = (if (isSelected) 3.5f else 2.2f) * canvasState.zoom)
            )

            // Inner radiant energy core
            val innerCoreRadius = baseRadius * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, primaryColor),
                    center = screenPos,
                    radius = innerCoreRadius
                ),
                radius = innerCoreRadius,
                center = screenPos
            )

            // Checkmark badge if node action/thought is completed
            if (node.isCompleted) {
                val badgePos = Offset(screenPos.x + baseRadius * 0.7f, screenPos.y - baseRadius * 0.7f)
                drawCircle(
                    color = BioEmerald,
                    radius = 8f * canvasState.zoom,
                    center = badgePos
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f * canvasState.zoom,
                    center = badgePos
                )
            }

            // Draw Node Text & XP Badges via native canvas
            drawContext.canvas.nativeCanvas.apply {
                val scaledTextSize = (14f * canvasState.zoom).coerceIn(18f, 36f)
                textPaint.textSize = scaledTextSize

                // Draw Title Label below node
                val labelY = screenPos.y + baseRadius + (18f * canvasState.zoom)
                val displayTitle = if (node.title.length > 22) node.title.take(20) + "…" else node.title
                drawText(displayTitle, screenPos.x, labelY, textPaint)

                // Draw Level / XP Badge
                val badgeText = "Lv.${node.level} • ${node.experience} XP"
                badgePaint.textSize = (scaledTextSize * 0.75f).coerceIn(14f, 26f)
                badgePaint.color = glowColor.toArgb()
                drawText(badgeText, screenPos.x, labelY + (scaledTextSize * 1.1f), badgePaint)
            }
        }
    }
}

private fun getNodeRadius(level: Int): Float {
    return when (level) {
        1 -> 24f
        2 -> 32f
        3 -> 40f
        4 -> 48f
        else -> 28f
    }
}

private fun getNodeColors(category: String, level: Int): Pair<Color, Color> {
    return when (category) {
        "TRIGGER" -> Pair(BioCyan, BioCyanGlow)
        "DECISION" -> Pair(BioAmber, Color(0xFFFBBF24))
        "VALIDATION" -> Pair(Color(0xFF34D399), Color(0xFF6EE7B7))
        "BOT_ACTION" -> Pair(BioEmerald, BioEmeraldGlow)
        else -> Pair(BioViolet, BioVioletGlow)
    }
}
