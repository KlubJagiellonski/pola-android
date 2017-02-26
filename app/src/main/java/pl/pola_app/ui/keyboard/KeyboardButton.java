package pl.pola_app.ui.keyboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Button view with an animated circular background.
 *
 * @see NumericKeyboard
 */
public class KeyboardButton extends TextView {
    private static final int DURATION = 200;
    private static final int TRANSPARENT = 0;
    private static final int OPAQUE = 255;
    private final Paint backgroundPaint = new Paint();
    private final ValueAnimator alphaAnimator;
    private float width;
    private float height;
    private int alpha;

    public KeyboardButton(Context context) {
        this(context, null, 0);
    }

    public KeyboardButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backgroundPaint.setAlpha(TRANSPARENT);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
        alphaAnimator = ValueAnimator.ofInt(OPAQUE, TRANSPARENT);
        alphaAnimator.setDuration(DURATION);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                alpha = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            animateBackground();
        }
        return super.onTouchEvent(event);
    }

    private void animateBackground() {
        alphaAnimator.start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        backgroundPaint.setAlpha(alpha);
        canvas.drawCircle(width / 2, height / 2, width / 2, backgroundPaint);
        super.onDraw(canvas);
    }
}
