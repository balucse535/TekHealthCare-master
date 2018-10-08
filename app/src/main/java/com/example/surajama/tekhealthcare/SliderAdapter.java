package com.example.surajama.tekhealthcare;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context)
    {
        this.context = context;
    }
    public int[] slide_images= {R.drawable.trackers,R.drawable.ehr,R.drawable.iot,R.drawable.sf_healthcloud};
    public String[] slide_headings = {"Health Devices","EMR/EHR","IoT","SalesForce Health Cloud"};
    public String[] slide_desc ={"Our Framework uses Health Device like Smart Watch Bp Monitor, Weighing Scales to get the patient Non clincal data.Like Heart Beate rate, Steps Count, Blood Pressure , Height and Weight","EMR","IoT","SalesForce"};
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDesc = view.findViewById(R.id.slide_desc);
        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDesc.setText(slide_desc[position]);
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container,int position,Object object)
    {
        container.removeView((RelativeLayout)object);
    }

}
