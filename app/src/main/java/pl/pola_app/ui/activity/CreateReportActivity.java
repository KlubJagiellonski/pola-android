package pl.pola_app.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.databinding.ActivityCreateReportBinding;
import pl.pola_app.helpers.EventLogger;
import pl.pola_app.helpers.SessionId;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import pl.pola_app.network.Api;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CreateReportActivity extends Activity implements Callback<ReportResult> {

    private static final int MAX_IMAGE_COUNT = 2;

    private static final String MIME_TYPE = "image/jpg";
    private static final String FILE_EXT = "jpg"; //EasyImage captures jpegs
    private int photoMarginDp = 6;
    private int numberOfImages;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<String> bitmapsPaths = new ArrayList<>();//As we save file, it would be good to delete them after we

    private String productId;
    private String code;
    private ProgressDialog progressDialog;
    private Call<ReportResult> reportResultCall;
    private SessionId sessionId;

    ActivityCreateReportBinding binding;

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
        setImageView(bitmaps);

        if (logger == null) {
            logger = new EventLogger(this);
        }
        logger.logLevelStart("report", code, sessionId.get());
        binding.sendButton.setOnClickListener(this::clickSendButton);
    }

    private void setImageView(final ArrayList<Bitmap> bitmapsToSet) {
        int margin = Utils.dpToPx(photoMarginDp);
        binding.linearImageViews.removeAllViews();

        boolean showAddButton = true;
        if (bitmapsToSet != null && bitmapsToSet.size() > 0) {
            int i = 0;
            for (final Bitmap bitmap : bitmapsToSet) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.rightMargin = i == MAX_IMAGE_COUNT ? 0 : margin;
                layoutParams.weight = 1f;
                imageView.setLayoutParams(layoutParams);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogDeletePhoto(bitmapsToSet.indexOf(bitmap));
                    }
                });
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                binding.linearImageViews.addView(imageView);
                i++;
            }
            showAddButton = bitmapsToSet.size() <= MAX_IMAGE_COUNT;
        }
        if (showAddButton) {
            //Add add button
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1f;
            imageView.setLayoutParams(layoutParams);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchCamera();
                }
            });
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.linearImageViews.addView(imageView);
        }
    }

    private void showDialogDeletePhoto(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (bitmaps != null && position < bitmaps.size()) {
                            bitmaps.remove(position);
                            setImageView(bitmaps);
                        }
                        if (bitmapsPaths != null && position < bitmapsPaths.size()) {
                            bitmapsPaths.remove(position);
                        }
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateReportActivity.this);
        builder.setMessage(getString(R.string.dialog_delete_photo))
                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener)
                .show();
    }

    private void launchCamera() {
        String permissions[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        Nammu.askForPermission(CreateReportActivity.this, permissions, permissionCallback);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        super.onPause();
    }

    public void clickSendButton(View view) {
        CharSequence description = binding.descriptonEditText.getText();
        if (description != null) {
            sendReport(description.toString(), productId);
        }
    }

    private void sendReport(String description, String productId) {
        if (productId == null && (bitmapsPaths == null || bitmapsPaths.size() == 0)) {
            Toast.makeText(CreateReportActivity.this, getString(R.string.toast_raport_error_no_pic), Toast.LENGTH_LONG).show();
            return;
        } else if (description == null) {
            description = "";
        }
        numberOfImages = bitmapsPaths.size();

        //get ext from path
        Report report;
        if (productId != null) {
            report = new Report(description, productId, numberOfImages, MIME_TYPE, FILE_EXT);
        } else {
            report = new Report(description, numberOfImages, MIME_TYPE, FILE_EXT);
        }
        Api api = PolaApplication.retrofit.create(Api.class);
        reportResultCall = api.createReport(sessionId.get(), report);
        reportResultCall.enqueue(this);

        progressDialog = ProgressDialog.show(CreateReportActivity.this, "", getString(R.string.sending_image_dialog), true);
        logger.logLevelEnd("report", code, sessionId.get());
    }

    @Override
    public void onResponse(Call<ReportResult> call, Response<ReportResult> response) {
        if (response.isSuccessful()) {
            if (response.body() != null &&
                    response.body().signed_requests != null &&
                    response.body().signed_requests.size() == bitmapsPaths.size()) {
                if (bitmapsPaths != null && bitmapsPaths.size() > 0) {
                    numberOfImages = 0;
                    for (int i = 0; i < bitmapsPaths.size(); i++) {
                        String path = bitmapsPaths.get(i);
                        String url = response.body().signed_requests.get(i);
                        sendImage(path, url);
                    }
                } else {
                    showEndResult(true);
                }
            } else {
                showEndResult(false);
            }
        } else {
            showEndResult(false);
        }
    }

    private void sendImage(final String imagePath, String url) {
        //TODO tutaj
        numberOfImages++;
        Api api = PolaApplication.retrofit.create(Api.class);
        File imageFile = new File(imagePath);
        RequestBody photoBody = RequestBody.create(MediaType.parse(MIME_TYPE), imageFile);
        Call<JsonObject> reportResultCall = api.sendReportImage(url, photoBody);
        reportResultCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                File photoFile = new File(imagePath);
                photoFile.delete();
                numberOfImages--;
                if (numberOfImages == 0) {
                    showEndResult(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                numberOfImages--;
                if (numberOfImages == 0) {
                    showEndResult(false);
                }
            }
        });
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
        setImageView(bitmaps);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int type) {
                onPhotoReturned(file);
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(CreateReportActivity.this, getString(R.string.toast_raport_error_no_photo), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPhotoReturned(File file) {
        Bitmap bitmapPhoto = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            bitmapPhoto = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        } catch (OutOfMemoryError e) {
            Toast.makeText(CreateReportActivity.this, getString(R.string.toast_raport_error_no_memory), Toast.LENGTH_LONG).show();
        }
        String photoPath = file.getAbsolutePath();
        if (bitmapsPaths != null && !bitmapsPaths.contains(photoPath)) {
            bitmapsPaths.add(photoPath);
        }
        if (bitmapPhoto.getHeight() > 1000 || bitmapPhoto.getWidth() > 1000) {
            float aspectRatio = bitmapPhoto.getWidth() / (float) bitmapPhoto.getHeight();
            int width = 1000;
            int height = Math.round(width / aspectRatio);
            overrideImageLowRes(bitmapPhoto, width, height, photoPath);
            width = 200;
            height = Math.round(width / aspectRatio);
            bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, width, height, false);//TO use for upload
        }
        if (bitmaps != null && bitmapPhoto != null) {
            bitmaps.add(bitmapPhoto);
            setImageView(bitmaps);
        }
    }

    private void overrideImageLowRes(Bitmap decoded, int width, int height, String photoPath) {
        Bitmap bitmapToSave = Bitmap.createScaledBitmap(decoded, width, height, false);//To use as a thumbnail
        File dest = new File(photoPath);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFiles(ArrayList<String> paths) {
        for (String path : paths) {
            if (path != null) {
                File photoFile = new File(path);
                photoFile.delete();
            }
        }
        if (bitmaps != null) {
            bitmaps.clear();
        }
        if (bitmapsPaths != null) {
            bitmapsPaths.clear();
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmapsPaths != null && bitmapsPaths.size() > 0) {
            deleteFiles(bitmapsPaths);
        }
        if (reportResultCall != null) {
            reportResultCall.cancel();
        }
        super.onDestroy();
    }

    final PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            EasyImage.openCamera(CreateReportActivity.this, 0);
        }

        @Override
        public void permissionRefused() {
            Toast.makeText(CreateReportActivity.this, getString(R.string.toast_no_camera_access), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
