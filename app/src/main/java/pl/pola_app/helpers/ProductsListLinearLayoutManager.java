package pl.pola_app.helpers;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

import pl.pola_app.BuildConfig;
import timber.log.Timber;

public class ProductsListLinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {

    private static double HEIGHT_LIMIT = 0.45; //45% of the screen

    @SuppressWarnings("UnusedDeclaration")
    public ProductsListLinearLayoutManager(Context context) {
        super(context);
        setAutoMeasureEnabled(true);
    }

    @Override
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int heightSize = View.MeasureSpec.getSize(hSpec);
        heightSize = Math.min(heightSize, (int) (heightSize*HEIGHT_LIMIT));
        hSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.AT_MOST);
        super.setMeasuredDimension(childrenBounds, wSpec, hSpec);
    }
}
