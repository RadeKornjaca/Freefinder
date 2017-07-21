package org.freefinder.adapters;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.model.Category;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rade on 12.7.17..
 */

public class CategoryAdapter extends RealmBaseAdapter<Category> implements ListAdapter {

    private static class ViewHolder {
        TextView categoryText;
        CheckBox deleteCheckBox;
    }

    private boolean inDeletionMode = false;
    private Set<String> countersToDelete = new HashSet<String>();

    public CategoryAdapter(OrderedRealmCollection<Category> realmResults) {
        super(realmResults);
    }

    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    Set<String> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.categoryText = (TextView) convertView.findViewById(R.id.one_element_row);
            // viewHolder.deleteCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Category item = (Category) adapterData.get(position);
            viewHolder.categoryText.setText(item.getName());
//            if (inDeletionMode) {
//                viewHolder.deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        countersToDelete.add(item.getName());
//                    }
//                });
//            } else {
//                viewHolder.deleteCheckBox.setOnCheckedChangeListener(null);
//            }
//            viewHolder.deleteCheckBox.setChecked(countersToDelete.contains(item.getName()));
//            viewHolder.deleteCheckBox.setVisibility(inDeletionMode ? View.VISIBLE : View.GONE);
        }
        return convertView;
    }
}
