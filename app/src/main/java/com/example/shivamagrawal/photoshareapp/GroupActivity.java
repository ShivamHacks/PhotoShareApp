package com.example.shivamagrawal.photoshareapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

public class GroupActivity extends AppCompatActivity {

    Toolbar toolbar;
    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        toolbar = (Toolbar) findViewById(R.id.group_activity_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // todo: use savedInstanceState later
        Bundle extras = getIntent().getExtras();
        groupID = extras.getString("groupID");
        getSupportActionBar().setTitle(extras.getString("groupName"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_add:
                return true;
            case R.id.action_main_settings:
                // access account settings from here
                // in eventactivity, settings button leads to group settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
