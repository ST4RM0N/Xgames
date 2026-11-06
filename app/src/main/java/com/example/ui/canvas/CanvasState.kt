package com.example.ui.canvas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class GardenCanvasState(
    initialPanX: Float = 0f,
    initialPanY: Float = 0f,
    initialZoom: Float = 1.0f
) {
    var panX by mutableFloatStateOf(initialPanX)
    var panY by mutableFloatStateOf(initialPanY)
    var zoom by mutableFloatStateOf(initialZoom)

    fun panBy(dx: Float, dy: Float) {
        panX += dx / zoom
        panY += dy / zoom
    }

    fun zoomBy(zoomFactor: Float, focalPoint: Offset, canvasSize: Offset) {
        val oldZoom = zoom
        val newZoom = (oldZoom * zoomFactor).coerceIn(0.35f, 2.8f)
        if (newZoom != oldZoom) {
            zoom = newZoom
        }
    }

    fun reset() {
        panX = 0f
        panY = 0f
        zoom = 1.0f
    }

    fun focusOn(worldX: Float, worldY: Float) {
        panX = -worldX
        panY = -worldY
        zoom = 1.15f
    }

    fun toScreen(worldX: Float, worldY: Float, canvasWidth: Float, canvasHeight: Float): Offset {
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        val sx = centerX + (worldX + panX) * zoom
        val sy = centerY + (worldY + panY) * zoom
        return Offset(sx, sy)
    }

    fun toWorld(screenX: Float, screenY: Float, canvasWidth: Float, canvasHeight: Float): Offset {
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        val wx = (screenX - centerX) / zoom - panX
        val wy = (screenY - centerY) / zoom - panY
        return Offset(wx, wy)
    }
}
