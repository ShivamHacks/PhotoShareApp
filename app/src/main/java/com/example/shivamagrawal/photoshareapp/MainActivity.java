package com.example.shivamagrawal.photoshareapp;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shivamagrawal.photoshareapp.Objects.Group;
import com.example.shivamagrawal.photoshareapp.Objects.GroupAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
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

        //init();
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
        StringRequest sr = Server.GET(params, Server.getAllGroupsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject results = new JSONObject(s);
                            JSONArray groupsJSON = results.getJSONArray("groups");
                            for (int i = 0; i < groupsJSON.length(); i++) {
                                JSONObject group = new JSONObject(groupsJSON.get(i).toString());
                                groups.add(new Group(group.getString("_id"), group.getString("groupName")));
                            }
                            groupAdapter.notifyDataSetChanged();
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
                getGroups();
                // TODO: account settings
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
