package com.example.surajama.tekhealthcare;

import android.animation.Animator;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.surajama.tekhealthcare.services.LoginActivity;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public TextView userID;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private boolean isOpen;
    private Toolbar toolbar;
    private Toolbar topToolbar;
    private LinearLayout logo;
    int scrollRange = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devices);
        userID = findViewById(R.id.userID);
        setUserID();
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("Tek HealthCare");
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);
        topToolbar= findViewById(R.id.topToolbar);
        fab = findViewById(R.id.fab);
        logo = findViewById(R.id.logo);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isOpen = true;
                    viewMenu();
                } else if (isOpen) {
                    isOpen = false;
                    hideMenu();
                }
            }
        });
    }

    public void selectDevice(View view)
    {
        final Intent intent = new Intent(MainActivity.this,DeviceSelection.class);
        intent.putExtra("typeOfDevice",view.getId());
        startActivity(intent);


    }

    public void login(View view) {
        final Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    public void setUserID()
    {
        final Controller aController = (Controller) MainActivity.this.getApplicationContext();
        if(aController.getUserId() == null){
            userID.setText("GUEST");
        }
        else {
            userID.setText(aController.getUserId());
        }
    }
    private void viewMenu() {


            int x = logo.getRight();
            int y = logo.getBottom()+500;

            int startRadius = 0;
            int endRadius = (int) Math.hypot(toolbar.getWidth(), toolbar.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(topToolbar, x, y, startRadius, endRadius);

            topToolbar.setVisibility(View.VISIBLE);
            anim.start();
    }
    private void hideMenu()
    {
        int x = topToolbar.getRight();
        int y = topToolbar.getBottom()+500;

        int startRadius = Math.max(toolbar.getWidth(), toolbar.getHeight());
        int endRadius = 0;

        Animator anim = ViewAnimationUtils.createCircularReveal(topToolbar, x, y, startRadius, endRadius);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                topToolbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();

    }


}
