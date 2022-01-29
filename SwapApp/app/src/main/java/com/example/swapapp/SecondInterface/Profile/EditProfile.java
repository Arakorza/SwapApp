package com.example.swapapp.SecondInterface.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.swapapp.FirstInterface.LoginActivity;
import com.example.swapapp.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;
    private String uid;
    private ImageView pfp;
    private Button pfpButton, saveChanges, back;
    private TextInputLayout username, contactInfo, bio;
    private TextInputEditText usernameEdit, contactInfoEdit, bioEdit;
    private TextView error;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid", uid);

        pfp = (ImageView) findViewById(R.id.pfp_EditProfile);

        pfp = (ImageView) findViewById(R.id.pfp_EditProfile);
        pfp.setOnClickListener(this);
        pfpButton = (Button) findViewById(R.id.pfpButton_EditProfile);
        pfpButton.setOnClickListener(this);
        saveChanges = (Button) findViewById(R.id.saveChanges_EditProfile);
        saveChanges.setOnClickListener(this);
        back = (Button) findViewById(R.id.back_EditProfile);
        back.setOnClickListener(this);

        username = (TextInputLayout) findViewById(R.id.username_EditProfile);
        contactInfo = (TextInputLayout) findViewById(R.id.contactInfo_EditProfile);
        bio = (TextInputLayout) findViewById(R.id.bio_EditProfile);

        usernameEdit = (TextInputEditText) findViewById(R.id.username_EditProfileTextInputEditText);
        contactInfoEdit = (TextInputEditText) findViewById(R.id.contactInfo_EditProfileTextInputEditText);
        bioEdit = (TextInputEditText) findViewById(R.id.bio_EditProfileTextInputEditText);

        error = (TextView) findViewById(R.id.error_EditProfile);

        setUserInfo();

    }

    private void setUserInfo() {
        mFirebase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = "";

                temp = (String) snapshot.child("username").getValue();
                usernameEdit.setText(temp);

                temp = (String) snapshot.child("contactInfo").getValue();
                contactInfoEdit.setText(temp);

                temp = (String) snapshot.child("bio").getValue();
                bioEdit.setText(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        updatePicture();
    }

    private void saveUserEdits() {
        DatabaseReference userInfo = mFirebase.child("Users").child(uid);

        userInfo.child("username").setValue(username.getEditText().getText().toString());
        userInfo.child("contactInfo").setValue(contactInfo.getEditText().getText().toString());
        userInfo.child("bio").setValue(bio.getEditText().getText().toString());

        error.setTextColor(Color.parseColor("#00FF00"));
        error.setText("Your data has been updated!");
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
            pfp.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        StorageReference pfpRef = mStorage.child("pfp/" + uid);

        pfpRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        error.setTextColor(Color.parseColor("#00FF00"));
                        error.setText("Your profile picture has been updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setTextColor(Color.parseColor("#FF0000"));
                        error.setText("An error occurred when updating profile picture!");
                    }
                });
    }

    private void updatePicture() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference islandRef = storageRef.child("pfp/" + uid);

        final long FiveMB = 5 * 1024 * 1024;
        islandRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pfp.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                error.setTextColor(Color.parseColor("#FF0000"));
                error.setText("An error occurred when retrieving profile picture!");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pfp_EditProfile:
                choosePicture();
                break;
            case R.id.pfpButton_EditProfile:
                choosePicture();
                break;
            case R.id.saveChanges_EditProfile:
                saveUserEdits();
                break;
            case R.id.back_EditProfile:
                finish();
                break;
        }
    }
}