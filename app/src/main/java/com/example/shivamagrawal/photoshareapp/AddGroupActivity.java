package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import android.telephony.PhoneNumberUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shivamagrawal.photoshareapp.Objects.ContactsAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.Contact;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

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
    EditText groupName;
    Context context;
    String countryISO; // ISO of user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        context = this;

        membersListLayout = (LinearLayout) findViewById(R.id.members_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addButton = (Button) findViewById(R.id.add_member_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membersListLayout.addView(createACTV());
            }
        });

        submitButton = (Button) findViewById(R.id.submit_new_group_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        groupName = (EditText) findViewById(R.id.editText_group_name);

        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = tm.getSimCountryIso();

    }

    // TODO: fix this so that I do not have to create new contact list every time new edittext created
    // The reason this is currently how it is: Each edittext's total filtering pool is based on the previous edittext's filters
    // Which means that each consecutive edittext's filtering became worse
    private AutoCompleteTextView createACTV() {
        AutoCompleteTextView memberET = (AutoCompleteTextView) inflater.inflate(R.layout.add_member_edittext, null);
        ContactsAdapter adapter = new ContactsAdapter(context, R.layout.search_contacts_item, getContacts(context));
        memberET.setAdapter(adapter);
        return memberET;
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
        // TODO: when submitting, need to convert array to string
        List<String[]> phoneNumbers = new ArrayList<String[]>();
        for (int i = 0; i < membersListLayout.getChildCount(); i++) {
            EditText memberET = (EditText) membersListLayout.getChildAt(i);
            String member = PhoneNumberUtils.normalizeNumber(memberET.getText().toString());
            String[] numberInfo = { member, null };
            if (member.indexOf("+") == -1) { // not international number
                numberInfo[1] = countryISO;
            }
            phoneNumbers.add(numberInfo);
            Log.d("MEMBER", member);
        }
        String name = groupName.getText().toString();

        // Send stuff
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        for (int j = 0; j < phoneNumbers.size(); j++) {
            params.put("members[" + j + "]", Arrays.toString(phoneNumbers.get(j)));
        }
        // Post to server
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = Server.POST(params, Server.createGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        );
        queue.add(sr);
    }
}
