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
import android.widget.RelativeLayout;
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

public class RecyclerAdapterTradeLog extends RecyclerView.Adapter<RecyclerAdapterTradeLog.MyViewHolder> {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;
    private Uri imageUri;

    private Long time;

    private int largestKey;

    Context context;

    private ArrayList<String> tradeIDs;

    private String userPostedUID;
    private boolean accepted = false;
    private String chosenPost;

    private String uid;

    private Long timeStamp;
    private boolean user1Status, user2Status;
    private String user1UID, user1Post, user2UID, user2Post;

    private String userUsername, userEmail, userContactInfo, userBio;
    private Long userRating, userNumOfRatings, userNumOfTrades;

    private String user1PostTitle, user1PostDescription;

    private String user2PostTitle, user2PostDescription;

    private String userPFP;
    private final long FiveMB = 5 * 1024 * 1024;

    private boolean user2PostAdded = true, isUser1;

    StorageReference storageRef;
    StorageReference childRef;

    public RecyclerAdapterTradeLog(ArrayList<String> tradeIDs) {
        this.tradeIDs = tradeIDs;
        Log.d("check", tradeIDs.toString());
        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView pfp, image1, image2, rating1, rating2, rating3, rating4, rating5;
        private TextView username, email, numOfRatings, numOfTrades, contactInfo, bio;
        private TextView time, title1, description1, title2, description2;
        private Button rateUser;
        private RelativeLayout boxOutline2;

        public MyViewHolder(final View view) {
            super(view);

            pfp = view.findViewById(R.id.pfp_PostItemTradeLog);

            image1 = view.findViewById(R.id.image1_PostItemTradeLog);
            image2 = view.findViewById(R.id.image2_PostItemTradeLog);

            rating1 = view.findViewById(R.id.rating1_PostItemTradeLog);
            rating2 = view.findViewById(R.id.rating2_PostItemTradeLog);
            rating3 = view.findViewById(R.id.rating3_PostItemTradeLog);
            rating4 = view.findViewById(R.id.rating4_PostItemTradeLog);
            rating5 = view.findViewById(R.id.rating5_PostItemTradeLog);

            username = view.findViewById(R.id.username_PostItemTradeLog);
            email = view.findViewById(R.id.email_PostItemTradeLog);
            numOfRatings = view.findViewById(R.id.numOfRatings_PostItemTradeLog);
            numOfTrades = view.findViewById(R.id.numOfTrades_PostItemTradeLog);
            contactInfo = view.findViewById(R.id.contactInfo_PostItemTradeLog);
            bio = view.findViewById(R.id.bio_PostItemTradeLog);

            time = view.findViewById(R.id.time_PostItemTradeLog);
            title1 = view.findViewById(R.id.title1_PostItemTradeLog);
            description1 = view.findViewById(R.id.description1_PostItemTradeLog);
            title2 = view.findViewById(R.id.title2_PostItemTradeLog);
            description2 = view.findViewById(R.id.description2_PostItemTradeLog);

            rateUser = view.findViewById(R.id.rate_PostItemTradeLog);

            boxOutline2 = view.findViewById(R.id.boxOutline2_PostItemTradeLog);
        }

    }

    @NonNull
    @Override
    public RecyclerAdapterTradeLog.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item_trade_log, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterTradeLog.MyViewHolder holder, int position) {

        String tradeID = tradeIDs.get(position);

        Log.d("check", tradeIDs.toString());

        isUser1 = false;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Get Trade Info
        mFirebase.child("Trades").child(tradeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeStamp = (Long) snapshot.child("timeStamp").getValue();

                user1Status = (boolean) snapshot.child("user1Status").getValue();
                user1UID = (String) snapshot.child("user1UID").getValue();
                user1Post = (String) snapshot.child("user1Post").getValue();

                user2Status = (boolean) snapshot.child("user2Status").getValue();
                user2UID = (String) snapshot.child("user2UID").getValue();
                if (snapshot.child("user2Post").exists() || !snapshot.child("user2Post").getValue().equals("")) {
                    user2Post = (String) snapshot.child("user2Post").getValue();
                    user2PostAdded = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (uid.equals(user1UID)) {
            isUser1 = true;
        }

        //Get Other User Info
        if (isUser1) {
            mFirebase.child("Users").child(user2UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userUsername = (String) snapshot.child("username").getValue();
                    userEmail = (String) snapshot.child("email").getValue();
                    userContactInfo = (String) snapshot.child("contactInfo").getValue();
                    userBio = (String) snapshot.child("bio").getValue();

                    userRating = (Long) snapshot.child("rating").getValue();
                    userNumOfRatings = (Long) snapshot.child("numOfRatings").getValue();
                    userNumOfTrades = (Long) snapshot.child("numOfTrades").getValue();

                    userPFP = user2UID;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            mFirebase.child("Users").child(user1UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userUsername = (String) snapshot.child("username").getValue();
                    userEmail = (String) snapshot.child("email").getValue();
                    userContactInfo = (String) snapshot.child("contactInfo").getValue();
                    userBio = (String) snapshot.child("bio").getValue();

                    userRating = (Long) snapshot.child("rating").getValue();
                    userNumOfRatings = (Long) snapshot.child("numOfRatings").getValue();
                    userNumOfTrades = (Long) snapshot.child("numOfTrades").getValue();

                    userPFP = user1UID;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //Displaying Other User Info
        storageRef = FirebaseStorage.getInstance().getReference();
        childRef = storageRef.child("pfp/" + userPFP);

        childRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        holder.username.setText(userUsername);
        holder.email.setText(userEmail);
        holder.numOfRatings.setText(String.valueOf(userNumOfRatings));
        holder.numOfTrades.setText(String.valueOf(userNumOfTrades));
        holder.contactInfo.setText(userContactInfo);
        holder.bio.setText(userBio);

        double ratingDouble = userRating.doubleValue();
        double ratingRounded = (((int) (ratingDouble*2 + 0.5))/2.0);

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

        //Displaying Time
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        SimpleDateFormat stf = new SimpleDateFormat();
        stf.setTimeZone(TimeZone.getTimeZone("EST"));

        sdf.applyPattern("MMM dd, yyyy");
        stf.applyPattern("HH:mm:ss");

        Date timePosted = new Date(timeStamp);
        holder.time.setText(sdf.format(timePosted) + " [" + stf.format(timePosted) + "]");

        //Getting User 1 Post Data
        mFirebase.child("Posts").child(user1Post).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user1PostTitle = (String) snapshot.child("title").getValue();
                user1PostDescription = (String) snapshot.child("description").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Getting User 2 Post Data
        if (user2PostAdded) {
            mFirebase.child("Posts").child(user2Post).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user2PostTitle = (String) snapshot.child("title").getValue();
                    user2PostDescription = (String) snapshot.child("description").getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //Setting Up UI
        if (isUser1) {
            if (!user2PostAdded) {
                holder.title1.setText("Not Available");
                holder.description1.setText("The user you are trying to trade with as not added an offer");
            }

            holder.title2.setText(user1PostTitle);
            holder.description2.setText(user1PostDescription);

            storageRef = FirebaseStorage.getInstance().getReference();
            childRef = storageRef.child("post/" + user1Post);

            childRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.image2.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } else {
            holder.title1.setText(user1PostTitle);
            holder.description1.setText(user1PostDescription);

            storageRef = FirebaseStorage.getInstance().getReference();
            childRef = storageRef.child("post/" + user1Post);

            childRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.image1.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });

            if (!user2PostAdded) {
                holder.title2.setVisibility(View.INVISIBLE);
                holder.description2.setVisibility(View.INVISIBLE);
                holder.boxOutline2.setVisibility(View.INVISIBLE);
            } else {
                holder.title2.setText(user2PostTitle);
                holder.description2.setText(user2PostDescription);

                childRef = storageRef.child("post/" + user2Post);

                childRef.getBytes(FiveMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.image2.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        }

        holder.rateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RateUser.class);

                if (isUser1) {
                    intent.putExtra("uid", user2UID);
                } else {
                    intent.putExtra("uid", user1UID);
                }

                ((Activity) context).startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        chosenPost = data.getStringExtra("chosenPostID");
        //Access Firebase
    }

    @Override
    public int getItemCount() {
        return tradeIDs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        context = recyclerView.getContext();
    }

}