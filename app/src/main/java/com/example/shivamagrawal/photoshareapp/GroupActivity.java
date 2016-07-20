package com.example.shivamagrawal.photoshareapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.content.Context;

import com.example.shivamagrawal.photoshareapp.Objects.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    Toolbar toolbar;
    String groupID;
    Context context;
    ListView eventsList;
    SwipeRefreshLayout srl;
    List<String> events; // TODO: list should be List<Event> later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.group_activity_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // todo: use savedInstanceState later
        Bundle extras = getIntent().getExtras();
        groupID = extras.getString("groupID");
        getSupportActionBar().setTitle(extras.getString("groupName"));

        getEvents(); // Load events
        eventsList = (ListView) findViewById(R.id.list_events);
        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog eventDialog = new Dialog(context);
                eventDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.event_actions_view, null);
                //Button joinEventButton = (Button) layout.findViewById(R.id.eventactions_joinevent);
                Button viewPhotosButton = (Button) layout.findViewById(R.id.eventactions_viewphotos);
                //Button renameButton = (Button) layout.findViewById(R.id.eventactions_renameevent);

                viewPhotosButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent gallery = new Intent(context, GalleryActivity.class);
                        startActivity(gallery);
                    }
                });

                eventDialog.setContentView(layout);
                eventDialog.show();
            }
        });
        // TODO: make a custom adapter
        ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, events);
        eventsList.setAdapter(eventsAdapter);

        srl = (SwipeRefreshLayout) findViewById(R.id.list_groups_swipeContainer);
        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d("REFRESH", "onRefresh called from SwipeRefreshLayout");
                    }
                }
        );
        srl.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA);
    }

    private void getEvents() {
        events = new ArrayList<String>();
        events.add("Event 1");
        events.add("Event 2");
        events.add("Event 3");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_group_add:
                return true;
            case R.id.action_group_settings:
                Intent settings = new Intent(this, GroupSettingsActivity.class);
                startActivity(settings);
                // access account settings from here
                // in eventactivity, settings button leads to group settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
