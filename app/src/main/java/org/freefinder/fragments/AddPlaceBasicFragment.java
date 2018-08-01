package org.freefinder.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import org.freefinder.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rade on 31.10.17..
 */

public class AddPlaceBasicFragment extends Fragment {

    private static final String CATEGORY_NAMES = "categoryNames";

    @BindView(R.id.place_image) ImageView imageView;
    private Bitmap imageBitmap;

    @BindView(R.id.place_name)         EditText placeNameEditText;
    @BindView(R.id.place_description)  EditText placeDescriptionEditText;
    @BindView(R.id.place_category)     AutoCompleteTextView placeCategoryTextView;

    private OnPickedCategoryListener onCategoryPickedListener;

    public AddPlaceBasicFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_place_basic, container, false);
        ButterKnife.bind(this, view);

        final List<String> categoryNames = getArguments().getStringArrayList(CATEGORY_NAMES);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
        );

        placeCategoryTextView.setAdapter(arrayAdapter);
        placeCategoryTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String pickedCategoryName = (String) parent.getItemAtPosition(position);

                onCategoryPickedListener.onPickedCategory(pickedCategoryName);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onCategoryPickedListener = (OnPickedCategoryListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnPickedCategoryListener");
        }
    }

    public interface OnPickedCategoryListener {
        void onPickedCategory(final String categoryName);
    }
}
