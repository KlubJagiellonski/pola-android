package pl.pola_app.ui.scanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import pl.pola_app.helpers.Utils;

/**
 * Created by Tajchert on 07.11.2015.
 * Class used to draw white rectangle over barcode reader
 */
public class ScannerBox extends View {
    Paint paint;
    Path path;

    final int boxWidth = Utils.dpToPx(230);
    final int boxHeight = Utils.dpToPx(150);
    final int verticalPadding = Utils.dpToPx(100);
    int horizontalStart;
    int verticalStart;
    public ScannerBox(Context context) {
        super(context);
        init(context);
    }

    public ScannerBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScannerBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScannerBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(Utils.dpToPx(1));
        paint.setStyle(Paint.Style.STROKE);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        horizontalStart = (metrics.widthPixels - boxWidth)/2;
        verticalStart = (metrics.heightPixels - boxHeight - verticalPadding)/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(horizontalStart, verticalStart, horizontalStart+boxWidth, verticalStart+boxHeight, paint);
    }

    public void setDefaultPosition(Context context) {
        setMovedPosition(context, 0);
    }

    public void setMovedPosition(Context context, int cardNumber) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        horizontalStart = (metrics.widthPixels - boxWidth)/2;
        int verticalCardPadding = cardNumber * Utils.dpToPx(25);
        verticalStart = (metrics.heightPixels - boxHeight - verticalPadding - verticalCardPadding)/2;
        invalidate();
    }
}
