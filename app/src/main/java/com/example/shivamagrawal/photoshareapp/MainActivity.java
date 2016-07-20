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
import android.widget.AdapterView;
import android.widget.ListView;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

import com.example.shivamagrawal.photoshareapp.Objects.Group;
import com.example.shivamagrawal.photoshareapp.Objects.GroupAdapter;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    SwipeRefreshLayout srl;
    ListView groupsList;
    GroupAdapter groupAdapter;
    List<Group> groups;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Intent gallery = new Intent(context, GalleryActivity.class);
        startActivity(gallery);

        toolbar = (Toolbar) findViewById(R.id.main_activity_tool_bar);
        setSupportActionBar(toolbar);

        getGroups(); // Load groups
        groupsList = (ListView) findViewById(R.id.list_groups);
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent group = new Intent(context, GroupActivity.class);
                startActivity(group);
            }
        });
        groupAdapter = new GroupAdapter(this, R.layout.group_item_layout, groups);
        groupsList.setAdapter(groupAdapter);

        srl = (SwipeRefreshLayout) findViewById(R.id.list_groups_swipeContainer);
        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d("REFRESH", "onRefresh called from SwipeRefreshLayout");
                        updateGroups();
                    }
                }
        );
        srl.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA);

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
        groups = new ArrayList<Group>();
        groups.add(new Group("1", "NAME 1"));
        groups.add(new Group("2", "NAME 2"));
        groups.add(new Group("3", "NAME 3"));
        groups.add(new Group("4", "NAME 4"));
        groups.add(new Group("5", "NAME 5"));
        groups.add(new Group("6", "NAME 6"));
        groups.add(new Group("7", "NAME 7"));
        groups.add(new Group("8", "NAME 8"));
        groups.add(new Group("9", "NAME 9"));
    }

    private void updateGroups() {
        // do updating
        groupAdapter.notifyDataSetChanged();
        srl.setRefreshing(false);
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
                // access account settings from here
                // in eventactivity, settings button leads to group settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save stuff
        // savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state
        // mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
    }

    // TODO: App restart, stop, pause, etc handling for all activities
}
