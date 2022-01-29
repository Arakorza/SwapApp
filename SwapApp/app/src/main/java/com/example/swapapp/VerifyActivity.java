package com.example.swapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class VerifyActivity extends AppCompatActivity {

    TextInputEditText code1, code2, code3, code4, code5, code6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        code1 = findViewById(R.id.code1_VerifyTextInputEditText);
        code2 = findViewById(R.id.code2_VerifyTextInputEditText);
        code3 = findViewById(R.id.code3_VerifyTextInputEditText);
        code4 = findViewById(R.id.code4_VerifyTextInputEditText);
        code5 = findViewById(R.id.code5_VerifyTextInputEditText);
        code6 = findViewById(R.id.code6_VerifyTextInputEditText);

        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (code1.getText().toString().length()==1) {
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (code2.getText().toString().length()==1) {
                    code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (code3.getText().toString().length()==1) {
                    code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (code4.getText().toString().length()==1) {
                    code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (code5.getText().toString().length()==1) {
                    code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }
}