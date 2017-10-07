package org.freefinder.activities;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import org.freefinder.R;
import org.freefinder.api.RevisionApi;
import org.freefinder.fragments.RevisionFragment;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.receivers.HttpServiceReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static org.freefinder.activities.Constants.RESOURCE_ID;
import static org.freefinder.activities.Constants.REVISION_TYPE;

public class RevisionDetailActivity extends AppCompatActivity
                                    implements RevisionFragment.OnRevisionFragmentClickListener {

    @BindView(R.id.revision_download_progress) ProgressBar revisionDownloadProgressBar;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    private Parcelable resource;
    private String revisionType;

    private Realm realm;

    private HttpServiceReceiver httpServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final long resourceId = getIntent().getExtras().getLong(RESOURCE_ID, 0);
        revisionType = getIntent().getExtras().getString(REVISION_TYPE);

        realm = Realm.getDefaultInstance();

        switch (revisionType) {
            case "category":
                resource = realm.where(Category.class).equalTo("id", resourceId).findFirst();
                break;
            case "place":
                resource = realm.where(Place.class).equalTo("id", resourceId).findFirst();
                break;
        }

        // Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager) findViewById(R.id.pager);

//        long id = getIntent().getLongExtra("id", 0);
//        String revisionType = getIntent().getStringExtra("revisionType");
        RevisionApi.RevisionFetchTask revisionFetchTask = new RevisionApi.RevisionFetchTask(this);
        Bundle revisionBundle = getIntent().getExtras();
        revisionFetchTask.execute(revisionBundle);
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
    public void onFragmentButtonClicked(View view) {
        int currPos = pager.getCurrentItem();

        pager.setCurrentItem(currPos + 1);
    }

    public void showProgress(boolean isLoading) {
        revisionDownloadProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        pager.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    public ViewPager getPager() {
        return pager;
    }

    public void setPager(ViewPager pager) {
        this.pager = pager;
    }

    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    public Parcelable getResource() {
        return resource;
    }

    public HttpServiceReceiver getHttpServiceReceiver() {
        return httpServiceReceiver;
    }

    public void setHttpServiceReceiver(HttpServiceReceiver httpServiceReceiver) {
        this.httpServiceReceiver = httpServiceReceiver;
    }
}
