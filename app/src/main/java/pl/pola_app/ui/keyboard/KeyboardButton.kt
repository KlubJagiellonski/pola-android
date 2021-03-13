package pl.pola_app.ui.keyboard

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

/**
 * Button view with an animated circular background.
 *
 * @see NumericKeyboard
 */
class KeyboardButton @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private val backgroundPaint = Paint()
    private val alphaAnimator: ValueAnimator
    private var width = 0f
    private var height = 0f
    private var alpha = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            animateBackground()
        }
        return super.onTouchEvent(event)
    }

    private fun animateBackground() {
        alphaAnimator.start()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        backgroundPaint.alpha = alpha
        canvas.drawCircle(width / 2, height / 2, width / 2, backgroundPaint)
        super.onDraw(canvas)
    }

    companion object {
        private const val DURATION = 200
        private const val TRANSPARENT = 0
        private const val OPAQUE = 255
    }

    init {
        backgroundPaint.alpha = TRANSPARENT
        backgroundPaint.color = Color.LTGRAY
        backgroundPaint.style = Paint.Style.FILL
        alphaAnimator = ValueAnimator.ofInt(OPAQUE, TRANSPARENT)
        alphaAnimator.duration = DURATION.toLong()
        alphaAnimator.addUpdateListener { valueAnimator ->
            alpha = valueAnimator.animatedValue as Int
            invalidate()
        }
    }
}