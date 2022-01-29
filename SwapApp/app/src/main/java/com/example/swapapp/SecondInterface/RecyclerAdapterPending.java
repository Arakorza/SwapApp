package com.example.swapapp.SecondInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swapapp.R;
import com.example.swapapp.Trade;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapterPending extends RecyclerView.Adapter<RecyclerAdapterPending.MyViewHolder> {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;
    private Uri imageUri;

    private Long time;

    private int largestKey;

    Context context;

    private ArrayList<String> postIDs;

    private String userPostedUID;
    private String userRequestingUID;
    private boolean accepted = false;
    private String chosenPost;

    public RecyclerAdapterPending(ArrayList<String> postIDs) {
        this.postIDs = postIDs;
        Log.d("check", postIDs.toString());
        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, time, bio, username, email, numOfRatings, numOfTrades;
        private ImageView image, pfp, rating1, rating2, rating3, rating4, rating5;
        private Button accept, decline;
        private ConstraintLayout mainLayout;

        public MyViewHolder(final View view) {
            super(view);

            title = view.findViewById(R.id.title_PostItemPending);
            time = view.findViewById(R.id.time_PostItemPending);
            bio = view.findViewById(R.id.bio_PostItemPending);
            username = view.findViewById(R.id.username_PostItemPending);
            email = view.findViewById(R.id.email_PostItemPending);
            numOfRatings = view.findViewById(R.id.numOfRatings_PostItemPending);
            numOfTrades = view.findViewById(R.id.numOfTrades_PostItemPending);

            rating1 = view.findViewById(R.id.rating1_PostItemPending);
            rating2 = view.findViewById(R.id.rating2_PostItemPending);
            rating3 = view.findViewById(R.id.rating3_PostItemPending);
            rating4 = view.findViewById(R.id.rating4_PostItemPending);
            rating5 = view.findViewById(R.id.rating5_PostItemPending);

            image = view.findViewById(R.id.image_PostItemPending);
            pfp = view.findViewById(R.id.pfp_PostItemPending);

            accept = view.findViewById(R.id.accept_PostItemPending);
            decline = view.findViewById(R.id.decline_PostItemPending);

            mainLayout = view.findViewById(R.id.mainLayout_PostItemPending);
        }


    }

    @NonNull
    @Override
    public RecyclerAdapterPending.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item_pending, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterPending.MyViewHolder holder, int position) {

        String postID = postIDs.get(position);

        Log.d("check", postIDs.toString());

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        SimpleDateFormat stf = new SimpleDateFormat();
        stf.setTimeZone(TimeZone.getTimeZone("EST"));

        sdf.applyPattern("MMM dd, yyyy");
        stf.applyPattern("HH:mm:ss");

        mFirebase.child("Posts").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (accepted == true) {
                    holder.accept.setBackgroundColor(Color.parseColor("#00AA00"));
                    holder.accept.setTextColor(Color.parseColor("#000000"));
                    holder.accept.setText("Accept");
                }

                String temp;
                Long numLong;

                temp = (String) snapshot.child("title").getValue();
                holder.title.setText(temp);

                time = (Long) snapshot.child("timeStamp").getValue();
                Date timePosted = new Date(time);
                holder.time.setText(sdf.format(timePosted) + " [" + stf.format(timePosted) + "]");

                userPostedUID = (String) snapshot.child("userPosted").getValue();
                userRequestingUID = (String) snapshot.child("wishlistBy").child("1").getValue();

                mFirebase.child("Users").child(userRequestingUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String temp1;
                        Long numLong1;

                        temp1 = (String) snapshot.child("username").getValue();
                        holder.username.setText(temp1);

                        temp1 = (String) snapshot.child("email").getValue();
                        holder.email.setText(temp1);

                        temp1 = (String) snapshot.child("bio").getValue();
                        holder.bio.setText(temp1);

                        Log.d("uid", userRequestingUID);

                        double ratingRounded = 5;

                        Long tempLong = (Long) snapshot.child("rating").getValue();
                        if (snapshot.child("rating").exists()) {
                            double ratingDouble = tempLong.doubleValue();
                            ratingRounded = ((int) (ratingDouble*2 + 0.5))/2.0;
                            Log.d("rounded", String.valueOf(ratingRounded));
                        }


                        holder.rating1.setImageResource(R.drawable.ic_baseline_star_border_24);
                        holder.rating2.setImageResource(R.drawable.ic_baseline_star_border_24);
                        holder.rating3.setImageResource(R.drawable.ic_baseline_star_border_24);
                        holder.rating4.setImageResource(R.drawable.ic_baseline_star_border_24);
                        holder.rating5.setImageResource(R.drawable.ic_baseline_star_border_24);

                        if (ratingRounded == 5.0) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating3.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating4.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating5.setImageResource(R.drawable.ic_baseline_star_24);
                        }
                        if (ratingRounded == 4.5) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating3.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating4.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating5.setImageResource(R.drawable.ic_baseline_star_half_24);
                        }
                        if (ratingRounded == 4.0) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating3.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating4.setImageResource(R.drawable.ic_baseline_star_24);
                        }
                        if (ratingRounded == 3.5) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating3.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating4.setImageResource(R.drawable.ic_baseline_star_half_24);
                        }
                        if (ratingRounded == 3.0) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating3.setImageResource(R.drawable.ic_baseline_star_24);
                        }
                        if (ratingRounded == 2.5) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating3.setImageResource(R.drawable.ic_baseline_star_half_24);
                        }
                        if (ratingRounded == 2.0) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_24);
                        }
                        if (ratingRounded == 1.5) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                            holder.rating2.setImageResource(R.drawable.ic_baseline_star_half_24);
                        }
                        if (ratingRounded == 1.0) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_24);
                        }
                        if (ratingRounded == 0.5) {
                            holder.rating1.setImageResource(R.drawable.ic_baseline_star_half_24);
                        }

                        numLong1 = (Long) snapshot.child("numOfRatings").getValue();
                        holder.numOfRatings.setText(String.valueOf(numLong1));

                        numLong1 = (Long) snapshot.child("numOfTrades").getValue();
                        holder.numOfTrades.setText(String.valueOf(numLong1 + " Trades"));

                        Log.d("check", postIDs.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                StorageReference islandRef = storageRef.child("pfp/" + userRequestingUID);

                final long FiveMB = 5 * 1024 * 1024;
                islandRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.pfp.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });

                storageRef = FirebaseStorage.getInstance().getReference();

                islandRef = storageRef.child("post/" + postID);

                islandRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.image.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference tradeRef = mFirebase.child("Trades");

                String key = tradeRef.push().getKey();

                tradeRef.child(key).setValue(new Trade(userPostedUID, postID, userRequestingUID));

                holder.accept.setBackgroundColor(Color.parseColor("#00AA00"));
                holder.accept.setTextColor(Color.parseColor("#000000"));
                holder.accept.setText("Accept");

                mFirebase.child("Users").child(userRequestingUID).child("wishlist").child("1").setValue("temp");
                mFirebase.child("Posts").child(postID).child("wishlistBy").child("1").setValue("temp");

                accepted = true;
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.decline.setBackgroundColor(Color.parseColor("#AA0000"));
                holder.decline.setTextColor(Color.parseColor("#000000"));
                holder.decline.setText("Decline");
            }
        });
    }

    @Override
    public int getItemCount() {
        return postIDs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

}
