package org.freefinder.adapters.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import org.freefinder.R;
import org.freefinder.fragments.AddPlaceAdditionalInfoFragment;
import org.freefinder.fragments.AddPlaceBasicFragment;
import org.freefinder.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rade on 1.11.17..
 */

public class AddPlacePagerAdapter extends FragmentStatePagerAdapter {
    private static final int ADD_PLACE_BASIC_INFORMATION = 0;
    private static final int ADD_PLACE_ADDITIONAL_INFORMATION = 1;

    private static final int PAGE_NUMBER = 2;

    private static final String CATEGORY_NAMES = "categoryNames";
    private static final String PICKED_CATEGORY = "pickedCategory";

    private String[] tabTitles;

    private final ArrayList<String> categoryNames;
    private Category pickedCategory;

    public AddPlacePagerAdapter(FragmentManager fm,
                                Context context,
                                final ArrayList<String> categoryNames) {
        super(fm);
        this.categoryNames = categoryNames;

        tabTitles = new String[] {
                context.getResources().getString(R.string.basic_info_tab_text),
                context.getResources().getString(R.string.additional_info_tab_text)
        };
    }

    @Override
    public Fragment getItem(int position) {

        Fragment addPlaceFragment;

        switch(position) {
            case ADD_PLACE_BASIC_INFORMATION:
                Bundle basicInformationBundle = new Bundle();
                basicInformationBundle.putStringArrayList(CATEGORY_NAMES, categoryNames);

                addPlaceFragment = new AddPlaceBasicFragment();
                addPlaceFragment.setArguments(basicInformationBundle);
                break;
            case ADD_PLACE_ADDITIONAL_INFORMATION:
                Bundle pickedCategoryBundle = new Bundle();
                pickedCategoryBundle.putParcelable(PICKED_CATEGORY, pickedCategory);

                addPlaceFragment = new AddPlaceAdditionalInfoFragment();
                addPlaceFragment.setArguments(pickedCategoryBundle);
                break;
            default:
                addPlaceFragment = null;
        }

        return addPlaceFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof AddPlaceBasicFragment) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    public void setPickedCategory(Category pickedCategory) {
        this.pickedCategory = pickedCategory;
    }
}
