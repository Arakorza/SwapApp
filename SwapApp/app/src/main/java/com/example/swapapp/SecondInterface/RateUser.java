package com.example.swapapp.SecondInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.swapapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RateUser extends AppCompatActivity {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;

    private String uid, userUsername, userEmail;
    private long oldRating, newRating, numOfRatings;
    private final long FiveMB = 5 * 1024 * 1024;

    private TextView username, email;
    private ImageView pfp;
    private RatingBar ratingBar;
    private Button rateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);

        ratingBar = findViewById(R.id.ratingBar_PostItemRateUser);
        rateUser = findViewById(R.id.rate_PostItemRateUser);

        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        uid = getIntent().getStringExtra("uid");

        mFirebase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userUsername = (String) snapshot.child("username").getValue();
                userEmail = (String) snapshot.child("email").getValue();

                oldRating = (long) snapshot.child("rating").getValue();
                numOfRatings = (long) snapshot.child("numOfRatings").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        username = findViewById(R.id.username_PostItemRateUser);
        username.setText(userUsername);
        email = findViewById(R.id.email_PostItemRateUser);
        email.setText(userEmail);

        pfp = findViewById(R.id.pfp_PostItemRateUser);
        StorageReference childRef = mStorage.child("pfp/" + uid);
        childRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                pfp.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        rateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long ratingGiven = (long) ratingBar.getRating();
                newRating = (oldRating*numOfRatings + ratingGiven)/(numOfRatings + 1);

                mFirebase.child("Users").child(uid).child("rating").setValue(newRating);
                mFirebase.child("Users").child(uid).child("numOfRatings").setValue(numOfRatings + 1);

                finish();
            }
        });



    }
}