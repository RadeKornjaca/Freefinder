package org.freefinder.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import io.realm.Realm;

public class CategoryDetailActivity extends AppCompatActivity {

    @BindView(R.id.category_name) TextView categoryNameTextView;

    @BindView(R.id.parent_category) TextView parentCategoryTextView;

    private Realm realm;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
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

        final String categoryName = getIntent().getStringExtra("categoryName");
        realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category = realm.where(Category.class).equalTo("name", categoryName).findFirst();
            }
        });

        categoryNameTextView.setText(category.getName());
        if(category.getParentCategory() != null) {
            parentCategoryTextView.setText(category.getParentCategory().getName());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
