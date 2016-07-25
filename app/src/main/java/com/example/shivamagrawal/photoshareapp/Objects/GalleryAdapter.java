package com.example.shivamagrawal.photoshareapp.Objects;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.GridView;

import java.util.List;
import com.bumptech.glide.Glide;

import android.view.WindowManager;
import android.view.Display;
import android.graphics.Point;

import android.util.Log;

public class GalleryAdapter extends BaseAdapter {

    private Context context;
    private List<String> photoURLs;

    int width;
    int height;

    public GalleryAdapter(Context context, List<String> photoURLs) {
        this.context = context;
        this.photoURLs = photoURLs;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    public int getCount() {
        return photoURLs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(width/3, width/3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d("LOADING", photoURLs.get(position));
        Glide.with(context).load(photoURLs.get(position)).centerCrop().into(imageView);
        return imageView;
    }

}
