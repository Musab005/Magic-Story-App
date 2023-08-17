package com.apps005.magicstory.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import com.apps005.magicstory.Util.NetworkChangeReceiver;
import com.apps005.magicstory.Util.NetworkRequest;
import com.apps005.magicstory.Util.MainLoadingViewModel;
import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.Util.WordListener;
import com.apps005.magicstory.databinding.ActivityMainBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//TODO:welcome username and network connection lost toast onconfigchanged should not show again
//TODO: landing page wifi off after login pressed?? loading widget not stoppin??
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseFirestore db;
    private NetworkRequest networkRequest;
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
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init_widgets();
        Toast.makeText(MainActivity.this, "welcome " + instance_SP.getUsername(), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        instance_SP.saveData(word1, word2, word3, category);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                if (isConnectedToInternet()) {
                    db = FirebaseFirestore.getInstance();
                    incrementImageCount();
                    startImageActivity();
                } else {
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void incrementImageCount() {
        CollectionReference usersCollection = db.collection("Users");
        String usernameToUpdate = instance_SP.getUsername();
        Map<String, Object> incrementData = new HashMap<>();
        incrementData.put("count_image", FieldValue.increment(1));

        usersCollection.whereEqualTo("username", usernameToUpdate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String documentId = documentSnapshot.getId();
                        usersCollection.document(documentId)
                                .update(incrementData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update is successful
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                    Toast.makeText(MainActivity.this,"ERROR: Please try again later",Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(MainActivity.this,"ERROR: Please try again later",Toast.LENGTH_SHORT).show();
                });
    }

    private void startImageActivity() {
        loadingViewModel.setLoading(true);
        CompletableFuture<String> future = networkRequest.
                generateImageAsync(word1, word2, word3, category);
        // Handling the result when it becomes available
        future.thenAccept(imageUrl -> {
            // This code will run in the main thread (UI thread)
            // Use imageUrl here to display the image or perform other actions
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            intent.putExtra("url", imageUrl);
            intent.putExtra("word1",word1);
            intent.putExtra("word2",word2);
            intent.putExtra("word3",word3);
            intent.putExtra("category",category);
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(() ->
                    loadingViewModel.setLoading(false), 1000);
        }).exceptionally(exception -> {
            // This code will also run in the main thread (UI thread)
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error loading image")
                    .setMessage("An unexpected error occurred.")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        // Do something if the user clicks "OK"
                        dialog.dismiss(); // Dismiss the dialog
                    })
                    .setCancelable(false) // Prevent dialog dismissal on outside touch or back press
                    .create()
                    .show();
            loadingViewModel.setLoading(false);
            exception.printStackTrace();
            return null;
        });
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                        (dialogInterface, i) -> MainActivity.super.onBackPressed()).create().show();
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
        instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        networkRequest = new NetworkRequest();

        // Register the BroadcastReceiver to monitor network changes
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
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

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.category = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

}