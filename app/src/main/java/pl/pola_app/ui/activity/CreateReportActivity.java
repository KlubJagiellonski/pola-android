package pl.pola_app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        if(productId == null) {
            //productId = "‘8005510001549’";//TODO TEST ONLY
            //This shouldn't happen at all
            this.finish();
        }
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
        linearImageViews.removeAllViews();
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
                launchCamera();
            }
        });
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp));
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        linearImageViews.addView(imageView);
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
        Call<ReportResult> reportResultCall = api.createReport("test", productId, report);
        reportResultCall.enqueue(this);
        if(BuildConfig.USE_CRASHLYTICS) {
            try {
                Answers.getInstance().logLevelEnd(new LevelEndEvent()
                                .putLevelName("Report")
                                .putCustomAttribute("Code", productId + "")
                                .putCustomAttribute("DeviceId", "test")
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
                if(bitmaps != null && bitmaps.size() > 0) {
                    for(Bitmap bitmap : bitmaps) {
                        sendImage(bitmap, Integer.toString(response.body().id));
                    }
                }
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: ");
    }

    private void sendImage(Bitmap photo, String reportId) {

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.56.1:8888/a")
                .addConverterFactory(GsonConverterFactory.create())
                .build();*/
        Api api = PolaApplication.retrofit.create(Api.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), encodedImage);
        Call<JsonObject> reportResultCall = api.sendReportImage("test", reportId, photoBody);
        reportResultCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                Log.d(TAG, "onResponse image");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure image");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_PHOTO_CODE && (resultCode == Activity.RESULT_OK || resultCode == 0 )) {//TODO 0 is RESULT_OK on emulator
            if(photoPath == null) {
                return;
            }
            Bitmap decoded = BitmapFactory.decodeFile(photoPath);
            if(bitmapsPaths != null && !bitmapsPaths.contains(photoPath)) {
                bitmapsPaths.add(photoPath);
            }
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            //original.compress(Bitmap.CompressFormat.PNG, 60, out);//Change quality of bitmap
            //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            //TODO lower quality, but that solution consumes too much time.
            if(decoded.getHeight() > 2048 || decoded.getWidth() > 2048) {
                float aspectRatio = decoded.getWidth() / (float) decoded.getHeight();
                int width = 100;
                int height = Math.round(width / aspectRatio);
                decoded = Bitmap.createScaledBitmap(decoded, width, height, false);
            }

            if (bitmaps != null) {
                bitmaps.add(decoded);
                setImageView(bitmaps);
            }
        }
        photoPath = null;
    }

    private void deleteFiles(ArrayList<String> paths) {
        for(String path : paths) {
            if(path != null) {
                File photoFile = new File(path);
                photoFile.delete();
            }
        }
    }

    @Override
    protected void onStop() {
        if(bitmapsPaths != null && bitmapsPaths.size() > 0) {
            deleteFiles(bitmapsPaths);
        }
        super.onStop();
    }
}
