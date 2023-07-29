package com.apps005.magicstory.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.controller.StoryController;
import com.apps005.magicstory.databinding.ActivityMainBinding;

//TODO: 1. Incorporate a "loading" widget when the app makes the Network Request
//issue of after login method when using on mobile might be solved with onStart() ??
//too much activity on main thread!!
//Put all instantiation in a separate method (widget instantiation)
//TODO: 3. Setup an animation upon start of the application which greets user by their username
//TODO: 4. Make the quality of spinner better
//TODO: 5. Make UI/UX better in general
//TODO: 6. Let user choose a theme colour of the app and save their preference. Can edit later


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public interface startImage {
        void onSuccess(String url);
        void onError(String error);
    }

    private EditText first_word_box;
    private EditText second_word_box;
    private EditText third_word_box;
    private String category = "";
    private String word1 = "";
    private String word2 = "";
    private String word3 = "";
    private SharedPreferencesManager instance_SP;
    private LottieAnimationView anim;
    private ProgressBar pBar;
    private ActivityMainBinding bo;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "super onCreate");
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        if (instance_SP.getUsername().isEmpty()) {
            Intent intent_first_login = new Intent(MainActivity.this, LandingPage.class);
            Log.d("MainActivity", "starting landing page");
            launchLandingPage.launch(intent_first_login);
        } else {
            afterLogin();
    }}



    @SuppressLint("ResourceAsColor")
    private void afterLogin() {
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toast.makeText(MainActivity.this, "welcome " + instance_SP.getUsername(), Toast.LENGTH_LONG).show();
        wordBox_init(bo);
        bo.wordsStatement.setTextColor(R.color.light_white);
        spinner = spinner_init(bo);
        setSavedData(spinner);
        //start anim
        anim = bo.animationView;
        anim.setVisibility(View.VISIBLE);

        //Generate Button - On Click
        bo.generateButton.setOnClickListener(view -> {
            instance_SP.saveData(word1, word2, word3, category);
            if (word1.equals("") ||
                    word2.equals("") ||
                    word3.equals("") ||
                    category.isEmpty()) {
                Toast.makeText(MainActivity.this,
                        "Enter 3 words and choose a category",
                        Toast.LENGTH_SHORT).show();
            } else {
                buffer_start(bo, spinner);
                startImageActivity();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void buffer_start(ActivityMainBinding bo, Spinner spinner) {
        first_word_box.setVisibility(View.INVISIBLE);
        second_word_box.setVisibility(View.INVISIBLE);
        third_word_box.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        bo.generateButton.setVisibility(View.INVISIBLE);
        bo.categoryStatement.setVisibility(View.INVISIBLE);
        bo.wordsStatement.setText("Putting in the magic...");
        bo.wordsStatement.setTextColor(R.color.black);
        anim.cancelAnimation();
        anim.setVisibility(View.INVISIBLE);
        pBar.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ResourceAsColor")
    private void buffer_end(ActivityMainBinding bo, Spinner spinner) {
        first_word_box.setVisibility(View.VISIBLE);
        second_word_box.setVisibility(View.VISIBLE);
        third_word_box.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        bo.generateButton.setVisibility(View.VISIBLE);
        bo.categoryStatement.setVisibility(View.VISIBLE);
        bo.wordsStatement.setText(R.string.enter_3_words_text);
        bo.wordsStatement.setTextColor(R.color.light_white);
        anim.playAnimation();
        anim.setVisibility(View.VISIBLE);
        pBar.setVisibility(View.INVISIBLE);

    }

    private void startImageActivity() {
        StoryController.getInstance(this.getApplicationContext()).generateImage(
                word1, word2, word3, category, this.getApplicationContext(), new startImage() {
                    @Override
                    public void onSuccess(String url) {
                        Intent intent = new Intent(MainActivity.this, ImageTest.class);
                        intent.putExtra("url", url);
                        intent.putExtra("word1", word1);
                        intent.putExtra("word2", word2);
                        intent.putExtra("word3", word3);
                        intent.putExtra("category", category);
                        Log.d("MainActivity", "Starting image activity");
                        //pBar.setVisibility(View.GONE);
                        buffer_end(bo, spinner);
                        startActivity(intent);
                    }
                    @Override
                    public void onError(String error) {
                        Log.d("MainActivity", "error: " + error);
                        Toast.makeText(MainActivity.this, "Error: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    @NonNull
    private Spinner spinner_init(ActivityMainBinding bo) {
        Spinner spinner = bo.categoryBox;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return spinner;
    }

    private void setSavedData(Spinner spinner) {
        word1 = instance_SP.getWord1();
        word2 = instance_SP.getWord2();
        word3 = instance_SP.getWord3();
        category = instance_SP.getCategory();
        first_word_box.setText(word1);
        second_word_box.setText(word2);
        third_word_box.setText(word3);
        String[] categoryArray = getResources().getStringArray(R.array.Categories);
        int categoryIndex = -1;
        for (int i = 0; i < categoryArray.length; i++) {
            if (categoryArray[i].equals(category)) {
                categoryIndex = i;
                break;
            }
        }
        if (categoryIndex != -1) {
            spinner.setSelection(categoryIndex);
        }
        Log.d("MainActivity", "setting: " + word1 + word2 + word3 + category);
    }

    private void wordBox_init(ActivityMainBinding bo) {
        pBar = bo.pBar;
        first_word_box = bo.word1;
        second_word_box = bo.word2;
        third_word_box = bo.word3;
        third_word_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                word3 = third_word_box.getText().toString().trim();
                hideKeyboard(textView);
                Log.d("MainActivity", "word3: " + word3);
                return true;
            }
            return false;
        });
        first_word_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                word1 = first_word_box.getText().toString().trim();
                hideKeyboard(textView);
                Log.d("MainActivity", "word1: " + word1);
                return true;
            }
            return false;
        });
        second_word_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                word2 = second_word_box.getText().toString().trim();
                hideKeyboard(textView);
                Log.d("MainActivity", "word2: " + word2);
                return true;
            }
            return false;
        });
        first_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
            }
        });
        second_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
            }
        });
        third_word_box.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
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
                    Toast.makeText(MainActivity.this, "Error",
                            Toast.LENGTH_LONG).show();

                }
            }
    );

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        word3 = third_word_box.getText().toString().trim();
        word2 = second_word_box.getText().toString().trim();
        word1 = first_word_box.getText().toString().trim();
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
                category, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        instance_SP.saveData(word1, word2, word3, category);
    }
    @Override
    protected void onPause() {
        super.onPause();
        instance_SP.saveData(word1, word2, word3, category);
        Log.d("MainActivity", "saving: " + word1 + word2 + word3 + category);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        buffer_end(bo, spinner);
//    }
}