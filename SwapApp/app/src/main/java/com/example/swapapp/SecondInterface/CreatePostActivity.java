package com.example.swapapp.SecondInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;
    private String uid;
    private Uri imageUri;

    private ImageView image;
    private Button upload, post;
    private TextInputLayout title, description;
    private TextInputEditText titleEdit, descriptionEdit;
    private TextView error;

    private Boolean imageChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        image = (ImageView) findViewById(R.id.image_CreatePostImageView);

        upload = (Button) findViewById(R.id.uploadButton_CreatePostButton);
        upload.setOnClickListener(this);
        post = (Button) findViewById(R.id.post_CreatePost);
        post.setOnClickListener(this);

        title = (TextInputLayout) findViewById(R.id.title_CreatePostTextInputLayout);
        description = (TextInputLayout) findViewById(R.id.description_CreatePostTextInputLayout);

        titleEdit = (TextInputEditText) findViewById(R.id.title_CreatePostTextInputEditText);
        descriptionEdit = (TextInputEditText) findViewById(R.id.description_CreatePostTextInputEditText);

        error = (TextView) findViewById(R.id.error_CreatePostTextView);

        imageChosen = false;
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            imageChosen = true;
        }
    }

    private void uploadPicture(String key) {
        StorageReference pfpRef = mStorage.child("post/" + key);

        pfpRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        error.setTextColor(Color.parseColor("#00FF00"));
                        error.setText("Your post has been uploaded!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setTextColor(Color.parseColor("#FF0000"));
                        error.setText("An error occurred when uploading post!");
                    }
                });
    }

    private void sendPost() {

        if (imageChosen == false) {
            error.setTextColor(Color.parseColor("#FF0000"));
            error.setText("Please chose a picture!");
        } else {
            DatabaseReference posts = mFirebase.child("Posts");

            String key = posts.push().getKey();

            posts.child(key).child("userPosted").setValue(uid);
            posts.child(key).child("title").setValue(title.getEditText().getText().toString());
            posts.child(key).child("description").setValue(description.getEditText().getText().toString());
            posts.child(key).child("timeStamp").setValue(System.currentTimeMillis());
            posts.child(key).child("traded").setValue(false);

            List wishlistedBy = new ArrayList();
            Log.d("key", key);
            wishlistedBy.add("temp");
            mFirebase.child("Posts").child(key).child("wishlistBy").setValue(wishlistedBy);

            uploadPicture(key);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadButton_CreatePostButton:
                choosePicture();
                break;
            case R.id.post_CreatePost:
                sendPost();
                break;
        }
    }

}