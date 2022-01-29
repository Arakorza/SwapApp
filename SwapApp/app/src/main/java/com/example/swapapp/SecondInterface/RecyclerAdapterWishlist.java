package com.example.swapapp.SecondInterface;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swapapp.R;
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

public class RecyclerAdapterWishlist extends RecyclerView.Adapter<RecyclerAdapterWishlist.MyViewHolder> {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;
    private Uri imageUri;

    private Long time;

    private int largestKey;

    Context context;

    private ArrayList<String> postIDs;

    public RecyclerAdapterWishlist(ArrayList<String> postIDs) {
        this.postIDs = postIDs;
        Log.d("check", postIDs.toString());
        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, time, description, username, email, numOfRatings, numOfTrades;
        private ImageView image, pfp, rating1, rating2, rating3, rating4, rating5, wishlistedIcon;

        public MyViewHolder(final View view) {
            super(view);

            title = view.findViewById(R.id.title_PostItemWishlist);
            time = view.findViewById(R.id.time_PostItemWishlist);
            description = view.findViewById(R.id.description_PostItemWishlist);
            username = view.findViewById(R.id.username_PostItemWishlist);
            email = view.findViewById(R.id.email_PostItemWishlist);
            numOfRatings = view.findViewById(R.id.numOfRatings_PostItemWishlist);
            numOfTrades = view.findViewById(R.id.numOfTrades_PostItemWishlist);

            rating1 = view.findViewById(R.id.rating1_PostItemWishlist);
            rating2 = view.findViewById(R.id.rating2_PostItemWishlist);
            rating3 = view.findViewById(R.id.rating3_PostItemWishlist);
            rating4 = view.findViewById(R.id.rating4_PostItemWishlist);
            rating5 = view.findViewById(R.id.rating5_PostItemWishlist);

            image = view.findViewById(R.id.image_PostItemWishlist);
            pfp = view.findViewById(R.id.pfp_PostItemWishlist);
            wishlistedIcon = view.findViewById(R.id.wishlistedIcon_PostItemWishlist);
        }

    }

    @NonNull
    @Override
    public RecyclerAdapterWishlist.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item_wishlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterWishlist.MyViewHolder holder, int position) {
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

                String temp;
                Long numLong;
                String userPostedUID;

                temp = (String) snapshot.child("title").getValue();
                holder.title.setText(temp);

                time = (Long) snapshot.child("timeStamp").getValue();
                Date timePosted = new Date(time);
                holder.time.setText(sdf.format(timePosted) + " [" + stf.format(timePosted) + "]");

                temp = (String) snapshot.child("description").getValue();
                holder.description.setText(temp);

                userPostedUID = (String) snapshot.child("userPosted").getValue();

                mFirebase.child("Users").child(userPostedUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String temp1;
                        Long numLong1;

                        temp1 = (String) snapshot.child("username").getValue();
                        holder.username.setText(temp1);

                        temp1 = (String) snapshot.child("email").getValue();
                        holder.email.setText(temp1);

                        Log.d("uid", userPostedUID);

                        Long tempLong = (Long) snapshot.child("rating").getValue();
                        double ratingDouble = tempLong.doubleValue();
                        double ratingRounded = ((int) (ratingDouble*2 + 0.5))/2.0;
                        Log.d("rounded", String.valueOf(ratingRounded));

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

                StorageReference islandRef = storageRef.child("pfp/" + userPostedUID);

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

        holder.wishlistedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();

                String currentPostID = postIDs.get(currentPosition);
                Log.d("currentPostID", currentPostID);

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                mFirebase.child("Users").child(uid).child("wishlist").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                            String wishlistedPost = (String) childSnapshot.getValue();

                            if (wishlistedPost.equals(currentPostID)) {
                                childSnapshot.getRef().setValue("temp");
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                mFirebase.child("Posts").child(currentPostID).child("wishlistBy").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                            String userWishlisted = (String) childSnapshot.getValue();

                            if (userWishlisted.equals(uid)) {
                                childSnapshot.getRef().setValue("temp");
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /*holder.wishlistedIcon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);*/

        mFirebase.child("Users").child(uid).child("wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int currentPosition = holder.getAdapterPosition();
                String currentPostID = postIDs.get(currentPosition);

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.getValue().equals(currentPostID)) {
                        holder.wishlistedIcon.setColorFilter(ContextCompat.getColor(context, R.color.wishlisted), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
