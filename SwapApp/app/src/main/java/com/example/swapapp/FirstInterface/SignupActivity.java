package com.example.swapapp.FirstInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapapp.R;
import com.example.swapapp.User;
import com.example.swapapp.VerifyActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout email, username, password, repeatPassword;
    private Button register;
    private TextView error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        email = (TextInputLayout) findViewById(R.id.email_SignupTextInputLayout);
        username = (TextInputLayout) findViewById(R.id.username_SignupTextInputLayout);
        password = (TextInputLayout) findViewById(R.id.password_SignupTextInputLayout);
        repeatPassword = (TextInputLayout) findViewById(R.id.repeatPassword_SignupTextInputLayout);

        register = (Button) findViewById(R.id.register_SignupButton);

        error = (TextView) findViewById(R.id.error_SignupTextView);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser() {
        String emailString = email.getEditText().getText().toString().trim();
        String usernameString = username.getEditText().getText().toString().trim();
        String passwordString = password.getEditText().getText().toString().trim();
        String repeatPasswordString = repeatPassword.getEditText().getText().toString().trim();

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

        if (usernameString.isEmpty()) {
            username.setError("Username is required!");
            username.requestFocus();
            return;
        }else{username.setError(null);}

        if (passwordString.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }else{password.setError(null);}

        if (passwordString.length() < 6) {
            password.setError("Minimum password length is 6 characters!");
            password.requestFocus();
            return;
        }else{password.setError(null);}

        if (!passwordString.equals(repeatPasswordString)) {
            repeatPassword.setError("Passwords do not match!");
            repeatPassword.getEditText().getText().clear();
            repeatPassword.requestFocus();
            return;
        }else{repeatPassword.setError(null);}

        mAuth.createUserWithEmailAndPassword(emailString,passwordString)
             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()) {
                         User user = new User(emailString, usernameString);

                         FirebaseDatabase.getInstance().getReference("Users")
                                         .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                         .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()) {
                                     error.setTextColor(Color.parseColor("#00FF00"));
                                     error.setText("User has been registered!");
                                     addWishlist();
                                     clearAllTextInputLayouts();
                                 }else{
                                     error.setTextColor(Color.parseColor("#FF0000"));
                                     error.setText("Failed to register! Try again!");
                                 }
                             }
                         });
                     }else{
                         error.setTextColor(Color.parseColor("#FF0000"));
                         error.setText("Failed to register! Try again!");
                     }
                 }
             });
    }



    public void clearAllTextInputLayouts() {
        email.getEditText().getText().clear();
        username.getEditText().getText().clear();
        password.getEditText().getText().clear();
        repeatPassword.getEditText().getText().clear();

        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    public void openVerifyActivity() {
        Intent verifyActivityIntent = new Intent(this, VerifyActivity.class);
        startActivity(verifyActivityIntent);
    }

    public void addWishlist() {
        String emailString = email.getEditText().getText().toString().trim();
        String passwordString = password.getEditText().getText().toString().trim();
        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    List wishlist = new ArrayList();
                    wishlist.add("temp");
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("wishlist").setValue(wishlist);

                    FirebaseAuth.getInstance().signOut();
                }else{
                    error.setTextColor(Color.parseColor("#FF0000"));
                    error.setText("xxx");
                }
            }
        });
    }

}