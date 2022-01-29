package com.example.swapapp.SecondInterface.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapapp.FirstInterface.LoginActivity;
import com.example.swapapp.R;
import com.example.swapapp.SecondInterface.CreatePostActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfile extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mFirebase;
    private String uid;
    private ImageView pfp, rating1, rating2, rating3, rating4, rating5;
    private TextView username, email, numOfTrades, contactInfo, bio, numOfRatings;
    private Button changeProfile, logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mFirebase = FirebaseDatabase.getInstance().getReference();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid", uid);

        pfp = (ImageView) findViewById(R.id.pfp_UserProfile);
        rating1 = (ImageView) findViewById(R.id.rating1_UserProfile);
        rating2 = (ImageView) findViewById(R.id.rating2_UserProfile);
        rating3 = (ImageView) findViewById(R.id.rating3_UserProfile);
        rating4 = (ImageView) findViewById(R.id.rating4_UserProfile);
        rating5 = (ImageView) findViewById(R.id.rating5_UserProfile);

        username = (TextView) findViewById(R.id.username_UserProfile);
        email = (TextView) findViewById(R.id.email_UserProfile);
        numOfTrades = (TextView) findViewById(R.id.numOfTrades_UserProfile);
        contactInfo = (TextView) findViewById(R.id.contactInfo_UserProfile);
        bio = (TextView) findViewById(R.id.bio_UserProfile);
        numOfRatings = (TextView) findViewById(R.id.numOfRatings_UserProfile);

        changeProfile = (Button) findViewById(R.id.changeProfile_UserProfile);
        changeProfile.setOnClickListener(this);
        logOut = (Button) findViewById(R.id.logOut_UserProfile);
        logOut.setOnClickListener(this);

        updateDisplayedInfo();
    }

    private void updateDisplayedInfo() {
        mFirebase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String str;
                Long numLong;

                str = (String) snapshot.child("username").getValue();
                username.setText(str);

                str = (String) snapshot.child("email").getValue();
                email.setText(str);

                numLong = (Long) snapshot.child("numOfTrades").getValue();
                numOfTrades.setText(numLong.toString() + " Trades");

                str = (String) snapshot.child("contactInfo").getValue();
                if (str.equals("")) {
                    contactInfo.setText("No Contact Info");
                }else {
                    contactInfo.setText(str);
                }

                str = (String) snapshot.child("bio").getValue();
                if (str.equals("")) {
                    bio.setText("No Bio");
                }else {
                    bio.setText(str);
                }

                Long tempLong = (Long) snapshot.child("rating").getValue();
                double ratingDouble = tempLong.doubleValue();
                double ratingRounded = ((int) (ratingDouble*2 + 0.5))/2.0;
                Log.d("rounded", String.valueOf(ratingRounded));
                updateRating(ratingRounded);

                numLong = (Long) snapshot.child("numOfRatings").getValue();
                numOfRatings.setText(numLong.toString());

                updatePicture();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void updateRating(double rating) {
        rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
        rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
        rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
        rating4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
        rating5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));

        if (rating == 5.0) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
        }
        if (rating == 4.5) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_half_24));
        }
        if (rating == 4.0) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
        }
        if (rating == 3.5) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_half_24));
        }
        if (rating == 3.0) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
        }
        if (rating == 2.5) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_half_24));
        }
        if (rating == 2.0) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
        }
        if (rating == 1.5) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
            rating2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_half_24));
        }
        if (rating == 1.0) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
        }
        if (rating == 0.5) {
            rating1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_half_24));
        }
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
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeProfile_UserProfile:
                startActivity(new Intent(this, EditProfile.class));
                break;
            case R.id.logOut_UserProfile:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}