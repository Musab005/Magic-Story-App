package com.example.magicstory2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.magicstory2.R;
import com.example.magicstory2.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityMainBinding bo;
    private EditText first_word_box;
    private EditText second_word_box;
    private EditText third_word_box;
    private Intent intent;
    private final int REQ_CODE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set Content View
        bo = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //Create intent
        intent = new Intent(this, Story.class);
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
            if (first_word_box.getText().toString().equals("") || second_word_box.getText().toString().equals("") || third_word_box.getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "Enter 3 words and choose a category", Toast.LENGTH_SHORT).show();
            } else {
                intent.putExtra("word1", first_word_box.getText().toString().trim());
                intent.putExtra("word2", second_word_box.getText().toString().trim());
                intent.putExtra("word3", third_word_box.getText().toString().trim());
                //TODO
                startActivityForResult(intent, REQ_CODE);
            }
        });


        String google_URL = "https://www.google.com";
        StringRequest sr = new StringRequest(Request.Method.GET, google_URL,
                (Response.Listener<String>) response -> {
            intent.putExtra("response",response.substring(0,10000));

        }, (Response.ErrorListener) error -> {
            intent.putExtra("response", "error");
        });

        // Add a request to your RequestQueue.
        NetworkManager.getInstance(this).addToRequestQueue(sr);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.REQ_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String value = data.getStringExtra("message_back");
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        intent.putExtra("Category",choice);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



}