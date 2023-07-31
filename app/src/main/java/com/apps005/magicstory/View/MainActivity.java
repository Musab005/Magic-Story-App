package com.apps005.magicstory.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
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

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.ImageNetworkRequest;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.databinding.ActivityMainBinding;

import java.util.concurrent.CompletableFuture;

//TODO: move landing page widgets down
//TODO: buffer-end when "done" pressed form story activity
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
        Log.d("MainActivity", "onCreate");
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        if (instance_SP.getUsername().isEmpty()) {
            Intent intent_first_login = new Intent(MainActivity.this, LandingPage.class);
            Log.d("MainActivity", "starting landing page");
            launchLandingPage.launch(intent_first_login);
        } else {
            Log.d("MainActivity", "did not start landing page");
            afterLogin();
    }}



    private void afterLogin() {
        init();
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
                startImageActivity();
            }
        });
    }

    private void init() {
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toast.makeText(MainActivity.this, "welcome " + instance_SP.getUsername(), Toast.LENGTH_LONG).show();
        anim = bo.animationView;
        anim.setVisibility(View.VISIBLE);
        pBar = bo.pBar;
        wordBox_init(bo);
        spinner = spinner_init(bo);
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


    private void buffer_start(ActivityMainBinding bo, Spinner spinner) {
        first_word_box.setVisibility(View.INVISIBLE);
        second_word_box.setVisibility(View.INVISIBLE);
        third_word_box.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        bo.generateButton.setVisibility(View.INVISIBLE);
        bo.categoryStatement.setVisibility(View.INVISIBLE);
        bo.wordsStatement.setVisibility(View.INVISIBLE);
        bo.hiddenStatement.setVisibility(View.VISIBLE);
        anim.cancelAnimation();
        anim.setVisibility(View.INVISIBLE);
        pBar.setVisibility(View.VISIBLE);
    }

    private void buffer_end(ActivityMainBinding bo, Spinner spinner) {
        first_word_box.setVisibility(View.VISIBLE);
        second_word_box.setVisibility(View.VISIBLE);
        third_word_box.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        bo.generateButton.setVisibility(View.VISIBLE);
        bo.categoryStatement.setVisibility(View.VISIBLE);
        bo.hiddenStatement.setVisibility(View.INVISIBLE);
        bo.wordsStatement.setVisibility(View.VISIBLE);
        anim.playAnimation();
        anim.setVisibility(View.VISIBLE);
        pBar.setVisibility(View.INVISIBLE);

    }

    private void startImageActivity() {
        buffer_start(bo, spinner);
        CompletableFuture<String> future = new ImageNetworkRequest().generateImageAsync(word1, word2, word3, category, MainActivity.this.getApplicationContext());
        // Handling the result when it becomes available
        future.thenAccept(imageUrl -> {
            // Handle the image URL when the request is successful
            // This code will run in the main thread (UI thread)
            // Use imageUrl here to display the image or perform other actions
            Log.d("MainActivity", "ImageURL successfully generated");
            Intent intent = new Intent(MainActivity.this, ImageTest.class);
            intent.putExtra("url", imageUrl);
            intent.putExtra("word1",word1);
            intent.putExtra("word2",word2);
            intent.putExtra("word3",word3);
            intent.putExtra("category",category);
            buffer_end(bo, spinner);
            startActivity(intent);
        }).exceptionally(exception -> {
            Toast.makeText(MainActivity.this,"Image fail",Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "ImageURL unsuccessful");
            // Handle exceptions here, if any
            // This code will also run in the main thread (UI thread)
            exception.printStackTrace();
            return null;
        });
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

    private void wordBox_init(ActivityMainBinding bo) {
        first_word_box = bo.word1;
        second_word_box = bo.word2;
        third_word_box = bo.word3;

        first_word_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                word1 = first_word_box.getText().toString().trim();
                hideKeyboard(textView);
                return true;
            }
            return false;
        });
        second_word_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                word2 = second_word_box.getText().toString().trim();
                hideKeyboard(textView);
                return true;
            }
            return false;
        });
        third_word_box.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                word3 = third_word_box.getText().toString().trim();
                hideKeyboard(textView);
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
                    Log.d("MainActivity:", "onResult after activity ended");
                    afterLogin();
                } else {
                    // Handle other result scenarios, if needed
                    Toast.makeText(MainActivity.this, "Error",
                            Toast.LENGTH_LONG).show();
                    Log.d("MainActivity:", "onResult error after activity ended");

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

}