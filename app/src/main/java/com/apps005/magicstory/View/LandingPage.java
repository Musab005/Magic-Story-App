package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.LandingPageLoadingViewModel;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.databinding.ActivityLandingPageBinding;
import com.apps005.magicstory.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LandingPage extends AppCompatActivity {

    private EditText first_name_box;
    private SharedPreferencesManager instance_SP;
    private ProgressBar pBar;
    private EditText last_name_box;
    private EditText username_box;
    private Button save_button;
    private FirebaseFirestore db;
    private String first_name;
    private String last_name;
    private String username;
    private LandingPageLoadingViewModel landingPageLoadingViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.apps005.magicstory.databinding.ActivityLandingPageBinding bo = DataBindingUtil.setContentView(this, R.layout.activity_landing_page);
        init(bo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        button_listener();
        landingPageLoadingViewModel = new ViewModelProvider(this).get(LandingPageLoadingViewModel.class);
        landingPageLoadingViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                save_button.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);
            } else {
                save_button.setVisibility(View.VISIBLE);
                pBar.setVisibility(View.GONE);
            }
        });
    }

    private void button_listener() {
        save_button.setOnClickListener(view -> {
            save_fields();
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = currentDate.format(formatter);

            if (first_name.isEmpty() ||
                    last_name.isEmpty() ||
                    username.isEmpty()) {
                Toast.makeText(LandingPage.this,
                        "Please write your first name, last name and choose a username",
                        Toast.LENGTH_SHORT).show();
            } else {
                landingPageLoadingViewModel.setLoading(true);
                saveData(first_name, last_name, username, formattedDate);
            }
        });
    }

    private void saveData(String first_name, String last_name, String username, String formattedDate) {
        User user = new User(first_name, last_name, formattedDate,0, username, 0,0);
        SharedPreferencesManager.getInstance(this.getApplicationContext()).saveUsername(username);

        CollectionReference usersCollection = db.collection("Users");
        usersCollection.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // Username is available, add the new user to the collection
                        usersCollection.add(user)
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LandingPage.this,"ERROR. Try again later",Toast.LENGTH_SHORT).show();
                                    landingPageLoadingViewModel.setLoading(false);
                                })
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(() -> {
                                            instance_SP.setFirstLaunch(false);
                                            startActivity(new Intent(LandingPage.this, MainActivity.class));
                                            this.finish();
                                        }, 2000);
                                    } else {
                                        landingPageLoadingViewModel.setLoading(false);
                                        Toast.makeText(LandingPage.this,"ERROR. Try again later",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Username is already taken, display an error message
                        landingPageLoadingViewModel.setLoading(false);
                        Toast.makeText(LandingPage.this,"Username is already taken",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    landingPageLoadingViewModel.setLoading(false);
                    Toast.makeText(LandingPage.this,"ERROR. Try again later",Toast.LENGTH_SHORT).show();
                });

    }

    private void init(ActivityLandingPageBinding bo) {
        LottieAnimationView anim = bo.animationView;
        anim.setVisibility(View.VISIBLE);
        save_button = bo.saveButton;
        db = FirebaseFirestore.getInstance();
        first_name_box = bo.firstNameBox;
        last_name_box = bo.lastNameBox;
        username_box = bo.usernameBox;
        pBar = bo.pBar;
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        wordBox_init();
    }

    private void save_fields() {
        first_name = first_name_box.getText().toString().trim();
        last_name = last_name_box.getText().toString().trim();
        username = username_box.getText().toString().trim();
    }

    private void wordBox_init() {
        first_name_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(textView);
                return true;
            }
            return false;
        });
        last_name_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(textView);
                return true;
            }
            return false;
        });
        username_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(textView);
                return true;
            }
            return false;
        });
        first_name_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
            }
        });
        last_name_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
            }
        });
        username_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
            }
        });
    }


    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null) {
                hideKeyboard(view);
            }
        }
        return super.onTouchEvent(event);
    }



}