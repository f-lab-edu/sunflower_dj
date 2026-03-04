package com.djyoo.sunflower.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.withClip
import com.djyoo.sunflower.R

class CustomRoundLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    enum class CornerType {
        ALL, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
    }

    private var canvasRounder: CanvasRounder

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CustomRoundLayout, 0, 0)
        val commonRadius = array.getDimension(R.styleable.CustomRoundLayout_corner_radius, 0f)

        val corners = CornersHolder(
            topLeft = array.getDimension(R.styleable.CustomRoundLayout_top_left_corner_radius, commonRadius),
            topRight = array.getDimension(R.styleable.CustomRoundLayout_top_right_corner_radius, commonRadius),
            bottomRight = array.getDimension(R.styleable.CustomRoundLayout_bottom_right_corner_radius, commonRadius),
            bottomLeft = array.getDimension(R.styleable.CustomRoundLayout_bottom_left_corner_radius, commonRadius)
        )
        array.recycle()

        canvasRounder = CanvasRounder(corners)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = RoundOutlineProvider(corners)
            clipToOutline = true
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasRounder.updateSize(w, h)
    }

    override fun draw(canvas: Canvas) {
        canvasRounder.round(canvas) {
            super.draw(it)
        }
    }

    override fun dispatchDraw(canvas: Canvas) = super.dispatchDraw(canvas)

    fun setCornerRadius(radius: Float, type: CornerType = CornerType.ALL) {
        when (type) {
            CornerType.ALL -> canvasRounder.cornerRadius = radius
            CornerType.TOP_LEFT -> canvasRounder.topLeft = radius
            CornerType.TOP_RIGHT -> canvasRounder.topRight = radius
            CornerType.BOTTOM_RIGHT -> canvasRounder.bottomRight = radius
            CornerType.BOTTOM_LEFT -> canvasRounder.bottomLeft = radius
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = RoundOutlineProvider(canvasRounder.cornersHolder)
        }
        invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    internal class RoundOutlineProvider(private val corners: CornersHolder) : ViewOutlineProvider() {
        constructor(radius: Float) : this(CornersHolder(radius, radius, radius, radius))

        override fun getOutline(view: View, outline: Outline) {
            val rectF = RectF(0f, 0f, view.measuredWidth.toFloat(), view.measuredHeight.toFloat())
            val path = Path().apply {
                addRoundRect(
                    rectF,
                    floatArrayOf(
                        corners.topLeft, corners.topLeft,
                        corners.topRight, corners.topRight,
                        corners.bottomRight, corners.bottomRight,
                        corners.bottomLeft, corners.bottomLeft
                    ),
                    Path.Direction.CW
                )
                close()
            }
            if (path.isConvex) {
                outline.setConvexPath(path)
            }
        }
    }

    internal class CanvasRounder(val cornersHolder: CornersHolder) {
        private val path = Path()
        private var rectF = RectF(0f, 0f, 0f, 0f)

        var topLeft: Float
            get() = cornersHolder.topLeft
            set(value) {
                cornersHolder.topLeft = value
                resetPath()
            }

        var topRight: Float
            get() = cornersHolder.topRight
            set(value) {
                cornersHolder.topRight = value
                resetPath()
            }

        var bottomRight: Float
            get() = cornersHolder.bottomRight
            set(value) {
                cornersHolder.bottomRight = value
                resetPath()
            }

        var bottomLeft: Float
            get() = cornersHolder.bottomLeft
            set(value) {
                cornersHolder.bottomLeft = value
                resetPath()
            }

        var cornerRadius: Float
            get() = if (topLeft == topRight && topRight == bottomRight && bottomRight == bottomLeft) topLeft else Float.NaN
            set(value) {
                cornersHolder.topLeft = value
                cornersHolder.topRight = value
                cornersHolder.bottomRight = value
                cornersHolder.bottomLeft = value
                resetPath()
            }

        fun round(canvas: Canvas, drawFunction: (Canvas) -> Unit) {
            canvas.withClip(path) {
                drawFunction(canvas)
            }
        }

        fun updateSize(width: Int, height: Int) {
            rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            resetPath()
        }

        private fun resetPath() {
            path.reset()
            path.addRoundRect(
                rectF,
                floatArrayOf(
                    topLeft, topLeft,
                    topRight, topRight,
                    bottomRight, bottomRight,
                    bottomLeft, bottomLeft
                ),
                Path.Direction.CW
            )
            path.close()
        }
    }

    internal data class CornersHolder(
        var topLeft: Float,
        var topRight: Float,
        var bottomRight: Float,
        var bottomLeft: Float
    )
}