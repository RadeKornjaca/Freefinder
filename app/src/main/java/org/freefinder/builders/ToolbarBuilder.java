package org.freefinder.builders;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by rade on 8.3.17..
 */
public class ToolbarBuilder {
    private Context context;
    private View view;
    private Toolbar toolbar;

    public ToolbarBuilder(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public ToolbarBuilder registerToolbar(final int toolbarId) {
        this.toolbar = (Toolbar) view.findViewById(toolbarId);
        ((AppCompatActivity) context).setSupportActionBar(toolbar);

        return this;
    }

    public ToolbarBuilder withTitle(final int titleId) {
        this.toolbar.setTitle(titleId);
        return this;
    }

    public ToolbarBuilder withUpButton() {
        ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return this;
    }
}
