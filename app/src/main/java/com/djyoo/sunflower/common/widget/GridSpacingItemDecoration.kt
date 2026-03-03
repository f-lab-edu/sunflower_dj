package com.djyoo.sunflower.common.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * GridLayoutManager 에서 일정 간격을 유지하기 위한 ItemDecoration.
 */
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingDp: Int,
    private val includeBottom: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val spacing = spacingDp.dpToPx(view)
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        // 좌우 균등 분배
        outRect.left = spacing - column * spacing / spanCount
        outRect.right = (column + 1) * spacing / spanCount

        outRect.top = spacing

        // 아래 옵션
        if (includeBottom) {
            outRect.bottom = spacing
        } else {
            outRect.bottom = 0
        }
    }
}

private fun Int.dpToPx(view: View): Int {
    return (this * view.resources.displayMetrics.density).roundToInt()
}

