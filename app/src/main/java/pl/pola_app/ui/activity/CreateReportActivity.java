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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.pola_app.BuildConfig;
import pl.pola_app.PolaApplication;
import pl.pola_app.R;
import pl.pola_app.helpers.Utils;
import pl.pola_app.model.Report;
import pl.pola_app.model.ReportResult;
import pl.pola_app.network.Api;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CreateReportActivity extends Activity implements Callback<ReportResult> {
    private static final String TAG = CreateReportActivity.class.getSimpleName();
    private static final int MAX_IMAGE_COUNT = 2;
    private String productId;
    private int photoMarginDp = 6;
    private ProgressDialog progressDialog;
    private int numberOfImages;
    private Call<ReportResult> reportResultCall;

    @Bind(R.id.descripton_editText)
    EditText descriptionEditText;
    @Bind(R.id.linearImageViews)
    LinearLayout linearImageViews;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<String> bitmapsPaths = new ArrayList<>();//As we save file, it would be good to delete them after we send them

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            productId = getIntent().getStringExtra("productId");
        }
        setImageView(bitmaps);
        Nammu.init(this);

        if (BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logLevelStart(new LevelStartEvent()
                                .putLevelName("Report")
                                .putCustomAttribute("Code", productId + "") //because can be null, ugly
                                .putCustomAttribute("DeviceId", Utils.getDeviceId(this))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        super.onPause();
    }

    private void setImageView(final ArrayList<Bitmap> bitmapsToSet) {
        int margin = Utils.dpToPx(photoMarginDp);
        linearImageViews.removeAllViews();

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
                linearImageViews.addView(imageView);
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
                    Log.d(TAG, "onClick: ");
                    launchCamera();
                }
            });
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            linearImageViews.addView(imageView);
        }
    }

    private void showDialogDeletePhoto(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (bitmaps != null && bitmaps.size() >= position) {
                            bitmaps.remove(position);
                            setImageView(bitmaps);
                        }
                        if (bitmapsPaths != null && bitmapsPaths.size() >= position) {
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
        Nammu.askForPermission(CreateReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionWriteCallback);
        ;
    }

    @OnClick(R.id.send_button)
    public void clickSendButton() {
        String description = descriptionEditText.getText().toString();
        sendReport(description, productId);
    }

    private void sendReport(String description, String productId) {
        if (productId == null && (bitmapsPaths == null || bitmapsPaths.size() == 0)) {
            Toast.makeText(CreateReportActivity.this, getString(R.string.toast_raport_error_no_pic), Toast.LENGTH_LONG).show();
            return;
        } else if (description == null) {
            description = "";
        }
        Report report;
        if (productId != null) {
            report = new Report(description, productId);
        } else {
            report = new Report(description);
        }
        Api api = PolaApplication.retrofit.create(Api.class);
        reportResultCall = api.createReport(Utils.getDeviceId(CreateReportActivity.this), report);
        reportResultCall.enqueue(this);
        numberOfImages = bitmapsPaths.size();
        progressDialog = ProgressDialog.show(CreateReportActivity.this, "", getString(R.string.sending_image_dialog), true);
        if (BuildConfig.USE_CRASHLYTICS) {
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
        if (response.isSuccess()) {
            if (response.body() != null) {
                if (bitmapsPaths != null && bitmapsPaths.size() > 0) {
                    numberOfImages = 0;
                    for (String path : bitmapsPaths) {
                        sendImage(path, Integer.toString(response.body().id));
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
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: ");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        Toast.makeText(CreateReportActivity.this, getString(R.string.toast_send_raport_error), Toast.LENGTH_LONG).show();
    }

    private void sendImage(final String imagePath, String reportId) {
        numberOfImages++;
        Api api = PolaApplication.retrofit.create(Api.class);
        File imageFile = new File(imagePath);
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
        Call<JsonObject> reportResultCall = api.sendReportImage(Utils.getDeviceId(CreateReportActivity.this), reportId, photoBody);
        reportResultCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                Log.d(TAG, "onResponse image");
                File photoFile = new File(imagePath);
                photoFile.delete();
                numberOfImages--;
                if (numberOfImages == 0) {
                    showEndResult(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure image");
                numberOfImages--;
                if (numberOfImages == 0) {
                    showEndResult(false);
                }
            }
        });
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
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                Toast.makeText(CreateReportActivity.this, "Brak zdjęcia", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                onPhotoReturned(imageFile);
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

    final PermissionCallback permissionWriteCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            EasyImage.openCamera(CreateReportActivity.this);
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
