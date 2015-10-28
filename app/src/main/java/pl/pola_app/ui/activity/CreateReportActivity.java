package pl.pola_app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
