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
    final List<Contact> members;

    public ContactsAdapter(Context context, int layoutResource, List<Contact> items) {
        super(context, layoutResource, items);
        this.layoutResource = layoutResource;
        members = items;

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

    @Override
    public Filter getFilter() {
        return CustomFilter;
    }

    private Filter CustomFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object ResultValue) {
            return ((Contact) ResultValue).getNumber();
        }

        /*
        So it seems that every iteration of filtering reduces members to only filters that satisfied the pervious result
        So a mistype reduces all potential results for all following characters
         */

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            List<Contact> allMembers = new ArrayList<Contact>(members);

            if(constraint == null || constraint.length() == 0){
                results.values = allMembers;
                results.count = allMembers.size();
            } else {
                final ArrayList<Contact> NewValues = new ArrayList<Contact>();

                for(Contact contact : allMembers){
                    if (contact.getNumber().indexOf(constraint.toString()) != -1) {
                        NewValues.add(contact);
                    }
                }
                results.values = NewValues;
                results.count = NewValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence Constraint, FilterResults results) {
            clear();
            if(results.count > 0) {
                addAll((ArrayList<Contact>) results.values);
            }
            notifyDataSetInvalidated();
        }
    };

}
