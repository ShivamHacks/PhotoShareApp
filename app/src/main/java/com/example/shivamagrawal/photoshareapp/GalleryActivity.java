package com.example.shivamagrawal.photoshareapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.app.Dialog;
import android.view.Window;
import android.widget.TextView;
import android.view.WindowManager.LayoutParams;
import android.content.pm.ActivityInfo;

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
import com.example.shivamagrawal.photoshareapp.Objects.PhotoAdapter;
import com.example.shivamagrawal.photoshareapp.Objects.PhotoFragment;
import com.example.shivamagrawal.photoshareapp.Objects.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GalleryActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context context;
    String groupID;
    LayoutInflater inflater;
    TextView noImagesTV;

    GalleryAdapter galleryAdapter;
    ArrayList<String> urls = new ArrayList<String>();
    boolean loadingURLS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

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

        inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        noImagesTV = (TextView) findViewById(R.id.noimages_textview);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (!loadingURLS) showFragment(position);
            }
        });

        init();

    }

    private void showFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("photoURLs", urls);
        bundle.putInt("position", position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PhotoFragment pager = new PhotoFragment();
        pager.setArguments(bundle);
        ft.add(pager, null).commit();
    }

    private void init() {

        // Get Photo URLS
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", Server.getToken(context));
        params.put("groupID", groupID);
        StringRequest sr = Server.GET(params, Server.getAllPhotosURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject results = new JSONObject(s);
                            Log.d("RES", s);
                            JSONArray urlsJSON = results.getJSONArray("photoURLS");
                            if (urlsJSON.length() == 0) {
                                noImagesTV.setText("No Images in this group yet");
                            } else {
                                noImagesTV.setVisibility(View.GONE);
                                for (int i = 0; i < urlsJSON.length(); i++) {
                                    Log.d("URL", urlsJSON.get(i).toString());
                                    urls.add(urlsJSON.get(i).toString());
                                }
                                galleryAdapter.notifyDataSetChanged();
                                loadingURLS = false;
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

}
