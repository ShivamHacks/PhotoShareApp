package com.example.shivamagrawal.photoshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.example.shivamagrawal.photoshareapp.Objects.GalleryAdapter;

public class GalleryActivity extends AppCompatActivity {

    Context context;
    LayoutInflater inflater;
    String[] photoURLS = {
            "https://images.unsplash.com/photo-1464740042629-b78a46b310ae",
            "https://images.unsplash.com/photo-1456894332557-b03dc5cf60d5",
            "https://images.unsplash.com/photo-1451479456262-b94f205059be",
            "https://images.unsplash.com/photo-1467094568967-95f87ee9c873",
            "https://images.unsplash.com/photo-1466721591366-2d5fba72006d",
            "https://images.unsplash.com/photo-1468245856972-a0333f3f8293",
            "https://images.unsplash.com/photo-1465284958051-1353268c077d",
            "https://images.unsplash.com/photo-1465232377925-cce9a9d87843",
            "https://images.unsplash.com/photo-1465441494912-68f5747c3fe0",
            "https://images.unsplash.com/photo-1464655646192-3cb2ace7a67e",
            "https://images.unsplash.com/photo-1461295025362-7547f63dbaea",
            "https://images.unsplash.com/photo-1460400408855-36abd76648b9",
            "https://images.unsplash.com/photo-1459197648846-52de5e4d1e9a",
            "https://images.unsplash.com/reserve/Lt0DwxdqRKSQkX7439ey_Chaz_fisheye-11.jpg",
            "https://images.unsplash.com/reserve/unsplash_52ce2b0530dab_1.JPG",
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b",
            "https://images.unsplash.com/photo-1463392898715-3939d4ab1019",
            "https://images.unsplash.com/photo-1460186136353-977e9d6085a1",
            "https://images.unsplash.com/photo-1458571037713-913d8b481dc6",
            "https://images.unsplash.com/photo-1452711932549-e7ea7f129399"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        context = this;

        GridView gridview = (GridView) findViewById(R.id.photo_gridview);
        gridview.setAdapter(new GalleryAdapter(this, photoURLS));

        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Dialog photoDialog = new Dialog(context);
                photoDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.photo_view, null);
                ImageView photo = (ImageView) layout.findViewById(R.id.photo_imageview);
                Glide.with(context).load(photoURLS[position]).into(photo);
                photoDialog.setContentView(layout);
                photoDialog.show();
            }
        });

        gridview.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


    }

}
