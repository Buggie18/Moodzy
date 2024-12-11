package com.example.moodzy;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(MainActivity.this);
        setContentView(R.layout.activity_main);
        View rootView = findViewById(android.R.id.content);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f);
        fadeIn.setDuration(2000);
        fadeIn.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        WindowInsetsController controller = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            controller = getWindow().getInsetsController();
        }

        if (controller != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.hide(WindowInsets.Type.navigationBars());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        View roundedRectangle = findViewById(R.id.imageView);
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(roundedRectangle, "translationY", -2000f, 0f);
        slideDown.setDuration(1500);
        slideDown.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recyclerView = (RecyclerView) findViewById(R.id.mood_view);
                String[] moodTitles = getResources().getStringArray(R.array.mood_titles);
                TypedArray moodImages = getResources().obtainTypedArray(R.array.mood_images);

                SpanningLinearLayoutManager layoutManager = new SpanningLinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, moodTitles, moodImages);
                recyclerView.setAdapter(adapter);
            }
        }, 1500);
    }
    }