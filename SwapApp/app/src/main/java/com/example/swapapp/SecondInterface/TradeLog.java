package com.example.swapapp.SecondInterface;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class TradeLog extends AppCompatActivity {

    private DatabaseReference mFirebase;
    private String uid;

    private RecyclerView recyclerView;

    private ArrayList<String> postIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_log);

        mFirebase = FirebaseDatabase.getInstance().getReference();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("uid", uid);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_TradeLog);

        postIDs = new ArrayList<String>();

        setPostIDs();
    }

    private synchronized void setPostIDs() {
        postIDs.clear();

        mFirebase.child("Trades").addValueEventListener(new ValueEventListener() {
            boolean status;
            String user1UID, user2UID;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    status = (boolean) childSnapshot.child("tradeStatus").getValue();
                    if (status && (user1UID.equals(uid) || user2UID.equals(uid))) {
                        postIDs.add((String) childSnapshot.getKey());
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private synchronized void setAdapter() {
        Log.d("check", postIDs.toString());
        RecyclerAdapterTradeLog adapter = new RecyclerAdapterTradeLog(postIDs);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView);
    }

}
