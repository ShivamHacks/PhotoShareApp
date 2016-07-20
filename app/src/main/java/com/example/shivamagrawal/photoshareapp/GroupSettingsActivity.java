package com.example.shivamagrawal.photoshareapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.shivamagrawal.photoshareapp.Objects.Contact;
import com.example.shivamagrawal.photoshareapp.Objects.ContactsAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.GalleryAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.GetContacts;
import com.example.shivamagrawal.photoshareapp.Objects.Server;
import com.example.shivamagrawal.photoshareapp.Objects.ViewHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupSettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout membersListLayout;
    LayoutInflater inflater;
    Button addButton;
    Button submitButton;
    EditText groupName;
    Context context;

    /*
    Settings that can be changed: add members, delete members (can't edit members) and group name
     */

    /*
    Loads existing group name (which is edittable) and existing members. Can add members or delete members
     */

    List<String> currentMembers = new ArrayList<String>(); // current members
    List<String> removedMembers = new ArrayList<String>(); // removed members
    List<String> addedMembers = new ArrayList<String>(); // addedMembers

    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        context = this;
        // Retrieve variables TODO

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.group_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get group data
        getGroupData();

        // Existing members
        ListView existingMembers = (ListView) findViewById(R.id.editgroup_existing_members_listview);
        existingMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // do something
            }
        });
        ArrayAdapter<String> membersAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, currentMembers);
        existingMembers.setAdapter(membersAdapter);
        ViewHelper.justifyListViewHeightBasedOnChildren(existingMembers);

        // New Members
        membersListLayout = (LinearLayout) findViewById(R.id.editgroup_members_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addButton = (Button) findViewById(R.id.editgroup_add_member_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membersListLayout.addView(createACTV());
            }
        });

        // Finished Button
        submitButton = (Button) findViewById(R.id.editgroup_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        //init();
    }

    private void getGroupData() {
        currentMembers.add("212312");
        currentMembers.add("390832");
        currentMembers.add("091323");
    }

    private AutoCompleteTextView createACTV() {
        AutoCompleteTextView memberET = (AutoCompleteTextView) inflater.inflate(R.layout.add_member_edittext, null);
        ContactsAdapter adapter = new ContactsAdapter(context, R.layout.search_contacts_item, GetContacts.get(context));
        memberET.setAdapter(adapter);
        return memberET;
    }


    private void submit() {
        // check if group name has changed (via variable comparison - name on activity create vs edittext content?
        // TODO need to do this

        // TODO: when submitting, need to convert array to string
        /*List<String[]> phoneNumbers = new ArrayList<String[]>();
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
        queue.add(sr);*/
    }

}
