package org.freefinder.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.activities.RevisionDetailActivity;
import org.freefinder.api.RevisionApi;
import org.freefinder.api.Status;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.model.Revision;
import org.freefinder.receivers.HttpServiceReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RevisionFragment extends Fragment implements HttpServiceReceiver.Receiver {
    private static final String CURRENT_INFO = "currentInfo";
    private static final String REVISIONABLE = "revisionable";

    @BindView(R.id.approve_button)  ImageButton approveButton;
    @BindView(R.id.disprove_button) ImageButton disproveButton;

    private HttpServiceReceiver httpServiceReceiver;
    private OnRevisionFragmentClickListener onRevisionFragmentClickListener;

    private Parcelable currentInfo;
    private Revision   revision;

    public RevisionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpServiceReceiver = new HttpServiceReceiver(new Handler());
        httpServiceReceiver.setReceiver(this);

        ((RevisionDetailActivity) getActivity()).setHttpServiceReceiver(httpServiceReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragmentView = inflater.inflate(R.layout.fragment_revision, container, false);
        ScrollView proposalScrollView = (ScrollView) fragmentView.findViewById(R.id.proposal);

        currentInfo = this.getArguments().getParcelable(CURRENT_INFO);

        revision = this.getArguments().getParcelable(REVISIONABLE);
        Parcelable proposable = revision.getProposable();

        if(proposable != null) {
            if (proposable instanceof Category) {
                View categoryRevisionDetailView = inflater.inflate(R.layout.content_category_detail, proposalScrollView, true);
                TextView nameTextView = (TextView) categoryRevisionDetailView.findViewById(R.id.category_name);
                TextView parentCategoryTextView = (TextView) categoryRevisionDetailView.findViewById(R.id.parent_category);

                fillInTextView(nameTextView, ((Category) currentInfo).getName(), ((Category) proposable).getName());
                if(((Category) proposable).getParentCategory() != null) {
                    parentCategoryTextView.setText(((Category) proposable).getParentCategory().getName());
                }
            } else if (proposable instanceof Place) {
//            inflater.inflate(R.layout.activity_place_detail, (ViewGroup) fragmentView, false);
            }
        } else {
            TextView messageTextView = new TextView(getActivity());
            messageTextView.setText("There are no pending revisions available");
            proposalScrollView.addView(messageTextView);
        }

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onRevisionFragmentClickListener = (OnRevisionFragmentClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((AppCompatActivity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch(resultCode) {
            case Status.SUCCESS:
                onRevisionFragmentClickListener.onFragmentButtonClicked(getView());
                break;
            case Status.FAILURE:
                Snackbar.make(this.getView(),
                              "Something went wrong, please try again.",
                              Snackbar.LENGTH_SHORT)
                        .show();
                approveButton.setBackgroundColor(Color.WHITE);
                disproveButton.setBackgroundColor(Color.WHITE);
                break;
        }
    }

    @OnClick(R.id.approve_button)
    public void approveButtonSubmission() {
        dispatchServiceCall(true);

        approveButton.setBackgroundColor(Color.GREEN);
    }

    @OnClick(R.id.disprove_button)
    public void disproveButtonSubmission() {
        dispatchServiceCall(false);

        disproveButton.setBackgroundColor(Color.RED);
    }

    private void dispatchServiceCall(boolean vote) {
        Class revisionTypeClass = currentInfo.getClass();

        RevisionApi.RevisionApprovalService.startService(
                getActivity(),
                ((Category) currentInfo).getId(),       // awful hack, gross (give me some duck typing and I'll conquer the world)
                revisionTypeClass.getSimpleName(),
                revision.getId(),
                vote
        );
    }

    private void fillInTextView(TextView view, String currentValue, String proposedValue) {
        if(!currentValue.equals(proposedValue)) {
            view.setBackgroundColor(Color.YELLOW);
        }

        view.setText(proposedValue);
    }

    public interface OnRevisionFragmentClickListener{
        void onFragmentButtonClicked(View view);
    }
}
