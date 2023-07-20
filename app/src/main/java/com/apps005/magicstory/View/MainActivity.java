package com.apps005.magicstory.View;

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
import com.apps005.magicstory.databinding.ActivityMainBinding;

//TODO: 1. Incorporate a "loading" widget when the app makes the Network Request
//TODO: 2. Ask user for their full name and username when they use the app for the first time
//TODO: 3. Setup an animation upon start of the application which greets user by their username
// OR MAKE A LEFT UTIL COLUMN TO MANAGE ACCOUNT NAME/LOGOUT AND ETC
//TODO: 4. Make the quality of spinner better
//TODO: 5. Make UI/UX better in general
//TODO: 6. Let user choose a theme colour of the app and save their preference. Can edit later


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public interface startActivity {
        void startActivity2(String story);
        void showError2(String error);
    }
    private ActivityMainBinding bo;
    private SharedPreferencesManager pref;
    private EditText first_word_box;
    private EditText second_word_box;
    private EditText third_word_box;
    private String category;
    private String word1;
    private String word2;
    private String word3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "Creating main activity");
        //initialise prefs
        pref = SharedPreferencesManager.getInstance(this);
        Log.d("MainActivity", "Prefs set");
        if (pref.getUsername().isEmpty()) {
            Intent intent_first_login = new Intent(MainActivity.this, LandingPage.class);
            Log.d("MainActivity", "starting landing page");
            startActivity(intent_first_login);
        }
        Log.d("MainActivity", "did not start landing page. Showing username");
        Log.d("MainActivity", pref.getUsername());
        Toast.makeText(MainActivity.this,"welcome" + pref.getUsername(),Toast.LENGTH_LONG).show();
        StoryController.getInstance().setRequestQueue(this);
        //set Content View
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.d("MainActivity", "Content view set");
        //initialise widgets
        first_word_box = bo.word1;
        second_word_box = bo.word2;
        third_word_box = bo.word3;
        //initialise spinner
        Spinner spinner = bo.categoryBox;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        word1 = pref.getWord1();
        word2 = pref.getWord2();
        word3 = pref.getWord3();
        category = pref.getCategory();
        first_word_box.setText(word1);
        second_word_box.setText(word2);
        third_word_box.setText(word3);
        spinner.setPrompt(category);
        Log.d("MainActivity", "setting: " + word1 + word2 + word3 + category);

        // Assuming your EditText widget is named editText
        first_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
                word1 = first_word_box.getText().toString().trim();
            }
        });

        second_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
                word2 = second_word_box.getText().toString().trim();
            }
        });

        third_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
                word3 = third_word_box.getText().toString().trim();
            }
        });

        //Generate Button - On Click
        bo.generateButton.setOnClickListener(view -> {
            Log.d("Main Activity:", "button clicked");
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
                StoryController.getInstance().generateStory(
                        word1, word2, word3, category,
                        new startActivity() {
                            @Override
                            public void startActivity2(String story) {
                                Intent intent3 = new Intent(MainActivity.this, Story.class);
                                intent3.putExtra("story", story);
                                Log.d("MainActivity", "Starting story activity");
                                startActivity(intent3);
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
        Toast.makeText(MainActivity.this,
                category, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        pref.saveData(word1, word2, word3, category);
        Log.d("MainActivity", "saving: " + word1 + word2 + word3 + category);
    }
}