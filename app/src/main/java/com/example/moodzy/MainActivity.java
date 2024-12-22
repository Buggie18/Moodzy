package com.example.moodzy;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.app.AlertDialog;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    HashMap<Integer, String> emojiMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(MainActivity.this);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        emojiMap = new HashMap<>();
        emojiMap.put(0, "happy");
        emojiMap.put(1, "cool");
        emojiMap.put(2, "romantic");
        emojiMap.put(3, "excited");
        emojiMap.put(4, "surprised");
        emojiMap.put(5, "neutral");
        emojiMap.put(6, "angry");
        emojiMap.put(7, "sad");
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
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("No internet connection. Please check your connection and try again.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return;
                }
                int selectedPosition = ((RecyclerViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).selectedPosition;
                if (selectedPosition == -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Please select an option before submitting.");
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    builder.show();
                    return;
                }
                String emoji = emojiMap.get(selectedPosition);
                assert emoji != null;
                DocumentReference emojiRef = db.collection("emojis").document(emoji);
                emojiRef.update("count", FieldValue.increment(1))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ((RecyclerViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).resetSelection();
                                Log.d("Firestore", "Document updated successfully");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Your response has been recorded. Thank you!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Error updating count", e);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Oops! Some problem has occured in recording your response. Please try again.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        });
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }
}