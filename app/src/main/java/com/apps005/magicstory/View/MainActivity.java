package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.ImageNetworkRequest;
import com.apps005.magicstory.Util.MainLoadingViewModel;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.Util.WordListener;
import com.apps005.magicstory.databinding.ActivityMainBinding;

import java.util.concurrent.CompletableFuture;

import okhttp3.internal.http2.Http2Reader;
//TODO: BUGS
//TODO: pBar on landing page dosen't continue onconfigchanged

//TODO: INFO
//TODO: read story arrow and statement under pBar? what about image view?
//TODO: issue when action bar back pressed during writing story anim
//TODO: check going to home then reopening app and also handling notifications during app
//TODO: onResume called after ending landing page
//TODO: for activities that display animation, we need anim to continue on config changed and not make multiple API calls

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public interface startImage {
        void onSuccess(String url);
        void onError(String error);
    }

    private ConstraintLayout hidden_layout;
    private EditText first_word_box;
    private EditText second_word_box;
    private EditText third_word_box;
    private String category = "";
    private String word1 = "";
    private String word2 = "";
    private String word3 = "";
    private SharedPreferencesManager instance_SP;
    private LottieAnimationView pencil_anim;
    private ActivityMainBinding bo;
    private Spinner spinner;
    private TextView category_statement;
    private TextView words_statement;
    private Button btn;
    private MainLoadingViewModel loadingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        if (instance_SP.getUsername().isEmpty()) {
            Intent intent_first_login = new Intent(MainActivity.this, LandingPage.class);
            Log.d("MainActivity", "starting landing page");
            startActivity(intent_first_login);
        } else {
            Log.d("MainActivity", "did not start landing page");
            init_widgets();
            setUI();
            loadingViewModel = new ViewModelProvider(this).get(MainLoadingViewModel.class);
            loadingViewModel.isLoading().observe(this, isLoading -> {
                if (isLoading) {
                    setUIvisibility(8);
                    hidden_layout.setVisibility(View.VISIBLE);
                } else {
                    setUIvisibility(0);
                    hidden_layout.setVisibility(View.GONE);
                }
            });
            onClick();
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
    }


    private void onClick() {
        btn.setOnClickListener(view -> {
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


    private void startImageActivity() {
        Log.d("MainActivity startImage method", "starting buffer");
        loadingViewModel.setLoading(true);

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

            Log.d("MainActivity startImage method", "starting image");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(() ->
                    loadingViewModel.setLoading(false), 1000);

        }).exceptionally(exception -> {
            // This code will also run in the main thread (UI thread)
            Toast.makeText(MainActivity.this,"Image fail",Toast.LENGTH_SHORT).show();
            loadingViewModel.setLoading(false);
            exception.printStackTrace();
            return null;
        });
    }

    private void init_widgets() {
        hidden_layout = bo.hiddenLayout;
        pencil_anim = bo.animationView;
        category_statement = bo.categoryStatement;
        spinner = bo.categoryBox;
        words_statement = bo.wordsStatement;
        first_word_box = bo.word1;
        second_word_box = bo.word2;
        third_word_box = bo.word3;
        btn = bo.generateButton;
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

    //finish affinity or no???
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

    private void setUIvisibility(int visibility) {
        pencil_anim.setVisibility(visibility);
        category_statement.setVisibility(visibility);
        spinner.setVisibility(visibility);
        words_statement.setVisibility(visibility);
        first_word_box.setVisibility(visibility);
        second_word_box.setVisibility(visibility);
        third_word_box.setVisibility(visibility);
        btn.setVisibility(visibility);
        Log.d("MainActivity line 356", "getting int value UI: " + btn.getVisibility());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy");
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

}