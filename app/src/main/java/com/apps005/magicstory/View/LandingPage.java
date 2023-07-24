package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LandingPage extends AppCompatActivity {

    private EditText first_name_box;
    private EditText last_name_box;
    private EditText username_box;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LandingPage", "super onCreate");
        com.apps005.magicstory.databinding.ActivityLandingPageBinding bo = DataBindingUtil.setContentView(this, R.layout.activity_landing_page);
        Button save_button = bo.saveButton;
        first_name_box = bo.firstNameBox;
        last_name_box = bo.lastNameBox;
        username_box = bo.usernameBox;
        progressBar = bo.progressBar;
        Handler handler = new Handler();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        save_button.setOnClickListener(view -> {
            Log.d("LandingPage", "save button clicked");
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
                //save data
                Log.d("LandingPage", "saving data");
                progressBar.setVisibility(View.VISIBLE);
                User user = new User(first_name,last_name,formattedDate,0,username);
                SharedPreferencesManager.getInstance(this.getApplicationContext()).saveUsername(username);
                db.collection("Users").add(user)
                        .addOnSuccessListener(documentReference ->
                                Toast.makeText(LandingPage.this,"success",Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(LandingPage.this,"fail",Toast.LENGTH_SHORT).show())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Delay for 2 seconds using Handler
                                handler.postDelayed(() -> {
                                    // Code to be executed after 2 seconds
                                    // For example, you can start a new activity here
                                    // startActivity(new Intent(MainActivity.this, NewActivity.class));
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Log.d("LandingPage", "onComplete");
                                    setResult(RESULT_OK);
                                    finish();
                                    Log.d("LandingPage", "finish done");
                                }, 2000);
                            } else {
                                //??
                            }
                        });
                //does setResult take back to mainActivity or finish??
                //correct to make sure the activity dosen't end in case of failuer listener
            }
        });
    }
}