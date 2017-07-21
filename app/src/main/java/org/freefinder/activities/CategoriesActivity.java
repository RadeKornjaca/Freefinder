package org.freefinder.activities;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.freefinder.BuildConfig;
import org.freefinder.R;
import org.freefinder.adapters.CategoryAdapter;
import org.freefinder.http.JsonArrayRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.shared.SharedPreferencesHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = CategoriesActivity.class.getSimpleName();

    private Realm realm;
    private CategoryAdapter categoryAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayAdapter<Category> mCategoryArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
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

        realm = Realm.getDefaultInstance();

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        // myUpdateOperation();

                        JsonArrayRequestWithToken categoriesRequest = new JsonArrayRequestWithToken(
                                Request.Method.GET,
                                TextUtils.join("/", new String[] { BuildConfig.API_URL,
                                                                   BuildConfig.CATEGORIES }),
                                SharedPreferencesHelper.getAuthorizationToken(CategoriesActivity.this),
                                null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        final JSONArray categoriesJson = response;
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                realm.createOrUpdateAllFromJson(Category.class, categoriesJson);
                                            }
                                        });
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category = categoryAdapter.getItem(i);
                if (category == null) {
                    return true;
                }

                final String id = category.getName();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // realm.where(Category.class).equalTo(Counter.FIELD_COUNT, id).findAll().deleteAllFromRealm();
                    }
                });
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        List<Category> categories = new ArrayList<>();
//        Category category1 = new Category();
//        category1.setName("test1");
//        Category category2 = new Category();
//        category2.setName("test2");
//        category2.setCategory(category1);
//
//        categories.add(category1);
//        categories.add(category2);
//
//        mCategoryArrayAdapter = new ArrayAdapter<Category>(this,
//                R.layout.list_row,
//                R.id.one_element_row,
//                categories);
//
//        ListView listView = (ListView) findViewById(R.id.category_list);
//
//        listView.setAdapter(mCategoryArrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
