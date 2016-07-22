package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.shivamagrawal.photoshareapp.Objects.GetContacts;
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

    Toolbar toolbar;
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

        toolbar = (Toolbar) findViewById(R.id.add_group_activity_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        membersListLayout = (LinearLayout) findViewById(R.id.members_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        membersListLayout.addView(createACTV()); // create First ACTV

        submitButton = (Button) findViewById(R.id.submit_new_group_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        groupName = (EditText) findViewById(R.id.editText_group_name);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = tm.getSimCountryIso();

    }

    // TODO: fix this so that I do not have to create new contact list every time new edittext created
    // The reason this is currently how it is: Each edittext's total filtering pool is based on the previous edittext's filters
    // Which means that each consecutive edittext's filtering became worse
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
        ContactsAdapter adapter = new ContactsAdapter(context, GetContacts.get((context)));
        memberET.setAdapter(adapter);
        return memberET;
    }

    private void submit() {
        // Define params
        Map<String, String> params = new HashMap<String, String>();

        // Put params if valid
        if (!(TextUtils.isEmpty(groupName.getText().toString())))
            params.put("groupName", groupName.getText().toString());
        if (membersListLayout.getChildCount() > 0) {
            List<String> phoneNumbers = new ArrayList<String>();
            for (int i = 0; i < membersListLayout.getChildCount(); i++) {
                EditText memberET = (EditText) membersListLayout.getChildAt(i);
                String member = PhoneNumberUtils.normalizeNumber(memberET.getText().toString());
                phoneNumbers.add(member);
            }
            for (int j = 0; j < phoneNumbers.size(); j++)
                params.put("members[" + j + "]", phoneNumbers.get(j));
            params.put("countryISO", countryISO);
        }

        // Post to server
        StringRequest sr = Server.POST(params, Server.createGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        /*try {
                            JSONObject result = new JSONObject(s);
                            if (result.getBoolean("success")) {
                                // save group ID here
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        Log.d("SUC", "SUC");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.d("ERR", "ERR");
                    }
                }
        );
        Server.makeRequest(context, sr);
    }
}
