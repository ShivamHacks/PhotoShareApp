package com.example.shivamagrawal.photoshareapp.Objects;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Context;
import android.widget.Filter;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;
import com.example.shivamagrawal.photoshareapp.R;


public class ContactsAdapter extends ArrayAdapter<Contact> {

    private int layoutResource;

    public ContactsAdapter(Context context, int layoutResource, List<Contact> items) {
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

        Contact contact = getItem(position);
        if (contact != null) {
            TextView contactName = (TextView) view.findViewById(R.id.search_contacts_name);
            TextView contactNumber = (TextView) view.findViewById(R.id.search_contacts_number);
            contactName.setText(contact.getName());
            contactNumber.setText(contact.getNumber());
        }

        return view;
    }

}
