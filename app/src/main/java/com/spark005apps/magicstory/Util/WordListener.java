package com.spark005apps.magicstory.Util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class WordListener implements TextWatcher {
    private final EditText editText;

    public WordListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not used in this case
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not used in this case
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString().trim();
        if (text.contains(" ")) {
            // If the text contains a space (multiple words), remove the extra words
            String singleWord = text.split(" ")[0];
            editText.setText(singleWord);
            editText.setSelection(singleWord.length());
        }
    }
}
