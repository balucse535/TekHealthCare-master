package com.example.surajama.tekhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotLayout;
    private SliderAdapter sliderAdapter;
    private TextView dots[];
    private int currentPage;
    private Button finishBtn;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_home);
        viewPager = findViewById(R.id.viewer);
        dotLayout = findViewById(R.id.dotLayout);
        sliderAdapter = new SliderAdapter(this);
        finishBtn = findViewById(R.id.finish_btn);
        viewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewLisitner);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
    public void addDotsIndicator(int position){
        dots = new TextView[4];
        dotLayout.removeAllViews();

        for(int i=0;i<dots.length;i++)
        {
           dots[i] = new TextView(this);
           dots[i].setTextSize(40);
           dots[i].setPadding(25,25,25,25);
           dots[i].setText(Html.fromHtml("&#8226;"));
           dots[i].setTextColor(getResources().getColor(R.color.transperent));
           dotLayout.addView(dots[i]);
        }
        if(dots.length>0)
        {
            dots[position].setTextColor(getResources().getColor(R.color.white_70));
        }
    }
    ViewPager.OnPageChangeListener viewLisitner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage =position;
            dots[position].setTextColor(getResources().getColor(R.color.brown));
            if (position==dots.length-1)
            {
                finishBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
