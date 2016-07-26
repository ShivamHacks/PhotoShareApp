package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shivamagrawal.photoshareapp.Objects.Group;
import com.example.shivamagrawal.photoshareapp.Objects.GroupAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.ResponseHandler;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView noGroupsTV;
    ListView groupsList;
    GroupAdapter groupAdapter;
    ArrayList<Group> groups = new ArrayList<Group>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.main_activity_tool_bar);
        setSupportActionBar(toolbar);

        groupsList = (ListView) findViewById(R.id.list_groups);
        groupAdapter = new GroupAdapter(this, groups);
        groupsList.setAdapter(groupAdapter);

        noGroupsTV = (TextView) findViewById(R.id.nogroup_textview);

        init();
    }

    private void init() {
        SharedPreferences sharedPref = this.getSharedPreferences("main", Context.MODE_PRIVATE);
        boolean loggedIn = sharedPref.getBoolean("loggedIn", false);
        if (!loggedIn) {
            Intent loginOrSignUp = new Intent(this, LoginOrSignUpActivity.class);
            startActivity(loginOrSignUp);
        }
    }

    // CURRENT SYSTEM: loads groups every time activity launched/recreated
    private void getGroups() {
        groups.clear();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", Server.getToken(context));
        StringRequest sr = Server.GET(params, Server.getAllGroupsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        JSONObject body = new ResponseHandler(context, res).parseRes();
                        if (body != null) changeGroupList(body);
                        else ResponseHandler.errorToast(context, "An error occured");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ResponseHandler.errorToast(context, "An error occured");
                    }
                }
        );
        Server.makeRequest(context, sr);
    }

    private void changeGroupList(JSONObject body) {
        try {
            JSONArray groupsJSON = body.getJSONArray("groups");
            if (groupsJSON.length() == 0) {
                noGroupsTV.setText("No Groups. Create one by clicking the plus button");
            } else {
                noGroupsTV.setVisibility(View.GONE);
                for (int i = 0; i < groupsJSON.length(); i++) {
                    JSONObject group = new JSONObject(groupsJSON.get(i).toString());
                    groups.add(new Group(group.getString("groupID"), group.getString("groupName")));
                }
            }
            groupAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            ResponseHandler.errorToast(context, "An error occured");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_add:
                Intent addGroup = new Intent(this, AddGroupActivity.class);
                startActivity(addGroup);
                return true;
            case R.id.action_main_settings:
                Intent accountSettings = new Intent(this, AccountSettingsActivity.class);
                startActivity(accountSettings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getGroups();
    }

}
