package org.freefinder.builders;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by rade on 8.3.17..
 */
public class ToolbarBuilder {
    private AppCompatActivity activity;
    private View view;
    private Toolbar toolbar;

    public ToolbarBuilder(AppCompatActivity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    public ToolbarBuilder registerToolbar(final int toolbarId) {
        this.toolbar = (Toolbar) view.findViewById(toolbarId);
        activity.setSupportActionBar(toolbar);

        return this;
    }

    public ToolbarBuilder withTitle(final int titleId) {
        this.toolbar.setTitle(titleId);
        return this;
    }

    public ToolbarBuilder withUpButton() {
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return this;
    }
}
