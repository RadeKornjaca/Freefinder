package org.freefinder.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.freefinder.R;
import org.freefinder.api.RevisionApi;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static org.freefinder.activities.Constants.RESOURCE_ID;
import static org.freefinder.activities.Constants.REVISION_TYPE;

public class RevisionActivity extends AppCompatActivity {

    @BindView(R.id.revision) ConstraintLayout revisionLayout;

    private Realm realm;

    private long id;
    private String revisionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater layoutInflater = getLayoutInflater();
        realm = Realm.getDefaultInstance();

        if(getIntent().getExtras() != null) {
            id = getIntent().getExtras().getLong(RESOURCE_ID, 0);
            revisionType = getIntent().getExtras().getString(REVISION_TYPE);
        } else {
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            id = sharedPreferences.getLong(RESOURCE_ID, 0);
            revisionType = sharedPreferences.getString(REVISION_TYPE, "");
        }

        RealmResults<Category> categories = realm.where(Category.class).findAll();
        List<String> categoryNames = new ArrayList<>();

        for(Category c : categories) {
            categoryNames.add(c.getName());
        }

        switch(revisionType) {
            case "category":
                createPrefilledCategoryForm(id, categoryNames, layoutInflater);
                break;
            case "place":
                createPrefilledPlaceForm(id, categoryNames, layoutInflater);
                break;

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(RESOURCE_ID, id);
        editor.putString(REVISION_TYPE, revisionType);

        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(RESOURCE_ID, id);
        outState.putString(REVISION_TYPE, revisionType);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        id = savedInstanceState.getLong(RESOURCE_ID);
        revisionType = savedInstanceState.getString(REVISION_TYPE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                final String revisionType = getIntent().getStringExtra(REVISION_TYPE);
                Intent backIntent = new Intent();
                switch (revisionType) {
                    case "category":
                        backIntent.setClass(this, CategoryDetailActivity.class);
                        break;
                    case "place":
                        backIntent.setClass(this, PlaceDetailActivity.class);
                        break;
                }

                backIntent.putExtra(RESOURCE_ID, id);
                backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(backIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createPrefilledCategoryForm(long id,
                                             List<String> categoryNames,
                                             LayoutInflater layoutInflater) {
        View revisionForm = layoutInflater.inflate(R.layout.content_add_category, revisionLayout, false);

        Category category = realm.where(Category.class).equalTo("id", id).findFirst();

        EditText categoryNameEditText = (EditText) revisionForm.findViewById(R.id.category_name);
        AutoCompleteTextView parentCategoryTextView = (AutoCompleteTextView) revisionForm.findViewById(R.id.autocomplete_parent_category);
        Button submitButton = (Button) revisionForm.findViewById(R.id.add_category_button);
        submitButton.setText(getString(R.string.category_revision_submit_text));
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitRevision();
            }
        });

        categoryNameEditText.setText(category.getName());
        if(category.getParentCategory() != null) {
            parentCategoryTextView.setText(category.getParentCategory().getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        parentCategoryTextView.setAdapter(arrayAdapter);

        revisionLayout.addView(revisionForm);
    }

    private void createPrefilledPlaceForm(long id,
                                          List<String> categoryNames,
                                          LayoutInflater layoutInflater) {
        View revisionForm = layoutInflater.inflate(R.layout.content_add_place, revisionLayout, false);

        Place place = realm.where(Place.class).equalTo("id", id).findFirst();

        EditText placeNameEditText = (EditText) revisionForm.findViewById(R.id.place_name);
        EditText placeDescriptionEditText = (EditText) revisionForm.findViewById(R.id.place_description);
        AutoCompleteTextView placeCategoryTextView = (AutoCompleteTextView) revisionForm.findViewById(R.id.place_category);
        Button submitButton = (Button) revisionForm.findViewById(R.id.add_place_button);
        submitButton.setText(getString(R.string.place_revision_submit_text));
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitRevision();
            }
        });

        placeNameEditText.setText(place.getName());
        placeDescriptionEditText.setText(place.getDescription());
        placeCategoryTextView.setText(place.getCategory().getName());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        placeCategoryTextView.setAdapter(arrayAdapter);

        revisionLayout.addView(revisionForm);
    }

    private JSONObject createCategoryJson() throws JSONException {
        JSONObject categoryJson = new JSONObject();

        EditText categoryNameEditText = (EditText) findViewById(R.id.category_name);
        AutoCompleteTextView parentCategoryTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_parent_category);

        categoryJson.put("name", categoryNameEditText.getText().toString());
        Category parentCategory = realm.where(Category.class)
                                  .equalTo("name", parentCategoryTextView.getText().toString())
                                  .findFirst();

        if(parentCategory != null) {
            categoryJson.put("parent_category_id", parentCategory.getId());
        }

        return categoryJson;
    }

    private JSONObject createPlaceJson() throws JSONException {
        JSONObject placeJson = new JSONObject();

        EditText placeNameEditText = (EditText) findViewById(R.id.place_name);
        EditText placeDescriptionEditText = (EditText) findViewById(R.id.place_description);
        AutoCompleteTextView placeCategoryTextView = (AutoCompleteTextView) findViewById(R.id.place_category);

        placeJson.put("name", placeNameEditText.getText().toString());
        placeJson.put("description", placeDescriptionEditText.getText().toString());
        Category category = realm.where(Category.class)
                .equalTo("name", placeCategoryTextView.getText().toString())
                .findFirst();
        placeJson.put("category_id", category.getId());

        return placeJson;
    }

    private void submitRevision() {
        JSONObject revisionJson = new JSONObject();
        JSONObject paramsJson = new JSONObject();

        try {
            paramsJson.put("proposable", revisionType.equals("category") ? createCategoryJson()
                                                                         : createPlaceJson());
            revisionJson.put("revision", paramsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RevisionApi.AddRevisionTask addRevisionTask = new RevisionApi.AddRevisionTask(this, id, revisionType);
        addRevisionTask.execute(revisionJson);
    }
}
