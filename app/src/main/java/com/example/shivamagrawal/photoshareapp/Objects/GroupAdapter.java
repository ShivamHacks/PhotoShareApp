package com.example.shivamagrawal.photoshareapp.Objects;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Context;
import java.util.List;
import com.example.shivamagrawal.photoshareapp.R;

public class GroupAdapter extends ArrayAdapter<Group> {

    private int layoutResource;

    public GroupAdapter(Context context, int layoutResource, List<Group> items) {
        super(context, layoutResource, items);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Group group = getItem(position);

        if (group != null) {
            TextView groupName = (TextView) view.findViewById(R.id.group_item_name);
            if (groupName != null) {
                groupName.setText(group.getName());
            }
        }

        return view;
    }

}
