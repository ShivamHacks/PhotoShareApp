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
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.shivamagrawal.photoshareapp.Objects.Contact;
import com.example.shivamagrawal.photoshareapp.Objects.ContactsAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.GetContacts;
import com.example.shivamagrawal.photoshareapp.Objects.Group;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupSettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout membersListLayout;
    LayoutInflater inflater;
    Button addMembersButton;
    Button submitButton;
    EditText groupNameET;
    Context context;

    String groupName;

    /*
    Settings that can be changed: add members, delete members (can't edit members) and group name
     */

    /*
    Loads existing group name (which is edittable) and existing members. Can add members or delete members
     */

    List<String> currentMembers = new ArrayList<String>(); // current members
    ListView existingMembers;
    ArrayAdapter<String> membersAdapter;

    String groupID;
    String countryISO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        context = this;

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.group_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Group Name
        groupNameET = (EditText) findViewById(R.id.group_name_edittext);

        // Get Launch data
        Bundle extras = getIntent().getExtras();
        groupID = extras.getString("groupID");
        groupName = extras.getString("groupName");
        getSupportActionBar().setTitle(groupName);
        groupNameET.setText(groupName);

        // Get group data
        getGroupData();

        // Existing members
        existingMembers = (ListView) findViewById(R.id.editgroup_existing_members_listview);
        membersAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, currentMembers);
        existingMembers.setAdapter(membersAdapter);
        justifyListViewHeightBasedOnChildren(existingMembers);

        // New Members
        membersListLayout = (LinearLayout) findViewById(R.id.groupsettings_add_members_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addMembersButton = (Button) findViewById(R.id.groupsettings_addmembers_button);
        addMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (membersListLayout.getChildCount() == 0) {
                    membersListLayout.addView(createACTV());
                } else {
                    AutoCompleteTextView lastACTV = (AutoCompleteTextView) membersListLayout
                            .getChildAt(membersListLayout.getChildCount() - 1);
                    if (TextUtils.isEmpty(lastACTV.getText().toString().trim())) { lastACTV.requestFocus(); }
                    else {
                        AutoCompleteTextView newACTV = createACTV();
                        newACTV.requestFocus();
                        membersListLayout.addView(newACTV);
                    }
                }
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

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = tm.getSimCountryIso();

        //init();
    }

    private void getGroupData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupID", groupID);
        // TODO: put token and groupID in params
        StringRequest sr = Server.GET(params, Server.getGroupInfoURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Log.d("RES", s);
                            JSONObject results = new JSONObject(s);
                            JSONArray membersJSON = results.getJSONArray("members");
                            Log.d("MEMBERS", membersJSON.toString());
                            for (int i = 0; i < membersJSON.length(); i++) {
                                Log.d("MEMBER", membersJSON.getString(i));
                                currentMembers.add(membersJSON.getString(i));
                            }
                            Log.d("SIZE", currentMembers.size() + "");
                            membersAdapter.notifyDataSetChanged();
                            justifyListViewHeightBasedOnChildren(existingMembers);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("ERR", "ERR");
                    }
                }
        );
        Server.makeRequest(context, sr);
    }

    private AutoCompleteTextView createACTV() {
        AutoCompleteTextView memberET = (AutoCompleteTextView) inflater.inflate(R.layout.add_member_edittext, null);
        memberET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    AutoCompleteTextView lastACTV = (AutoCompleteTextView) membersListLayout
                            .getChildAt(membersListLayout.getChildCount() - 1);
                    if (TextUtils.isEmpty(lastACTV.getText().toString().trim())) { lastACTV.requestFocus(); }
                    else {
                        AutoCompleteTextView newACTV = createACTV();
                        newACTV.requestFocus();
                        membersListLayout.addView(newACTV);
                    }
                    handled = true;
                }
                return handled;
            }
        });
        ContactsAdapter adapter = new ContactsAdapter(context);
        memberET.setAdapter(adapter);
        return memberET;
    }

    private void justifyListViewHeightBasedOnChildren (ListView listView) {
        // http://stackoverflow.com/questions/12212890/disable-listview-scrolling
        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    private void submit() {
        // Define params
        Map<String, String> params = new HashMap<String, String>();

        // Put params if valid
        if (!(groupName.equals(groupNameET.getText().toString())) &&
                !(TextUtils.isEmpty(groupNameET.getText().toString())))
            params.put("groupName", groupNameET.getText().toString());
        if (membersListLayout.getChildCount() > 0) {
            List<String> phoneNumbers = new ArrayList<String>();
            for (int i = 0; i < membersListLayout.getChildCount(); i++) {
                EditText memberET = (EditText) membersListLayout.getChildAt(i);
                String unFormatted = memberET.getText().toString().replaceAll("<.*?>", ""); // remove name
                String member = PhoneNumberUtils.normalizeNumber(unFormatted);
                if (!phoneNumbers.contains(member) && !currentMembers.contains(member))
                    phoneNumbers.add(member);
            }
            for (int j = 0; j < phoneNumbers.size(); j++)
                params.put("newMembers[" + j + "]", phoneNumbers.get(j));
            params.put("countryISO", countryISO);
        }

        // Post to server
        // params.put("token", Server.getToken(context)); --> Always do this
        StringRequest sr = Server.POST(params, Server.editGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("RES", s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("ERR", "ERR");
                    }
                }
        );
        Server.makeRequest(context, sr);

    }

}
