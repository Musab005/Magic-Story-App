package com.apps005.magicstory.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.controller.StoryController;

//TODO: 1. Incorporate a "loading" widget when the app makes the Network Request
//Put all instantiation in a separate method (widget instantiation)
//TODO: 3. Setup an animation upon start of the application which greets user by their username
//TODO: 4. Make the quality of spinner better
//TODO: 5. Make UI/UX better in general
//TODO: 6. Let user choose a theme colour of the app and save their preference. Can edit later


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public interface startActivity {
        void startActivity2(String story);
        void showError2(String error);
    }

    private EditText first_word_box;
    private EditText second_word_box;
    private EditText third_word_box;
    private String category = "";
    private String word1 = "";
    private String word2 = "";
    private String word3 = "";
    private SharedPreferencesManager instance_SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "super onCreate");
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        Log.d("MainActivity", "Created SP instance");
        if (instance_SP.getUsername().isEmpty()) {
            Log.d("MainActivity", "inside if block");
            Intent intent_first_login = new Intent(MainActivity.this, LandingPage.class);
            Log.d("MainActivity", "starting landing page");
            launchLandingPage.launch(intent_first_login);
        } else {
            afterLogin();
    }}

    private void afterLogin() {
        Log.d("MainActivity", "afterLogin");
        com.apps005.magicstory.databinding.ActivityMainBinding bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.d("MainActivity", "Username: " + instance_SP.getUsername());
        Toast.makeText(MainActivity.this, "welcome " + instance_SP.getUsername(), Toast.LENGTH_LONG).show();
        //initialise widgets
        first_word_box = bo.word1;
        second_word_box = bo.word2;
        third_word_box = bo.word3;
        //initialise spinner
        Spinner spinner = bo.categoryBox;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        word1 = instance_SP.getWord1();
        word2 = instance_SP.getWord2();
        word3 = instance_SP.getWord3();
        category = instance_SP.getCategory();
        first_word_box.setText(word1);
        second_word_box.setText(word2);
        third_word_box.setText(word3);
        String[] categoryArray = getResources().getStringArray(R.array.Categories);
// Find the index of the category in the array
        int categoryIndex = -1;
        for (int i = 0; i < categoryArray.length; i++) {
            if (categoryArray[i].equals(category)) {
                categoryIndex = i;
                break;
            }
        }
// Set the default selected item in the Spinner
        if (categoryIndex != -1) {
            spinner.setSelection(categoryIndex);
        }
        Log.d("MainActivity", "setting: " + word1 + word2 + word3 + category);


        first_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
                word1 = first_word_box.getText().toString().trim();
                instance_SP.saveData(word1, word2, word3, category);
            }
        });

        second_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
                word2 = second_word_box.getText().toString().trim();
                instance_SP.saveData(word1, word2, word3, category);
            }
        });

        third_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
                word3 = third_word_box.getText().toString().trim();
                instance_SP.saveData(word1, word2, word3, category);
            }
        });

        //Generate Button - On Click
        bo.generateButton.setOnClickListener(view -> {
            Log.d("Main Activity:", "button clicked");
            instance_SP.saveData(word1, word2, word3, category);
            if (word1.equals("") ||
                    word2.equals("") ||
                    word3.equals("") ||
                    category.isEmpty()) {
                Log.d("Main Activity:", "incorrect arguments");
                Toast.makeText(MainActivity.this,
                        "Enter 3 words and choose a category",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d("MainActivity", "calling generate story from controller");
                StoryController.getInstance(this.getApplicationContext()).generateStory(
                        word1, word2, word3, category, this.getApplicationContext(),
                        new startActivity() {
                            @Override
                            public void startActivity2(String story) {
                                Intent intent2 = new Intent(MainActivity.this, Story.class);
                                intent2.putExtra("story", story);
                                Log.d("MainActivity", "Starting story activity");
                                startActivity(intent2);
                            }
                            @Override
                            public void showError2(String error) {
                                Log.d("MainActivity", "error: " + error);
                                Toast.makeText(MainActivity.this, "Error: " + error,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private final ActivityResultLauncher<Intent> launchLandingPage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle the result from the LandingPage activity here
                    Log.d("MainActivity:", "onResult");
                    afterLogin();
                } else {
                    // Handle other result scenarios, if needed
                }
            }
    );

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.category = adapterView.getItemAtPosition(i).toString();
        instance_SP.saveData(word1, word2, word3, category);
        Toast.makeText(MainActivity.this,
                category, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        instance_SP.saveData(word1, word2, word3, category);
        instance_SP.saveData(word1, word2, word3, category);
    }
    @Override
    protected void onPause() {
        super.onPause();
        instance_SP.saveData(word1, word2, word3, category);
        Log.d("MainActivity", "saving: " + word1 + word2 + word3 + category);
    }

}