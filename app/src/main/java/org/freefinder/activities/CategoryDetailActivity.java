package org.freefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.api.categories.VisitCategoryService;
import org.freefinder.model.AdditionalField;
import org.freefinder.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import io.realm.Realm;

import static org.freefinder.activities.Constants.RESOURCE_ID;
import static org.freefinder.activities.Constants.REVISION_TYPE;

public class CategoryDetailActivity extends AppCompatActivity {

    @BindView(R.id.category_name)                  TextView categoryNameTextView;
    @BindView(R.id.parent_category)                TextView parentCategoryTextView;
    @BindView(R.id.additional_fields_table_layout) TableLayout additionalFieldsTable;

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
                Intent revisionIntent = new Intent(CategoryDetailActivity.this, RevisionActivity.class);
                revisionIntent.putExtra(REVISION_TYPE, "category");
                revisionIntent.putExtra(RESOURCE_ID, category.getId());
                startActivity(revisionIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final long categoryId = getIntent().getLongExtra("id", 0);
        realm = Realm.getDefaultInstance();
        category = realm.where(Category.class).equalTo("id", categoryId).findFirst();

        categoryNameTextView.setText(category.getName());
        if(category.getParentCategory() != null) {
            parentCategoryTextView.setText(category.getParentCategory().getName());
        }

        for(AdditionalField additionalField : category.getAdditionalFields()) {
            createTableRow(additionalField);
        }

        VisitCategoryService.startService(this, categoryId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.revision:
                Intent revisionDetailIntent = new Intent(this, RevisionDetailActivity.class);
                revisionDetailIntent.putExtra(RESOURCE_ID, getIntent().getLongExtra(RESOURCE_ID, 0));
                revisionDetailIntent.putExtra(REVISION_TYPE, "category");
                startActivity(revisionDetailIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.revision_menu, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void createTableRow(AdditionalField additionalField) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tableRow.setWeightSum(10);

        TextView additionalFieldNameTextView = new TextView(this);
        additionalFieldNameTextView.setGravity(Gravity.START);
        additionalFieldNameTextView.setPadding(5, 5, 5, 5);
        additionalFieldNameTextView.setText(additionalField.getName());

        ViewGroup.LayoutParams nameLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                7
        );

        TextView additionalFieldTypeTextView = new TextView(this);
        additionalFieldTypeTextView.setGravity(Gravity.START);
        additionalFieldTypeTextView.setPadding(5, 5, 5, 5);
        additionalFieldTypeTextView.setText(additionalField.getFieldType());

        ViewGroup.LayoutParams typeLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                3
        );

        tableRow.addView(additionalFieldNameTextView, nameLayoutParams);
        tableRow.addView(additionalFieldTypeTextView, typeLayoutParams);

        additionalFieldsTable.addView(tableRow);
    }
}
