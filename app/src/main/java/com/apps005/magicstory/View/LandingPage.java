package com.apps005.magicstory.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.databinding.ActivityLandingPageBinding;
import com.apps005.magicstory.databinding.ActivityLandingPageLandBinding;
import com.apps005.magicstory.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LandingPage extends AppCompatActivity {

    private EditText first_name_box;
    private ProgressBar pBar;
    private EditText last_name_box;
    private EditText username_box;
    private Button save_button;
    private FirebaseFirestore db;
    private ActivityLandingPageBinding bo;
    private ActivityLandingPageLandBinding bo_land;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bo_land = DataBindingUtil.setContentView(this, R.layout.activity_landing_page_land);
            init_land(bo_land);
            Log.d("LandingPage onCreate", "landscape config set");
        } else {
            bo = DataBindingUtil.setContentView(this, R.layout.activity_landing_page);
            init_portrait(bo);
            Log.d("LandingPage onCreate", "portrait config set");
        }
        button_listener();
    }

    private void button_listener() {
        save_button.setOnClickListener(view -> {
            String first_name = first_name_box.getText().toString().trim();
            String last_name = last_name_box.getText().toString().trim();
            String username = username_box.getText().toString().trim();
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
                save_button.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);
                    saveData(first_name, last_name, username, formattedDate);
            }
        });
    }

    private void saveData(String first_name, String last_name, String username, String formattedDate) {
        User user = new User(first_name, last_name, formattedDate,0, username);
        SharedPreferencesManager.getInstance(this.getApplicationContext()).saveUsername(username);
        db.collection("Users").add(user)
                .addOnFailureListener(e -> {
                    Toast.makeText(LandingPage.this,"fail",Toast.LENGTH_SHORT).show();
                    pBar.setVisibility(View.GONE);
                    save_button.setVisibility(View.VISIBLE);
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LandingPage", "ending");
                            finish();
                    } else {
                        pBar.setVisibility(View.GONE);
                        save_button.setVisibility(View.VISIBLE);
                        Toast.makeText(LandingPage.this,"fail",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init_portrait(ActivityLandingPageBinding bo) {
        LottieAnimationView anim = bo.animationView;
        anim.setVisibility(View.VISIBLE);
        save_button = bo.saveButton;
        db = FirebaseFirestore.getInstance();
        first_name_box = bo.firstNameBox;
        last_name_box = bo.lastNameBox;
        username_box = bo.usernameBox;
        pBar = bo.pBar;
        wordBox_init();
    }

    private void init_land(ActivityLandingPageLandBinding bo_land) {
        LottieAnimationView anim = bo_land.animationView;
        anim.setVisibility(View.VISIBLE);
        save_button = bo_land.saveButton;
        db = FirebaseFirestore.getInstance();
        first_name_box = bo_land.firstNameBox;
        last_name_box = bo_land.lastNameBox;
        username_box = bo_land.usernameBox;
        pBar = bo_land.pBar;
        wordBox_init();
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
    public void onBackPressed() {
        // Close the app when the back button is pressed on the LandingPageActivity
        finishAffinity();
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bo_land = DataBindingUtil.setContentView(this, R.layout.activity_main_land);
            Log.d("LandingPage onConfig", "setting landscape");
            init_land(bo_land);
            button_listener();
        } else {
            bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
            Log.d("LandingPage onConfig", "setting portrait");
            init_portrait(bo);
            button_listener();
        }
    }

}