package com.click.gaurav.app;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    Map<String, Bitmap> s = new HashMap<>();
    ArrayList<ImageAdapterPojo> imageAdapterPojos;
    public ArrayList<ImageAdapterPojo> deletedapterPojos = new ArrayList<>();
    ImageAdapter adapter = this;
    private Animator mCurrentAnimator;
    int count;
    ImageView imageView;
    View thumb1View;

    public ImageAdapter(Context c, ArrayList<ImageAdapterPojo> imageAdapterPojos, View thumb1View) {
        mContext = c;
        this.imageAdapterPojos = imageAdapterPojos;
        this.thumb1View = thumb1View;
    }

    public int getCount() {
        return imageAdapterPojos.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        count = position;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 8, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(imageAdapterPojos.get(position).getBitmap());
        imageView.setTag(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Delete selected image
                ImageAdapterPojo imageAdapterPojo = imageAdapterPojos.get(position);
                imageAdapterPojos.remove(position);
                deletedapterPojos.add(imageAdapterPojo);
                adapter.notifyDataSetChanged();
            }
        });

        return imageView;
    }
}
