package com.example.shivamagrawal.photoshareapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.shivamagrawal.photoshareapp.Objects.Group;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ListView listView = (ListView) findViewById(R.id.list_view);
        //ListAdapter adapter = new ListAdapter(this, createList(20));
        //listView.setAdapter(adapter);
    }

    private class ListAdapter extends ArrayAdapter<Group> {
        private final LayoutInflater mInflater;
        private final ViewBinderHelper binderHelper;

        public ListAdapter(Context context, List<Group> objects) {
            super(context, R.layout.group_list_item_layout, objects);
            mInflater = LayoutInflater.from(context);
            binderHelper = new ViewBinderHelper();

            // uncomment if you want to open only one row at a time
            binderHelper.setOpenOnlyOne(true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.group_list_item_layout, parent, false);

                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.group_item_name);
                holder.actionsView = convertView.findViewById(R.id.group_actions);
                holder.swipeLayout = (SwipeRevealLayout) convertView.findViewById(R.id.swipe_layout);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Group item = getItem(position);
            if (item != null) {
                binderHelper.bind(holder.swipeLayout, item.getName());
                holder.textView.setText(item.getName());
                // also do button listeners here
            }

            return convertView;
        }

        /**
         * Only if you need to restore open/close state when the orientation is changed.
         * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
         */
        public void saveStates(Bundle outState) {
            binderHelper.saveStates(outState);
        }

        /**
         * Only if you need to restore open/close state when the orientation is changed.
         * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
         */
        public void restoreStates(Bundle inState) {
            binderHelper.restoreStates(inState);
        }

        private class ViewHolder {
            TextView textView;
            View actionsView;
            SwipeRevealLayout swipeLayout;
        }
    }

}
