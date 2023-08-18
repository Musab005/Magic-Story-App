package com.apps005.magicstory.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.apps005.magicstory.Util.LoginPageLoadingViewModel;
import com.apps005.magicstory.Util.NetworkChangeReceiver;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.databinding.ActivityLoginBinding;
import com.apps005.magicstory.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


//TODO: clean up broadcast reciever

public class LoginActivity extends AppCompatActivity {

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
    private LoginPageLoadingViewModel loginPageLoadingViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.apps005.magicstory.databinding.ActivityLoginBinding bo = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init(bo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        button_listener();
        loginPageLoadingViewModel = new ViewModelProvider(this).get(LoginPageLoadingViewModel.class);
        loginPageLoadingViewModel.isLoading().observe(this, isLoading -> {
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
                Toast.makeText(LoginActivity.this,
                        "Please write your first name, last name and choose a username",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (isConnectedToInternet()) {
                    db = FirebaseFirestore.getInstance();
                    loginPageLoadingViewModel.setLoading(true);
                    saveData(first_name, last_name, username, formattedDate);
                } else {
                    Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData(String first_name, String last_name, String username, String formattedDate) {
            User user = new User(first_name, last_name, formattedDate,0, username, 0,0);
            SharedPreferencesManager.getInstance(this.getApplicationContext()).saveUsername(username);
            CollectionReference usersCollection;
                usersCollection = db.collection("Users");
                usersCollection.whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (querySnapshot.isEmpty()) {
                                // Username is available, add the new user to the collection
                                usersCollection.add(user)
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(LoginActivity.this,"ERROR. Try again later",Toast.LENGTH_SHORT).show();
                                            loginPageLoadingViewModel.setLoading(false);
                                        })
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                instance_SP.setFirstLaunch(false);
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                this.finish();
                                            } else {
                                                loginPageLoadingViewModel.setLoading(false);
                                                new AlertDialog.Builder(LoginActivity.this)
                                                        .setTitle("Error")
                                                        .setMessage("An unexpected error occurred.")
                                                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                                            // Do something if the user clicks "OK"
                                                            dialog.dismiss(); // Dismiss the dialog
                                                        })
                                                        .setCancelable(false) // Prevent dialog dismissal on outside touch or back press
                                                        .create()
                                                        .show();
                                            }
                                        });
                            } else {
                                // Username is already taken, display an error message
                                loginPageLoadingViewModel.setLoading(false);
                                Toast.makeText(LoginActivity.this,"Username is already taken",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle errors
                            loginPageLoadingViewModel.setLoading(false);
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Error")
                                    .setMessage("An unexpected error occurred.")
                                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                        // Do something if the user clicks "OK"
                                        dialog.dismiss(); // Dismiss the dialog
                                    })
                                    .setCancelable(false) // Prevent dialog dismissal on outside touch or back press
                                    .create()
                                    .show();
                        });
    }

    private void init(ActivityLoginBinding bo) {
        LottieAnimationView anim = bo.animationView;
        anim.setVisibility(View.VISIBLE);
        save_button = bo.saveButton;
        first_name_box = bo.firstNameBox;
        last_name_box = bo.lastNameBox;
        username_box = bo.usernameBox;
        pBar = bo.pBar;
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        wordBox_init();
        // Register the BroadcastReceiver to monitor network changes
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                        (dialogInterface, i) -> LoginActivity.super.onBackPressed()).create().show();
    }

}