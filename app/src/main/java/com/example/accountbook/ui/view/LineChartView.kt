package com.example.accountbook.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFA000")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFA000")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#999999")
        textSize = 28f
    }

    private val linePath = Path()
    private var dataPoints = listOf<Float>()
    private var labels = listOf<String>()

    fun setData(points: List<Float>, labelList: List<String>) {
        dataPoints = points
        labels = labelList
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        val padding = 40f
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        val maxValue = dataPoints.maxOrNull() ?: 1f
        val minValue = 0f
        val range = maxValue - minValue

        linePath.reset()

        val points = mutableListOf<Pair<Float, Float>>()

        dataPoints.forEachIndexed { index, value ->
            val x = padding + (index.toFloat() / (dataPoints.size - 1).coerceAtLeast(1)) * chartWidth
            val normalizedValue = if (range > 0) (value - minValue) / range else 0f
            val y = padding + chartHeight * (1 - normalizedValue)
            points.add(Pair(x, y))
        }

        if (points.isNotEmpty()) {
            linePath.moveTo(points[0].first, points[0].second)

            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val controlX1 = (prev.first + curr.first) / 2
                linePath.cubicTo(controlX1, prev.second, controlX1, curr.second, curr.first, curr.second)
            }
        }

        canvas.drawPath(linePath, linePaint)

        points.forEach { (x, y) ->
            canvas.drawCircle(x, y, 6f, dotPaint)
        }

        if (labels.isNotEmpty()) {
            val step = if (labels.size > 6) labels.size / 6 else 1
            labels.forEachIndexed { index, label ->
                if (index % step == 0 || index == labels.size - 1) {
                    val x = padding + (index.toFloat() / (labels.size - 1).coerceAtLeast(1)) * chartWidth
                    canvas.drawText(label, x - 15, height - 5f, textPaint)
                }
            }
        }
    }
}