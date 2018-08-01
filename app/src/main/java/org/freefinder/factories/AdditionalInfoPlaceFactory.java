package org.freefinder.factories;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.freefinder.model.AdditionalField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rade on 14.11.17..
 */

public class AdditionalInfoPlaceFactory {
    public enum Field {
        TEXT_FIELD("Text"),
        CHECKBOX("Checkbox");

        private String fieldType;

        Field(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getFieldType() {
            return fieldType;
        }

        private static final Map<String, Field> lookup = new HashMap<>();

        static
        {
            for(Field field : Field.values())
            {
                lookup.put(field.getFieldType(), field);
            }
        }

        public static Field get(String fieldType)
        {
            return lookup.get(fieldType);
        }
    }

    public static View createView(Context context, AdditionalField additionalField) {
        Field field = Field.get(additionalField.getFieldType());

        switch(field) {
            case TEXT_FIELD:
                return createEditText(context, additionalField.getName());
            case CHECKBOX:
                return createCheckbox(context, additionalField.getName());
        }

        throw new IllegalArgumentException("Field " + field.getFieldType() + " was not recognized");
    }

    private static EditText createEditText(Context context, String hint) {
        EditText editText = new EditText(context);
        editText.setHint(hint);

        return editText;
    }

    private static LinearLayout createCheckbox(Context context, String propertyName) {
        LinearLayout checkboxLayout = new LinearLayout(context);

        TextView propertyNameTextView = new TextView(context);
        propertyNameTextView.setText(propertyName);

        CheckBox checkBox = new CheckBox(context);

        checkboxLayout.addView(propertyNameTextView);
        checkboxLayout.addView(checkBox);

        return checkboxLayout;
    }
}
