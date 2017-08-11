package org.freefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.freefinder.BuildConfig;
import org.freefinder.R;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.shared.SharedPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddCategoryActivity extends AppCompatActivity {

    @BindView(R.id.category_name) EditText categoryNameTextView;

    @BindView(R.id.autocomplete_parent_category) AutoCompleteTextView parentCategoryTextView;

    @BindView(R.id.add_category_button) Button addCategoryButton;

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

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Category parentCategory = getParentCategory(parentCategoryTextView.getText().toString());

                JSONObject newCategoryJson = new JSONObject();

                try {
                    newCategoryJson.put("name", categoryNameTextView.getText().toString());
                    if(parentCategory != null) {
                        newCategoryJson.put("parent_category_id", parentCategory.getId());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private Category getParentCategory(String parentCategoryName) {
        if(parentCategoryName != null) {
            return realm.where(Category.class).equalTo("name", parentCategoryName).findFirst();
        }

        return null;
    }
}
