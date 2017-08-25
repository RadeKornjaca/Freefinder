package org.freefinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import org.freefinder.R;
import org.freefinder.api.PlaceApi;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.shared.ImageEncoder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddPlaceActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int APP_CAMERA_PERMISSION = 2;

    private final String[] permissions = new String[] {
            Manifest.permission.CAMERA
    };

    @BindView(R.id.place_image) ImageView mImageView;
    private Bitmap imageBitmap;

    @BindView(R.id.place_name) EditText placeNameEditText;
    @BindView(R.id.place_category) AutoCompleteTextView placeCategoryTextView;
    @BindView(R.id.add_place_button) Button addPlaceButton;
    @BindView(R.id.add_place_progress) ProgressBar progressBar;
    @BindView(R.id.add_place_form) ScrollView scrollView;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsCheck()) {
                dispatchTakePictureIntent();
            } else {

                ActivityCompat.requestPermissions(this, permissions, APP_CAMERA_PERMISSION);
            }
        } else {
            dispatchTakePictureIntent();
        }

        realm = Realm.getDefaultInstance();

        List<Category> categories = realm.where(Category.class).findAll();
        List<String> categoryNames = new ArrayList<>();

        for(Category c : categories) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        placeCategoryTextView.setAdapter(arrayAdapter);

        addPlaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Place place = new Place();
                    place.setName(placeNameEditText.getText().toString());
                    final Category category = realm.where(Category.class)
                                                   .equalTo("name", placeCategoryTextView.getText()
                                                                                         .toString())
                                                   .findFirst();
                    place.setCategory(category);
                    place.setEncodedImage(ImageEncoder.encodeImage(imageBitmap));

                    final Location location = getIntent().getParcelableExtra(MainActivity.USER_LOCATION);
                    place.setLat(location.getLatitude());
                    place.setLng(location.getLongitude());

                    JSONObject newPlaceJson = new JSONObject();

                    try {
                        newPlaceJson.put("name", place.getName());
                        newPlaceJson.put("category_id", place.getCategory().getId());
                        newPlaceJson.put("encoded_image", place.getEncodedImage());
                        newPlaceJson.put("lat", place.getLat());
                        newPlaceJson.put("lng", place.getLng());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    PlaceApi.AddNewPlaceTask addNewPlaceTask = new PlaceApi.AddNewPlaceTask(AddPlaceActivity.this);
                    addNewPlaceTask.execute(newPlaceJson);
                }
            }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            mImageView.setAdjustViewBounds(true);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case APP_CAMERA_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean permissionsCheck() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void showProgress(final boolean isLoading) {
        scrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}
