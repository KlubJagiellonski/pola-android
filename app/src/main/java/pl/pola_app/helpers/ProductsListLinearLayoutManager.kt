package pl.pola_app.helpers

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

class ProductsListLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    private val HEIGHT_LIMIT = 0.45 //45% of the screen
    override fun setMeasuredDimension(childrenBounds: Rect, wSpec: Int, hSpec: Int) {
        var hSpec = hSpec
        var heightSize = View.MeasureSpec.getSize(hSpec)
        heightSize = heightSize.coerceAtMost((heightSize * HEIGHT_LIMIT).toInt())
        hSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.AT_MOST)
        super.setMeasuredDimension(childrenBounds, wSpec, hSpec)
    }

    init {
        isAutoMeasureEnabled = true
    }
}