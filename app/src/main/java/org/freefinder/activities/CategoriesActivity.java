package org.freefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.freefinder.BuildConfig;
import org.freefinder.R;
import org.freefinder.adapters.CategoryAdapter;
import org.freefinder.http.JsonArrayRequestWithToken;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.shared.SharedPreferencesHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = CategoriesActivity.class.getSimpleName();

    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;

    private Realm realm;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCategoryIntent = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
                startActivity(addCategoryIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateCategories();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        // RealmResults are "live" views, that are automatically kept up to date, even when changes happen
        // on a background thread. The RealmBaseAdapter will automatically keep track of changes and will
        // automatically refresh when a change is detected.
        RealmResults<Category> categories = realm.where(Category.class).findAll();
        categoryAdapter = new CategoryAdapter(categories);

        ListView listView = (ListView) findViewById(R.id.category_list);
        listView.setAdapter(categoryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category = categoryAdapter.getItem(i);

                Intent categoryDetailIntent = new Intent(CategoriesActivity.this, CategoryDetailActivity.class);
                categoryDetailIntent.putExtra("categoryName", category.getName());
                startActivity(categoryDetailIntent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateCategories();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void updateCategories() {
        // This method performs the actual data-refresh operation.
        // The method calls setRefreshing(false) when it's finished.

        JsonObjectRequestWithToken categoriesRequest = new JsonObjectRequestWithToken(
                Request.Method.GET,
                getUpdateUrl(),
                SharedPreferencesHelper.getAuthorizationToken(CategoriesActivity.this),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            final JSONArray categoriesJson = response.getJSONArray("categories");
                            final String updateTimestamp = response.getString("updateTimestamp");

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.createOrUpdateAllFromJson(Category.class, categoriesJson);
                                }
                            });

                            SharedPreferencesHelper.setCategoryUpdateTimestamp(CategoriesActivity.this, updateTimestamp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoriesActivity.this,
                        "Could not fetch categories",
                        Toast.LENGTH_LONG);
            }
        });

        RequestQueueSingleton.getInstance(CategoriesActivity.this).enqueueRequest(categoriesRequest);
    }

    private String getUpdateUrl() {
        final String url = TextUtils.join("/", new String[] { BuildConfig.API_URL,
                                                              BuildConfig.CATEGORIES });

        final String updateTimestamp = SharedPreferencesHelper.getCategoryUpdateTimestamp(this);

        if(updateTimestamp != null) {
            final String timestampParameter = TextUtils.join("=", new String[]{"update_timestamp",
                    SharedPreferencesHelper.getCategoryUpdateTimestamp(this)});

            return TextUtils.join("?", new String[] {url, timestampParameter});
        }

        return url;
    }
}
