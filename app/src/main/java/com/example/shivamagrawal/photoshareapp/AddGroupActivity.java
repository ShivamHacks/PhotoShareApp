package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.util.Log;

import android.database.Cursor;
import android.provider.ContactsContract;
import com.example.shivamagrawal.photoshareapp.Objects.ContactsAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.Contact;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {

    // Use this to add group and edit group
    // So just return group data and use this activity to start it for result
    // NOt doing multicompeltetextview b/c want it to be easier to edit numbers
    // TODO: display name? And then allow for only numbers?? Need to make it more dynamic.
    // FORNOW: Sticking to numbers only

    LinearLayout membersListLayout;
    LayoutInflater inflater;
    Button addButton;
    Button submitButton;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        context = this;

        membersListLayout = (LinearLayout) findViewById(R.id.members_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final List<Contact> contacts = getContacts(this);

        addButton = (Button) findViewById(R.id.add_member_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView memberET = (AutoCompleteTextView) inflater.inflate(R.layout.add_member_edittext, null);
                ContactsAdapter adapter = new ContactsAdapter(context, R.layout.search_contacts_item, contacts);
                memberET.setAdapter(adapter);
                membersListLayout.addView(memberET);
            }
        });

        submitButton = (Button) findViewById(R.id.submit_new_group_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submit();
            }
        });

    }

    public List<Contact> getContacts(Context cntx) {
        // http://techblogon.com/read-multiple-phone-numbers-from-android-contacts-list-programmatically/
        List<Contact> contacts = new ArrayList<Contact>();
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount();
        if (contactsCount > 0) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCursor.moveToNext()) {
                        //int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phoneNo 	= pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(new Contact(contactName, phoneNo));
                        //numbers.add(phoneNo);
                    }
                    pCursor.close();
                }
            }
            cursor.close();
        }
        return contacts;
    }

    private void submit() {
        for (int i = 0; i < membersListLayout.getChildCount(); i++) {
            EditText member = (EditText) membersListLayout.getChildAt(i);
            //members.add(member.getText().toString());
            Log.d("MEMBER", member.getText().toString());
        }
    }
}
