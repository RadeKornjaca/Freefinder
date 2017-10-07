package org.freefinder.adapters;

/**
 * Created by rade on 30.9.17..
 */


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.freefinder.fragments.RevisionFragment;
import org.freefinder.model.Revision;

/**
 * A simple pager adapter that represents revision ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final String CURRENT_INFO = "currentInfo";
    private static final String REVISIONABLE = "revisionable";

    private Parcelable   currentInfo;
    private Revision[] revisionables;

    public ScreenSlidePagerAdapter(FragmentManager fm, Parcelable currentInfo, Revision[] revisionables) {
        super(fm);
        this.currentInfo   = currentInfo;
        this.revisionables = revisionables;
    }

    @Override
    public Fragment getItem(int position) {
        RevisionFragment revisionFragment = new RevisionFragment();

        Bundle revisionableBundle = new Bundle();
        revisionableBundle.putParcelable(CURRENT_INFO, currentInfo);
        revisionableBundle.putParcelable(REVISIONABLE, revisionables[position]);;
        revisionFragment.setArguments(revisionableBundle);

        return revisionFragment;
    }

    @Override
    public int getCount() {
        return revisionables.length;
    }
}
