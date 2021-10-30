package pl.pola_app.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.databinding.ActivityCreateReportBinding;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import pl.pola_app.network.Api;
import pl.tajchert.nammu.Nammu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CreateReportActivity extends Activity implements Callback<ReportResult> {

    private String productId;
    private String code;
    private ProgressDialog progressDialog;
    private Call<ReportResult> reportResultCall;
    private SessionId sessionId;

    ActivityCreateReportBinding binding;
    EditText descriptionEditText;

    private EventLogger logger = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionId = SessionId.create(this);

        if (getIntent() != null) {
            productId = getIntent().getStringExtra("productId");
            code = getIntent().getStringExtra("code");
        }
        Nammu.init(this);

        if (logger == null) {
            logger = new EventLogger(this);
        }
        logger.logLevelStart("report", code, sessionId.get());
        binding.sendButton.setOnClickListener(this::clickSendButton);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        super.onPause();
    }

    public void clickSendButton(View view) {
        String description = descriptionEditText.getText().toString();
        sendReport(description, productId);
    }

    private void sendReport(String description, String productId) {
        //get ext from path
        Report report;
        if (productId != null) {
            report = new Report(description, productId);
        } else {
            report = new Report(description);
        }
        Api api = PolaApplication.retrofit.create(Api.class);
        reportResultCall = api.createReport(sessionId.get(), report);
        reportResultCall.enqueue(this);

        progressDialog = ProgressDialog.show(CreateReportActivity.this, "", getString(R.string.sending_image_dialog), true);
        logger.logLevelEnd("report", code, sessionId.get());
    }

    @Override
    public void onResponse(Call<ReportResult> call, Response<ReportResult> response) {
        showEndResult(response.isSuccessful());
    }

    private void showEndResult(boolean isSuccess) {
        String toastMessage = getString(R.string.toast_send_raport);
        if (!isSuccess) {
            toastMessage = getString(R.string.toast_send_raport_error);
        }
        Toast.makeText(CreateReportActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        if (isSuccess) {
            CreateReportActivity.this.finish();
        }
    }

    @Override
    public void onFailure(Call<ReportResult> call, Throwable t) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        Timber.e(t, "Problem with photo report sending - this throwable cached and it is not fatal but app works wrong.");
        Toast.makeText(CreateReportActivity.this, getString(R.string.toast_send_raport_error), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        if (reportResultCall != null) {
            reportResultCall.cancel();
        }
        super.onDestroy();
    }
}
