package com.spark005apps.magicstory.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.spark005apps.magicstory.R;
import com.spark005apps.magicstory.Util.NetworkRequest;
import com.spark005apps.magicstory.Util.SharedPreferencesManager;
import com.spark005apps.magicstory.Util.WritingAnimViewModel;
import com.spark005apps.magicstory.databinding.ActivityImageBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ImageActivity extends AppCompatActivity {
    private Handler handler;
    private SharedPreferencesManager instance_SP;
    private FirebaseFirestore db;
    private LottieAnimationView anim;
    private ImageView iv;
    private ImageView arrow;
    private TextView statement;
    private ActivityImageBinding bo;
    private Intent intent;
    private TextView report_button;
    private ActionBar actionBar;
    private WritingAnimViewModel writingAnimViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bo = DataBindingUtil.setContentView(ImageActivity.this, R.layout.activity_image);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }
        init();
        writingAnimViewModel = new ViewModelProvider(this).get(WritingAnimViewModel.class);
        writingAnimViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                setUIvisibility(8);
                setAnimVisibility(0);
                if (actionBar != null) {
                    actionBar.hide();
                }
            } else {
                setUIvisibility(0);
                setAnimVisibility(8);
                if (actionBar != null) {
                    actionBar.show();
                }
            }
        });
        proceed();
    }

    private void proceed() {
        displayImage(iv, intent);
        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        report_button.setOnClickListener(view -> {
            if (isConnectedToInternet()) {
                db = FirebaseFirestore.getInstance();
                addReport();
            } else {
                Toast.makeText(ImageActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        arrow.setOnClickListener(view -> {
            if (isConnectedToInternet()) {
                db = FirebaseFirestore.getInstance();
                incrementReadStoryCount();
                startStoryActivity(word1, word2, word3, category);
            } else {
                Toast.makeText(ImageActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayImage(ImageView iv, Intent intent) {
        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(ImageActivity.this)
                .load(intent.getStringExtra("url"))
                .apply(requestOptions.placeholder(R.drawable.placeholder))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failure here
                        new AlertDialog.Builder(ImageActivity.this)
                                .setTitle("Error loading image")
                                .setMessage("An unexpected error occurred.")
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                    // Do something if the user clicks "OK"
                                    dialog.dismiss(); // Dismiss the dialog
                                })
                                .setCancelable(false) // Prevent dialog dismissal on outside touch or back press
                                .create()
                                .show();
                        return true; // Return false to allow Glide to display the placeholder or error image
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Resource successfully loaded
                        return false; // Return false to allow Glide to handle displaying the loaded resource
                    }

                })
                .into(iv);

        if (Boolean.TRUE.equals(writingAnimViewModel.isLoading().getValue())) {
            statement.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        } else {
            statement.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
        }
    }

    private void startStoryActivity(String word1, String word2, String word3, String category) {
        Bundle params = new Bundle();
        params.putString("category", "Button Click");
        params.putString("button_name", "story button");
        FirebaseAnalytics.getInstance(this).logEvent("button_click", params);

        writingAnimViewModel.setLoading(true);
        CompletableFuture<String> future = new NetworkRequest().
                generateStoryAsync(word1, word2, word3, category);

// Handling the result when it becomes available
        future.thenAccept(story -> {
            // Handle the image URL when the request is successful
            // This code will run in the main thread (UI thread)
            // Use imageUrl here to display the image or perform other actions
            Intent intent = new Intent(ImageActivity.this, StoryActivity.class);
            intent.putExtra("story", story);
            startActivity(intent);
            this.finish();
        }).exceptionally(exception -> {
            new AlertDialog.Builder(ImageActivity.this)
                    .setTitle("Error loading story")
                    .setMessage("An unexpected error occurred.")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        // Do something if the user clicks "OK"
                        dialog.dismiss(); // Dismiss the dialog
                    })
                    .setCancelable(false) // Prevent dialog dismissal on outside touch or back press
                    .create()
                    .show();
            //Toast.makeText(ImageActivity.this,"error loading",Toast.LENGTH_SHORT).show();
            writingAnimViewModel.setLoading(false);
            // This code will also run in the main thread (UI thread)
            exception.printStackTrace();
            return null;
        });
    }

    private void addReport() {
        CollectionReference usersCollection = db.collection("Users");
        String usernameToUpdate = instance_SP.getUsername();
        // Create a map with the updated "count" value
        Map<String, Object> incrementData = new HashMap<>();
        String words = instance_SP.getCategory() + ":" + instance_SP.getWord1() + "," +  instance_SP.getWord2() + "," + instance_SP.getWord3();
        incrementData.put("report_words", words);

        usersCollection.whereEqualTo("username", usernameToUpdate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        // Get the document ID of the user to update
                        String documentId = documentSnapshot.getId();
                        // Update the "count" field for the user
                        usersCollection.document(documentId)
                                .update(incrementData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update successful
                                    report_button.setVisibility(View.GONE);
                                    Toast.makeText(ImageActivity.this, "Image reported", Toast.LENGTH_SHORT).show();

                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ImageActivity.this, "ERROR: Please try again later", Toast.LENGTH_SHORT).show();
                });
    }
    private void incrementReadStoryCount() {
        CollectionReference usersCollection = db.collection("Users");
        String usernameToUpdate = instance_SP.getUsername();
        // Create a map with the updated "count" value
        Map<String, Object> incrementData = new HashMap<>();
        incrementData.put("read_story", FieldValue.increment(1));

        usersCollection.whereEqualTo("username", usernameToUpdate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        // Get the document ID of the user to update
                        String documentId = documentSnapshot.getId();
                        // Update the "count" field for the user
                        usersCollection.document(documentId)
                                .update(incrementData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update successful
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ImageActivity.this, "ERROR: Please try again later", Toast.LENGTH_SHORT).show();
                });
    }

    private void setUIvisibility(int value) {
        iv.setVisibility(value);
        if (value == 0) {
            handler.postDelayed(() -> {
                arrow.setVisibility(value);
                statement.setVisibility(value);
                report_button.setVisibility(value);
            }, 2000);
        } else {
            arrow.setVisibility(value);
            statement.setVisibility(value);
            report_button.setVisibility(value);
        }
    }

    private void setAnimVisibility(int value) {
        anim.setVisibility(value);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void init() {
        arrow = bo.ReadStoryArrow;
        statement = bo.readStoryStatement;
        iv = bo.imageView;
        anim = bo.animationView;
        report_button = bo.reportButton;
        handler = new Handler();
        intent = getIntent();
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Go back?")
                .setMessage("Are you sure you want to go back?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                        (dialogInterface, i) -> ImageActivity.super.onBackPressed()).create().show();
    }

    // Helper method to check internet connectivity
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}