package com.example.accountbook.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class DraggableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var downX = 0f
    private var downY = 0f
    private var downTranslationX = 0f
    private var downTranslationY = 0f
    private var isDragging = false
    private val touchSlop = 10f

    @SuppressLint("ClickableViewAccessibility")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                downTranslationX = translationX
                downTranslationY = translationY
                isDragging = false
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(ev.rawX - downX)
                val dy = Math.abs(ev.rawY - downY)
                if (dx > touchSlop || dy > touchSlop) {
                    isDragging = true
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - downX
                val dy = event.rawY - downY
                translationX = downTranslationX + dx
                translationY = downTranslationY + dy
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun isDragging(): Boolean = isDragging
}