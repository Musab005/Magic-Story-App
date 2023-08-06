package com.apps005.magicstory.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.ImageNetworkRequest;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.Util.WordListener;
import com.apps005.magicstory.databinding.ActivityMainBinding;

import java.util.concurrent.CompletableFuture;

//TODO: check going to home then reopening app and also handling notifications during app
//TODO: animation of writing story need to save upon config change
//TODO: Read story text appearing after delay upon config change
//TODO: onResume called after ending landing page
//TODO: for activities that display animation, we need anim to continue on config changed and not make multiple API calls
//TODO: ImageActivity oncnfigchanged put read story aarrow immediately w/o delay
//TODO: story activitty done button
//TODO: back button pressed on landing page
//TODO: buffer-end when "done" pressed form story activity ??
//TODO: image activity when config changed keep the saved instance state
//TODO: when clicked read story and writing animation appears, it defaults to normal screen onconfigchanged but
//TODO: background api call still working
//issue of after login method when using on mobile might be solved with onStart() ??


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
    private TextView hidden_statement;
    private TextView category_statement;
    private TextView words_statement;
    private Button btn;
    private Handler handler;
    private boolean isProgressBarVisible = false;
    private boolean isTextViewVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        if (savedInstanceState != null) {
            isProgressBarVisible = savedInstanceState.getBoolean("progressBarVisible", false);
            isTextViewVisible = savedInstanceState.getBoolean("textViewVisible", false);
        }
        if (instance_SP.getUsername().isEmpty()) {
            Intent intent_first_login = new Intent(MainActivity.this, LandingPage.class);
            Log.d("MainActivity", "starting landing page");
            startActivity(intent_first_login);
        } else {
            Log.d("MainActivity", "did not start landing page");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init_portrait();
        pBar.setVisibility(isProgressBarVisible ? View.VISIBLE : View.GONE);
        hidden_statement.setVisibility(isTextViewVisible ? View.VISIBLE : View.GONE);
        afterLogin();
    }

    private void init_portrait() {
        hidden_statement = bo.hiddenStatement;
        pBar = bo.pBar;
        anim = bo.animationView;
        category_statement = bo.categoryStatement;
        spinner = bo.categoryBox;
        words_statement = bo.wordsStatement;
        first_word_box = bo.word1;
        second_word_box = bo.word2;
        third_word_box = bo.word3;
        btn = bo.generateButton;
    }

    private void afterLogin() {
        setUI();
        //Generate Button - On Click
        btn.setOnClickListener(view -> {
            instance_SP.saveData(word1, word2, word3, category);
            Log.d("MainActivity onBtnClick", "saving: " + word1 + word2 + word3 + category);
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

    private void setUI() {
        wordBoxListener_init();
        spinner_init();
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
        Log.d("MainActivity setUI", "setting: " + word1 + word2 + word3 + category);
    }


    private void startImageActivity() {
        buffer_start();
        CompletableFuture<String> future = new ImageNetworkRequest().generateImageAsync(word1, word2, word3, category, MainActivity.this.getApplicationContext());
        // Handling the result when it becomes available
        future.thenAccept(imageUrl -> {
            // Handle the image URL when the request is successful
            // This code will run in the main thread (UI thread)
            // Use imageUrl here to display the image or perform other actions
            Log.d("MainActivity startImage", "ImageURL successfully generated");
            Intent intent = new Intent(MainActivity.this, ImageTest.class);
            intent.putExtra("url", imageUrl);
            intent.putExtra("word1",word1);
            intent.putExtra("word2",word2);
            intent.putExtra("word3",word3);
            intent.putExtra("category",category);
            handler = new Handler();
            startActivity(intent);
            //buffer_end();
            handler.postDelayed(this::buffer_end, 2000);
        }).exceptionally(exception -> {
            Toast.makeText(MainActivity.this,"error line 206: Image fail",Toast.LENGTH_SHORT).show();
            Log.d("MainActivity startImage", "ImageURL unsuccessful");
            // Handle exceptions here, if any
            // This code will also run in the main thread (UI thread)
            exception.printStackTrace();
            return null;
        });
    }

    private void buffer_start() {
        category_statement.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.GONE);
        words_statement.setVisibility(View.GONE);
        first_word_box.setVisibility(View.INVISIBLE);
        second_word_box.setVisibility(View.INVISIBLE);
        third_word_box.setVisibility(View.INVISIBLE);
        btn.setVisibility(View.INVISIBLE);
        isProgressBarVisible = true;
        isTextViewVisible = true;
        hidden_statement.setVisibility(View.VISIBLE);
        pBar.setVisibility(View.VISIBLE);
        anim.cancelAnimation();
        anim.setVisibility(View.INVISIBLE);
    }

    private void buffer_end() {
        category_statement.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        words_statement.setVisibility(View.VISIBLE);
        first_word_box.setVisibility(View.VISIBLE);
        second_word_box.setVisibility(View.VISIBLE);
        third_word_box.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        isProgressBarVisible = false;
        isTextViewVisible = false;
        hidden_statement.setVisibility(View.GONE);
        pBar.setVisibility(View.GONE);
        anim.playAnimation();
        anim.setVisibility(View.VISIBLE);
    }



    private void spinner_init() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void wordBoxListener_init() {
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
        first_word_box.addTextChangedListener(new WordListener(first_word_box));
        second_word_box.addTextChangedListener(new WordListener(second_word_box));
        third_word_box.addTextChangedListener(new WordListener(third_word_box));
    }

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
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        instance_SP.saveData(word1, word2, word3, category);
    }

    @Override
    public void onBackPressed() {
        // Go back to the previous activity when the back button is pressed
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");
        instance_SP.saveData(word1, word2, word3, category);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("progressBarVisible", isProgressBarVisible);
        outState.putBoolean("textViewVisible", isTextViewVisible);
    }

}