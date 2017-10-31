package org.freefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.freefinder.BuildConfig;
import org.freefinder.R;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.AdditionalField;
import org.freefinder.model.Category;
import org.freefinder.model.serializers.AdditionalFieldSerializer;
import org.freefinder.model.serializers.CategorySerializer;
import org.freefinder.shared.SharedPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class AddCategoryActivity extends AppCompatActivity {

    private static final String ADDITIONAL_FIELDS       = "additionalFields";

    @BindView(R.id.category_name)                EditText categoryNameTextView;
    @BindView(R.id.autocomplete_parent_category) AutoCompleteTextView parentCategoryTextView;
    @BindView(R.id.additional_fields_layout)     LinearLayout additionalFieldsLayout;
    @BindView(R.id.add_category_button)          Button addCategoryButton;

    private Realm realm;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
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

        if(savedInstanceState != null) {
            final ArrayList<AdditionalField> additionalFields = savedInstanceState.getParcelableArrayList(ADDITIONAL_FIELDS);

            for(AdditionalField additionalField : additionalFields) {
                createField();

                LinearLayout fieldLayout = (LinearLayout) additionalFieldsLayout.getChildAt(additionalFieldsLayout.getChildCount() - 2);
                ((EditText) fieldLayout.getChildAt(0)).setText(additionalField.getName());
                ((Spinner) fieldLayout.getChildAt(1)).setSelection(0);
            }
        }

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Category parentCategory = getParentCategory(parentCategoryTextView.getText().toString());
                RealmList<AdditionalField> additionalFields = getAdditionalFields();

                Category newCategory = new Category();
                newCategory.setName(categoryNameTextView.getText().toString());
                newCategory.setParentCategory(parentCategory);
                newCategory.setAdditionalFields(additionalFields);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Category.class, new CategorySerializer());
                gsonBuilder.registerTypeAdapter(AdditionalField.class, new AdditionalFieldSerializer());
                Gson gson = gsonBuilder.create();
                JSONObject newCategoryJson = null;
                try {
                    newCategoryJson = new JSONObject(gson.toJson(newCategory));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                JSONObject newCategoryJson = new JSONObject();
//
//                try {
//                    newCategoryJson.put("name", categoryNameTextView.getText().toString());
//                    if(parentCategory != null) {
//                        newCategoryJson.put("parent_category_id", parentCategory.getId());
//                    }
//
//                    ArrayList<AdditionalField> additionalFields = getAdditionalFields();
//                    newCategoryJson.put("additional_fields", additionalFields);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                JsonObjectRequestWithToken addCategoryRequest = new JsonObjectRequestWithToken(
                        Request.Method.POST,
                        TextUtils.join("/", new String[]{
                                BuildConfig.API_URL, BuildConfig.CATEGORIES
                        }),
                        SharedPreferencesHelper.getAuthorizationToken(AddCategoryActivity.this),
                        newCategoryJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = new Intent(AddCategoryActivity.this, CategoriesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                RequestQueueSingleton.getInstance(AddCategoryActivity.this).enqueueRequest(addCategoryRequest);
            }
        });

        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                categories = realm.where(Category.class).findAll();
            }
        });

        List<String> categoryNames = new ArrayList<>();

        for(Category c : categories) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        parentCategoryTextView.setAdapter(arrayAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<AdditionalField> additionalFields = new ArrayList<>();
        additionalFields.addAll(getAdditionalFields());

        outState.putParcelableArrayList(ADDITIONAL_FIELDS, additionalFields);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick(R.id.add_field_button)
    public void onClick() {
        createField();
    }

    private void createField() {
        Spinner fieldType = new Spinner(this);
        ArrayAdapter<String> fieldTypesAdapter = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                new String[] {"Text", "Checkbox"}
        );
        fieldType.setAdapter(fieldTypesAdapter);

        EditText fieldName = new EditText(this);
        fieldName.setHint("Field Name");
        ViewGroup.LayoutParams fieldNameLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0.7f
        );
        fieldName.setLayoutParams(fieldNameLayoutParams);

        LinearLayout fieldLinearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams fieldLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                     ViewGroup.LayoutParams.WRAP_CONTENT);
        fieldLinearLayout.setLayoutParams(fieldLayoutParams);

        fieldLinearLayout.addView(fieldName);
        fieldLinearLayout.addView(fieldType);

        additionalFieldsLayout.addView(fieldLinearLayout, additionalFieldsLayout.getChildCount() - 1);
    }

    private RealmList<AdditionalField> getAdditionalFields() {
        RealmList<AdditionalField> additionalFields = new RealmList<>();

        for(int i = 0;i < additionalFieldsLayout.getChildCount() - 1;i++) {
            AdditionalField additionalField = new AdditionalField();

            final LinearLayout fieldRow = (LinearLayout) additionalFieldsLayout.getChildAt(i);
            additionalField.setName(((EditText) fieldRow.getChildAt(0)).getText().toString());
            additionalField.setFieldType((String) (((Spinner) fieldRow.getChildAt(1)).getSelectedItem()));

            additionalFields.add(additionalField);
        }
        return additionalFields;
    }

    private Category getParentCategory(String parentCategoryName) {
        if(parentCategoryName != null) {
            return realm.where(Category.class).equalTo("name", parentCategoryName).findFirst();
        }

        return null;
    }
}
