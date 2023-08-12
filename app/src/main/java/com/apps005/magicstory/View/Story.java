package com.apps005.magicstory.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.databinding.ActivityStoryBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;


public class Story extends AppCompatActivity {
    private ScrollView scrollView;
    private SharedPreferencesManager instance_SP;
    private FirebaseFirestore db;
    private LinearLayout buttonLayout;
    private TextView storyText;
    private Button done_button;
    private ActivityStoryBinding bo;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bo = DataBindingUtil.setContentView(this, R.layout.activity_story);
        init();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        proceed();

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void proceed() {
        storyText.setText(intent.getStringExtra("story"));
        done_button.setOnClickListener(view -> {
            if (isConnectedToInternet()) {
                db = FirebaseFirestore.getInstance();
                incrementDoneCount();
            }
            this.finish();
        });
    }

    private void init() {
        done_button = bo.DoneButton;
        scrollView = bo.scrollView;
        buttonLayout = bo.buttonLayout;
        storyText = bo.storyText;
        widgets_init();
        intent = getIntent();
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
    }


    private void widgets_init() {
        scrollView.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Get the height of the ScrollView
            int scrollViewHeight = scrollView.getHeight();

            // Get the height of the content inside the ScrollView
            int contentHeight = storyText.getHeight();

            // Get the current scroll position
            int currentScrollPosition = scrollY + scrollViewHeight;

            //Check if the scroll position has reached the bottom
            if (currentScrollPosition >= contentHeight) {
                buttonLayout.setVisibility(View.VISIBLE);
            } else {
                buttonLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void incrementDoneCount() {
        CollectionReference usersCollection = db.collection("Users");
        String usernameToUpdate = instance_SP.getUsername();
        // Create a map with the updated "count" value
        Map<String, Object> incrementData = new HashMap<>();
        incrementData.put("done_story", FieldValue.increment(1));

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
                    // Handle errors
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Done reading?")
                .setMessage("Scroll all the way to the bottom!").show();
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}