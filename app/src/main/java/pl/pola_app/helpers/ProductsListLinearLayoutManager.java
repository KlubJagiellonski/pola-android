package pl.pola_app.helpers;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ProductsListLinearLayoutManager extends LinearLayoutManager {

    private static final double HEIGHT_LIMIT = 0.45; //45% of the screen

    public ProductsListLinearLayoutManager(Context context) {
        super(context);
        setAutoMeasureEnabled(true);
    }

    @Override
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int heightSize = View.MeasureSpec.getSize(hSpec);
        heightSize = Math.min(heightSize, (int) (heightSize * HEIGHT_LIMIT));
        hSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.AT_MOST);
        super.setMeasuredDimension(childrenBounds, wSpec, hSpec);
    }
}
