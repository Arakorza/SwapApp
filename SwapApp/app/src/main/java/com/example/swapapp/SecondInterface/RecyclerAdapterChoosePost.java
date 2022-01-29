package com.example.swapapp.SecondInterface;

import static android.app.Activity.RESULT_OK;

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

public class RecyclerAdapterChoosePost extends RecyclerView.Adapter<RecyclerAdapterChoosePost.MyViewHolder> {

    private DatabaseReference mFirebase;
    private StorageReference mStorage;
    private Uri imageUri;

    private Long time;

    private int largestKey;

    Context context;

    private ArrayList<String> postIDs;

    public RecyclerAdapterChoosePost(ArrayList<String> postIDs) {
        this.postIDs = postIDs;
        Log.d("check", postIDs.toString());
        mFirebase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, time, description;
        private ImageView image;
        private Button choose;

        public MyViewHolder(final View view) {
            super(view);

            title = view.findViewById(R.id.title_PostItemChoosePost);
            time = view.findViewById(R.id.time_PostItemChoosePost);
            description = view.findViewById(R.id.description_PostItemChoosePost);

            image = view.findViewById(R.id.image_PostItemChoosePost);

            choose = view.findViewById(R.id.choose_PostItemChoosePost);
        }

    }

    @NonNull
    @Override
    public RecyclerAdapterChoosePost.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item_choose_post, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterChoosePost.MyViewHolder holder, int position) {
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

                temp = (String) snapshot.child("title").getValue();
                holder.title.setText(temp);

                time = (Long) snapshot.child("timeStamp").getValue();
                Date timePosted = new Date(time);
                holder.time.setText(sdf.format(timePosted) + " [" + stf.format(timePosted) + "]");

                temp = (String) snapshot.child("description").getValue();
                holder.description.setText(temp);


                final long FiveMB = 5 * 1024 * 1024;

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                StorageReference islandRef = storageRef.child("post/" + postID);

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

        holder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("chosenPostID", postID);
                ((Activity) context).setResult(RESULT_OK, intent);
                ((Activity) context).finish();
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
