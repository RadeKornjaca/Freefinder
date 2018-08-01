package org.freefinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import org.freefinder.R;
import org.freefinder.adapters.pager.AddPlacePagerAdapter;
import org.freefinder.api.Status;
import org.freefinder.api.places.AddNewPlaceService;
import org.freefinder.fragments.AddPlaceBasicFragment;
import org.freefinder.model.Category;
import org.freefinder.receivers.HttpServiceReceiver;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AddPlaceActivity extends AppCompatActivity
                              implements AddPlaceBasicFragment.OnPickedCategoryListener,
                                         HttpServiceReceiver.Receiver {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int APP_CAMERA_PERMISSION = 2;

    private final String[] permissions = new String[] {
            Manifest.permission.CAMERA
    };

    @BindView(R.id.add_place_tabs)     TabLayout tabLayout;
    @BindView(R.id.add_place_progress) ProgressBar progressBar;
    @BindView(R.id.add_place_form)     ScrollView scrollView;
    @BindView(R.id.add_place_button)   Button addPlaceButton;

//    @BindView(R.id.add_place_layout)   ConstraintLayout addPlaceConstraintLayout;

    @BindView(R.id.add_place_pager) ViewPager pager;
    private PagerAdapter pagerAdapter;

    private Realm realm;
    private Category pickedCategory;

    private HttpServiceReceiver httpServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        final RealmResults<Category> categories = realm.where(Category.class).findAll();
        ArrayList<String> categoryNames = new ArrayList<>();

        for(Category c : categories) {
            categoryNames.add(c.getName());
        }

//        placeCategoryTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus) {
//                    final String selectedCategoryName = ((AutoCompleteTextView) v).getText()
//                                                                                  .toString();
//                    final Category category = categories.where()
//                                                        .equalTo("name", selectedCategoryName)
//                                                        .findFirst();
//
//                    for(AdditionalField additionalField : category.getAdditionalFields()) {
//                        createEditTextField(additionalField);
//                    }
//                }
//            }
//        });




        // TODO: Refactor Add place button

//        addPlaceButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Place place = new Place();
//                    place.setName(placeNameEditText.getText().toString());
//                    place.setDescription(placeDescriptionEditText.getText().toString());
//                    final Category category = realm.where(Category.class)
//                                                   .equalTo("name", placeCategoryTextView.getText()
//                                                                                         .toString())
//                                                   .findFirst();
//                    place.setCategory(category);
//                    place.setEncodedImage(ImageEncoder.encodeImage(imageBitmap));
//
//                    final Location location = getIntent().getParcelableExtra(MainActivity.USER_LOCATION);
//                    place.setLat(location.getLatitude());
//                    place.setLng(location.getLongitude());
//
//                    JSONObject newPlaceJson = new JSONObject();
//
//                    try {
//                        newPlaceJson.put("name", place.getName());
//                        newPlaceJson.put("description", place.getDescription());
//                        newPlaceJson.put("category_id", place.getCategory().getId());
//                        newPlaceJson.put("encoded_image", place.getEncodedImage());
//                        newPlaceJson.put("lat", place.getLat());
//                        newPlaceJson.put("lng", place.getLng());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    PlaceApi.AddNewPlaceTask addNewPlaceTask = new PlaceApi.AddNewPlaceTask(AddPlaceActivity.this);
//                    addNewPlaceTask.execute(newPlaceJson);
//                }
//            }
//        );

//        tabLayout.addTab(tabLayout.newTab().setText("Basic"));
//        tabLayout.addTab(tabLayout.newTab().setText("Additional Info"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        httpServiceReceiver = new HttpServiceReceiver(new Handler());
        httpServiceReceiver.setReceiver(this);

        FragmentManager sfm = getSupportFragmentManager();

        pagerAdapter = new AddPlacePagerAdapter(sfm, this, categoryNames);
        pager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // TODO: Image capture event

//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//
//            mImageView.setAdjustViewBounds(true);
//            mImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onPickedCategory(final String categoryName) {
        pickedCategory = realm.where(Category.class)
                              .equalTo("name", categoryName)
                              .findFirst();

//        AddPlaceAdditionalInfoFragment addPlaceAdditionalInfoFragment = (AddPlaceAdditionalInfoFragment)
//                getSupportFragmentManager().findFragmentById(R.id.add_place_additional_info_layout);
//
//        if(addPlaceAdditionalInfoFragment != null) {
////            final Bundle pickedCategoryBundle = new Bundle();
////            pickedCategoryBundle.putParcelable("pickedCategory", pickedCategory);
////
////            addPlaceAdditionalInfoFragment.setArguments(pickedCategoryBundle);
//            addPlaceAdditionalInfoFragment.generateUI(pickedCategory);
//        }

        ((AddPlacePagerAdapter) pagerAdapter).setPickedCategory(pickedCategory);
        pagerAdapter.notifyDataSetChanged();
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


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch(resultCode) {
            case Status.SUCCESS:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(intent);
                break;
            case Status.FAILURE:
                Snackbar.make( findViewById(android.R.id.content),
                               "Couldn't add new place.",
                               Snackbar.LENGTH_LONG)
                        .show();
                break;
        }
    }

    @OnClick(R.id.add_place_button)
    public void submitNewPlace() {
        AddNewPlaceService.startService(this, httpServiceReceiver);
    }

    private boolean permissionsCheck() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void showProgress(final boolean isLoading) {
        scrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

//    private void createEditTextField(AdditionalField additionalField) {
//        ConstraintSet constraintSet = new ConstraintSet();
//        View additionalFieldView;
//
//        switch(additionalField.getFieldType()) {
//            case "Text":
//                additionalFieldView = new EditText(this);
//                ((EditText) additionalFieldView).setHint(additionalField.getName());
//
//                break;
//            case "Checkbox":
//                additionalFieldView = new CheckBox(this);
//                break;
//            default:
//                additionalFieldView = new View(this);
//        }
//
//        addPlaceConstraintLayout.addView(additionalFieldView);
//
//        constraintSet.connect(R.id.add_place_layout,
//                ConstraintSet.START,
//                ConstraintSet.PARENT_ID,
//                ConstraintSet.START,
//                8);
//        constraintSet.connect(R.id.add_place_layout,
//                ConstraintSet.END,
//                ConstraintSet.PARENT_ID,
//                ConstraintSet.END,
//                8);
//
//        int lastElementId = addPlaceConstraintLayout.getChildAt(addPlaceConstraintLayout.getChildCount() - 1).getId();
//        constraintSet.connect(lastElementId,
//                              ConstraintSet.BOTTOM,
//                              additionalFieldView.getId(),
//                              ConstraintSet.TOP);
//
//    }
}
