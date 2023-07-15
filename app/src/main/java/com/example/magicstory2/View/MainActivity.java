package com.example.magicstory2.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.magicstory2.R;
import com.example.magicstory2.controller.StoryController;
import com.example.magicstory2.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public interface startActivity {
        void startActivity2(String story);
        void showError2(String error);
    }
    private ActivityMainBinding bo;
    private EditText first_word_box;
    private EditText second_word_box;
    private EditText third_word_box;
    private String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "Creating main activity");
        super.onCreate(savedInstanceState);
        StoryController.getInstance().setMainActivity(this);
        Log.d("MainActivity", "Context set.");
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

        //Generate Button - On Click
        bo.generateButton.setOnClickListener(view -> {
            Log.d("Main Activity:", "button clicked");
            if (first_word_box.getText().toString().equals("") ||
                    second_word_box.getText().toString().equals("") ||
                    third_word_box.getText().toString().equals("")) {
                Log.d("Main Activity:", "incorrect arguments");
                Toast.makeText(MainActivity.this,
                        "Enter 3 words and choose a category",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d("MainActivity", "calling generate story from controller");
                StoryController.getInstance().generateStory(
                        first_word_box.getText().toString().trim(),
                        second_word_box.getText().toString().trim(),
                        third_word_box.getText().toString().trim(),
                        category, new startActivity() {
                            @Override
                            public void startActivity2(String story) {
                                Intent intent3 = new Intent(MainActivity.this, Story.class);
                                intent3.putExtra("story", story);
                                Log.d("MainActivity", "Starting new activity");
                                startActivity(intent3);
                            }

                            @Override
                            public void showError2(String error) {
                                Log.d("MainActivity", "on error");
                                Toast.makeText(MainActivity.this, "Error: " + error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.category = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(MainActivity.this,
                category, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this,
                "Please select a category", Toast.LENGTH_LONG).show();
    }

}