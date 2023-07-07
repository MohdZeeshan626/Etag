package com.max360group.cammax360.views.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class PixelGridView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var numColumns = 0f
    private var numRows = 0f
    private var cellWidth = 0f
    private var cellHeight = 0f
    private val blackPaint: Paint = Paint()
    private lateinit var cellChecked: Array<BooleanArray>
    fun setNumColumns(numColumns: Int) {
        this.numColumns = numColumns.toFloat()
        calculateDimensions()
    }

    fun getNumColumns(): Float {
        return numColumns
    }

    fun setNumRows(numRows: Int) {
        this.numRows = numRows.toFloat()
        calculateDimensions()
    }

    fun getNumRows(): Float {
        return numRows
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateDimensions()
    }

    private fun calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return
        }
        cellWidth = width / numColumns
        cellHeight = height / numRows
        cellChecked = Array(numColumns.toInt()) { BooleanArray(numRows.toInt()) }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
//        canvas.drawColor(Color.WHITE)
        if (numColumns == 0f || numRows == 0f) {
            return
        }
        val width = width
        val height = height
        for (i in 0 until numColumns.toInt()) {
            for (j in 0 until numRows.toInt()) {
                if (cellChecked[i][j]) {
                    canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        blackPaint)
                }
            }
        }
        for (i in 1 until numColumns.toInt()) {
            canvas.drawLine(i * cellWidth, 0f, i * cellWidth, height.toFloat(), blackPaint)
        }
        for (i in 1 until numRows.toInt()) {
            canvas.drawLine(0f, i * cellHeight, width.toFloat(), i * cellHeight, blackPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
          /*  val column = (event.x / cellWidth).toInt()
            val row = (event.y / cellHeight).toInt()
            cellChecked[column][row] = !cellChecked[column][row]
            invalidate()*/
        }
        return true
    }

    init {
        blackPaint.style = Paint.Style.FILL_AND_STROKE
    }
}