package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.app.Dialog;
import android.view.Window;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.shivamagrawal.photoshareapp.Objects.GalleryAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.Group;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GalleryActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context context;
    String groupID;
    LayoutInflater inflater;

    GalleryAdapter galleryAdapter;
    List<String> urls = new ArrayList<String>();
    boolean loadingURLS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        context = this;

        toolbar = (Toolbar) findViewById(R.id.gallery_activity_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        groupID = extras.getString("groupID");
        getSupportActionBar().setTitle(extras.getString("groupName"));

        GridView gridview = (GridView) findViewById(R.id.photo_gridview);
        galleryAdapter = new GalleryAdapter(this, urls);
        gridview.setAdapter(galleryAdapter);

        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO: need to fix this
                Dialog photoDialog = new Dialog(context);
                photoDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.photo_view, null);
                ImageView photo = (ImageView) layout.findViewById(R.id.photo_imageview);
                Glide.with(context).load(urls.get(position)).into(photo);
                photoDialog.setContentView(layout);
                photoDialog.show();
            }
        });

        init();

    }

    private void init() {

        // Get Photo URLS
        Map<String, String> params = new HashMap<String, String>();
        StringRequest sr = Server.GET(params, Server.getAllPhotosURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject results = new JSONObject(s);
                            Log.d("RES", s);
                            JSONArray urlsJSON = results.getJSONArray("photoURLS");
                            for (int i = 0; i < urlsJSON.length(); i++)
                                urls.add(urlsJSON.get(i).toString());
                            galleryAdapter.notifyDataSetChanged();
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

}
