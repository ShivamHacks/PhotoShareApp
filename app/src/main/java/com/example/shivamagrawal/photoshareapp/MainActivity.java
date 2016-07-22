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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

        //ArrayList<Group> savedGroups = savedInstanceState.getParcelableArrayList("groups");
        if (savedInstanceState != null) {
            Log.e("RESTORING onCreate", "LOL");
            groups = savedInstanceState.getParcelableArrayList("groups");
        }
        context = this;

        toolbar = (Toolbar) findViewById(R.id.main_activity_tool_bar);
        setSupportActionBar(toolbar);

        //getGroups(); // Load groups
        groupsList = (ListView) findViewById(R.id.list_groups);
        groupAdapter = new GroupAdapter(this, groups);
        groupsList.setAdapter(groupAdapter);

        //init();
    }

    private void init() {
        SharedPreferences sharedPref = this.getSharedPreferences("main", Context.MODE_PRIVATE);
        boolean loggedIn = sharedPref.getBoolean("loggedIn", false);
        if (!loggedIn) {
            Intent login = new Intent(this, LoginOrSignUpActivity.class);
            startActivity(login);
        } // TODO need to do start activity for result and get whether logged in or not first
    }

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

    // TODO: need to fix this!!!

    @Override
    public void onPause() {
        super.onPause();
        Log.e("PAUSED", "P");
    }
    @Override
    public void onResume() {
        super.onResume();
        getGroups();
        Log.e("RESUME", "R");
    }
    @Override
    public void onRestart() {
        super.onRestart();
        getGroups();
        Log.e("RESTART", "R");
    }
    @Override
    public void onStart() {
        super.onStart();
        getGroups();
        Log.e("START", "S");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("DESTROY", "D");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("groups", groups);
        Log.e("SAVING", "S");
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("RESTORED", "R");
        groups = savedInstanceState.getParcelableArrayList("groups");
    }

    // TODO: App restart, stop, pause, etc handling for all activities
}
