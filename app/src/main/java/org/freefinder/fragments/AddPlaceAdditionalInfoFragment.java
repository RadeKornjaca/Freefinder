package org.freefinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.factories.AdditionalInfoPlaceFactory;
import org.freefinder.model.AdditionalField;
import org.freefinder.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rade on 2.11.17..
 */

public class AddPlaceAdditionalInfoFragment extends Fragment {

    private static final String PICKED_CATEGORY = "pickedCategory";

    @BindView(R.id.add_place_additional_info_layout) LinearLayout additionalFieldsLayout;

    private Category pickedCategory;

    public AddPlaceAdditionalInfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_place_additional_info, container, false);
        ButterKnife.bind(this, view);

        final Bundle argumentsBundle = getArguments();

        if(argumentsBundle != null) {
            pickedCategory = argumentsBundle.getParcelable(PICKED_CATEGORY);
        }

        if(pickedCategory != null) {
            for (AdditionalField additionalField : pickedCategory.getAdditionalFields()) {
                additionalFieldsLayout.addView(AdditionalInfoPlaceFactory.createView(getActivity(),
                        additionalField));
            }
        } else {
            TextView noCategoryPickedTextView = new TextView(getActivity());
            noCategoryPickedTextView.setText("Pick a category from basic section!");
            additionalFieldsLayout.addView(noCategoryPickedTextView);
        }

        return view;
    }
}
