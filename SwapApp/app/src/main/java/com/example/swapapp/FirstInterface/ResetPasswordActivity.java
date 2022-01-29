package com.example.swapapp.FirstInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.swapapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout email;
    private TextView error;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        email = (TextInputLayout) findViewById(R.id.email_ResetPasswordTextInputLayout);

        error = (TextView) findViewById(R.id.error_ResetPasswordTextView);

        reset = (Button) findViewById(R.id.reset_ResetPasswordButton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpassword();
            }
        });

    }

    private void resetpassword() {
        String emailString = email.getEditText().getText().toString().trim();

        if (emailString.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }else{email.setError(null);}

        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Enter a valid email!");
            email.requestFocus();
            return;
        }else{email.setError(null);}

        mAuth.sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    error.setTextColor(Color.parseColor("#00FF00"));
                    error.setText("A reset link has been send to your email!");
                }else{
                    error.setTextColor(Color.parseColor("#FF0000"));
                    error.setText("An error occurred, please try again!");
                }
            }
        });
    }
}