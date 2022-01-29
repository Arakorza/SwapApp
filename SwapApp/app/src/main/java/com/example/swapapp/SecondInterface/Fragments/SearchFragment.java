package com.example.swapapp.SecondInterface.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.swapapp.R;
import com.example.swapapp.SecondInterface.RecyclerAdapterSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.platforminfo.DefaultUserAgentPublisher;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SearchFragment extends Fragment {

    private DatabaseReference mFirebase;
    private String uid;

    private RecyclerView recyclerView;

    private ArrayList<String> postIDs;

    @Nullable
    @Override
    public synchronized View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mFirebase = FirebaseDatabase.getInstance().getReference();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid", uid);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_SearchFragment);

        postIDs = new ArrayList<String>();

        setPostIDs();

        return view;
    }

    private synchronized void setPostIDs() {
        postIDs.clear();

        mFirebase.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(@NonNull DataSnapshot postDataSnapshot) {
                for (DataSnapshot postSnapshot : postDataSnapshot.getChildren()){

                    if (postSnapshot.child("userPosted").exists()) {
                        String userPosted = (String) postSnapshot.child("userPosted").getValue();

                        Boolean traded = (Boolean) postSnapshot.child("traded").getValue();

                        if (!userPosted.equals(uid) && traded.equals(false)) {
                            Boolean wishlisted = false;

                            for (DataSnapshot wishlistedSnapshot : postSnapshot.child("wishlistBy").getChildren()) {

                                if (wishlistedSnapshot.getValue().equals(uid)) {
                                    wishlisted = true;
                                }
                            }

                            if (wishlisted.equals(false)) {
                                postIDs.add((String) postSnapshot.getKey());
                            }
                        }
                    }

                }
                setAdapter();
            }

            @Override
            public synchronized void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private synchronized void setAdapter() {
        Log.d("check", postIDs.toString());
        RecyclerAdapterSearch adapter = new RecyclerAdapterSearch(postIDs);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView);
    }

}
