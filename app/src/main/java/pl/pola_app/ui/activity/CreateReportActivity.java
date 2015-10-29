package pl.pola_app.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.BuildConfig;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import pl.pola_app.network.Api;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CreateReportActivity extends Activity implements Callback<ReportResult> {
    private static final String TAG = CreateReportActivity.class.getSimpleName();
    private String productId;

    @Bind(R.id.descripton_editText)
    EditText descriptionEditText;
    @Bind(R.id.linearImageViews)
    LinearLayout linearImageViews;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        ButterKnife.bind(this);

        if("product_report".equals(getIntent().getAction())) {
            productId = getIntent().getStringExtra("productId");
        }
        if(productId == null) {
            //This shouldn't happen at all
            this.finish();
        }
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.pola_ic_app_512));
        setImageView(bitmaps);

        if(BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logLevelStart(new LevelStartEvent()
                                .putLevelName("Report")
                                .putCustomAttribute("Code", productId)
                                .putCustomAttribute("DeviceId", Utils.getDeviceId(this))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setImageView(ArrayList<Bitmap> bitmapsToSet) {
        int margin = Utils.dpToPx(4);
        if(bitmapsToSet != null && bitmapsToSet.size() > 0) {
            for (Bitmap bitmap : bitmapsToSet) {
                ImageView imageView = new ImageView(this);
                imageView.setPadding(0, 0, margin, 0);
                int width = bitmap.getWidth() * Utils.dpToPx(80)/bitmap.getHeight();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, Utils.dpToPx(80));
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                linearImageViews.addView(imageView);
            }
        }
        //Add add button
        ImageView imageView = new ImageView(this);
        imageView.setPadding(0,0,margin,0);
        //imageView.setMaxHeight(Utils.dpToPx(80));
        //imageView.setMinimumHeight(Utils.dpToPx(80));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utils.dpToPx(80), Utils.dpToPx(80));
        imageView.setLayoutParams(layoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp));
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        linearImageViews.addView(imageView);
    }

    @OnClick(R.id.send_button)
    public void clickSendButton() {
        String description = descriptionEditText.getText().toString();
        if (productId != null) {
            if(description.length() <= 0) {
                descriptionEditText.setError(getString(R.string.description_empty));
                return;
            }
            sendReport(description);
        }
    }

    private void sendReport(String description) {
        Report report = new Report(description);
        Api api = PolaApplication.retrofit.create(Api.class);
        Call<ReportResult> reportResultCall = api.createReport(Utils.getDeviceId(this), productId, report);
        reportResultCall.enqueue(this);
        if(BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logLevelEnd(new LevelEndEvent()
                                .putLevelName("Report")
                                .putCustomAttribute("Code", productId + "")
                                .putCustomAttribute("DeviceId", Utils.getDeviceId(CreateReportActivity.this))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(Response<ReportResult> response, Retrofit retrofit) {
        Log.d(TAG, "onResponse: ");
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: ");
    }
}
