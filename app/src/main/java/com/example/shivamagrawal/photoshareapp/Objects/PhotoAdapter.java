package com.example.shivamagrawal.photoshareapp.Objects;

import android.support.v4.view.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import java.util.List;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.shivamagrawal.photoshareapp.R;

public class PhotoAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    List<String> photoURLS;

    public PhotoAdapter(Context context, List<String> photoURLS) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.photoURLS = photoURLS;
    }

    @Override
    public int getCount() {
        return photoURLS.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public RelativeLayout instantiateItem(ViewGroup container, int position) {
        RelativeLayout photoView = (RelativeLayout) inflater
                .inflate(R.layout.photo_view, container, false);
        ImageView imageView = (ImageView) photoView.findViewById(R.id.photo_imageview);
        Glide.with(context).load(photoURLS.get(position)).fitCenter().into(imageView);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}