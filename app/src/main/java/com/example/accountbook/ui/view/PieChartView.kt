package com.example.accountbook.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    private val colors = intArrayOf(
        Color.parseColor("#FFA000"),
        Color.parseColor("#FF7043"),
        Color.parseColor("#42A5F5"),
        Color.parseColor("#66BB6A"),
        Color.parseColor("#AB47BC"),
        Color.parseColor("#EF5350"),
        Color.parseColor("#26C6DA"),
        Color.parseColor("#FFCA28")
    )

    private var data = listOf<Pair<String, Float>>()

    fun setData(newData: List<Pair<String, Float>>) {
        data = newData
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - 20f
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val total = data.sumOf { it.second.toDouble() }.toFloat()
        if (total <= 0) return

        var startAngle = -90f

        data.forEachIndexed { index, (_, value) ->
            val sweepAngle = (value / total) * 360f
            paint.color = colors[index % colors.size]
            paint.style = Paint.Style.FILL
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

            val midAngle = startAngle + sweepAngle / 2
            val midRadius = radius * 0.6f
            val textX = centerX + midRadius * Math.cos(Math.toRadians(midAngle.toDouble())).toFloat()
            val textY = centerY + midRadius * Math.sin(Math.toRadians(midAngle.toDouble())).toFloat()

            if (sweepAngle > 20) {
                canvas.drawText("${(value / total * 100).toInt()}%", textX, textY + 10, textPaint)
            }

            startAngle += sweepAngle
        }
    }
}