package com.example.newzz.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.R

class CustomDividerItemDecoration(
    private val context: Context,
    private val marginStart: Int,
    private val marginEnd: Int
) : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint().apply {
        color = ContextCompat.getColor(
            context,
            R.color.divider
        )
        strokeWidth =
            context.resources.getDimension(R.dimen.divider_height)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + marginStart
        val right = parent.width - parent.paddingRight - marginEnd

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + paint.strokeWidth

            c.drawLine(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}
