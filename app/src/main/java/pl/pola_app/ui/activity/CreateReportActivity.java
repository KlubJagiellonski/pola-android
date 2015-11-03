package pl.pola_app.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import java.io.IOException;
import java.io.InputStream;
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
    private static final int REQUEST_PHOTO_CODE = 133;
    private String photoPath;
    private int photoMarginDp = 4;
    private ProgressDialog progressDialog;
    private int numberOfImages;

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

        if("product_report".equals(getIntent().getAction())) {
            productId = getIntent().getStringExtra("productId");
        }
        setImageView(bitmaps);

        if(BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logLevelStart(new LevelStartEvent()
                                .putLevelName("Report")
                                .putCustomAttribute("Code", productId+"") //because can be null, ugly
                                .putCustomAttribute("DeviceId", Utils.getDeviceId(this))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setImageView(final ArrayList<Bitmap> bitmapsToSet) {
        int margin = Utils.dpToPx(photoMarginDp);
        linearImageViews.removeAllViews();
        if(bitmapsToSet != null && bitmapsToSet.size() > 0) {
            for (final Bitmap bitmap : bitmapsToSet) {
                ImageView imageView = new ImageView(this);
                imageView.setPadding(margin, margin, margin, margin);
                int width = bitmap.getWidth() * Utils.dpToPx(80)/bitmap.getHeight();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, Utils.dpToPx(80));
                imageView.setLayoutParams(layoutParams);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogDeletePhoto(bitmapsToSet.indexOf(bitmap));
                    }
                });
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                linearImageViews.addView(imageView);
            }
        }
        //Add add button
        ImageView imageView = new ImageView(this);
        imageView.setPadding(margin, margin, margin, margin);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utils.dpToPx(80), Utils.dpToPx(80));
        imageView.setLayoutParams(layoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                launchCamera();
            }
        });
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        linearImageViews.addView(imageView);
    }

    private void showDialogDeletePhoto(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if(bitmaps != null && bitmaps.size() >= position) {
                            bitmaps.remove(position);
                            setImageView(bitmaps);
                        }
                        if(bitmapsPaths!= null && bitmapsPaths.size() >= position) {
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
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PolaPictures/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir+System.currentTimeMillis()+".jpg";
        File photoFile = new File(file);
        try {
            photoFile.createNewFile();
        } catch (IOException e) {}

        Uri outputFileUri = Uri.fromFile(photoFile);
        photoPath = photoFile.getAbsolutePath();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, REQUEST_PHOTO_CODE);
    }

    @OnClick(R.id.send_button)
    public void clickSendButton() {
        String description = descriptionEditText.getText().toString();
        sendReport(description);
    }

    private void sendReport(String description) {
        if(description == null) {
            description = "";
        }
        Report report = new Report(description);
        Api api = PolaApplication.retrofit.create(Api.class);
        Call<ReportResult> reportResultCall;
        if(productId != null && productId.length() > 0) {
            reportResultCall = api.createReport(Utils.getDeviceId(CreateReportActivity.this), productId, report);
        } else {
            reportResultCall = api.createReport(Utils.getDeviceId(CreateReportActivity.this), report);
        }
        reportResultCall.enqueue(this);
        numberOfImages = bitmapsPaths.size();
        progressDialog = ProgressDialog.show(CreateReportActivity.this, "", getString(R.string.sending_image_dialog), true);
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
        if(response.isSuccess()) {
            if(response.body() != null) {
                if(bitmapsPaths != null && bitmapsPaths.size() > 0) {
                    numberOfImages = 0;
                    for(String path : bitmapsPaths) {
                        sendImage(path, Integer.toString(response.body().id));
                    }
                }
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: ");
        if(progressDialog != null && progressDialog.isShowing()) {
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
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Toast.makeText(CreateReportActivity.this, getString(R.string.toast_send_raport), Toast.LENGTH_LONG).show();
                    CreateReportActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure image");
                numberOfImages--;
                if (numberOfImages == 0) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Toast.makeText(CreateReportActivity.this, getString(R.string.toast_send_raport_error), Toast.LENGTH_LONG).show();
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
        InputStream stream = null;
        if (requestCode == REQUEST_PHOTO_CODE && (resultCode == Activity.RESULT_OK || resultCode == 0 )) {//TODO 0 is RESULT_OK on emulator
            if(photoPath == null) {
                return;
            }
            Bitmap bitmapPhoto = BitmapFactory.decodeFile(photoPath);
            if(bitmapsPaths != null && !bitmapsPaths.contains(photoPath)) {
                bitmapsPaths.add(photoPath);
            }
            if(bitmapPhoto.getHeight() > 1000 || bitmapPhoto.getWidth() > 1000) {
                float aspectRatio = bitmapPhoto.getWidth() / (float) bitmapPhoto.getHeight();
                int width = 1000;
                int height = Math.round(width / aspectRatio);
                overrideImageLowRes(bitmapPhoto, width, height);
                width = 200;
                height = Math.round(width / aspectRatio);
                bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, width, height, false);//TO use for upload
            }
            if (bitmaps != null && bitmapPhoto != null) {
                bitmaps.add(bitmapPhoto);
                setImageView(bitmaps);
            }
        }
        photoPath = null;
    }

    private void overrideImageLowRes(Bitmap decoded, int width, int height) {
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
        for(String path : paths) {
            if(path != null) {
                File photoFile = new File(path);
                photoFile.delete();
            }
        }
        if(bitmaps != null) {
            bitmaps.clear();
        }
        if(bitmapsPaths != null) {
            bitmapsPaths.clear();
        }
    }

    @Override
    protected void onDestroy() {
        if(bitmapsPaths != null && bitmapsPaths.size() > 0) {
            deleteFiles(bitmapsPaths);
        }
        super.onDestroy();
    }
}
