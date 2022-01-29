package com.example.swapapp.SecondInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.swapapp.R;
import com.example.swapapp.SecondInterface.Fragments.PendingFragment;
import com.example.swapapp.SecondInterface.Fragments.SearchFragment;
import com.example.swapapp.SecondInterface.Fragments.TradingFragment;
import com.example.swapapp.SecondInterface.Fragments.WishlistFragment;
import com.example.swapapp.SecondInterface.Profile.UserProfile;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    ChipNavigationBar chipNavigationBar;
    Button createPost;
    ImageView profileButton, logButton;
    ViewPager2 viewPager;
    FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        createPost = (Button) findViewById(R.id.create_post_button);
        createPost.setOnClickListener(this);

        profileButton = (ImageView) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);
        logButton = (ImageView) findViewById(R.id.log_button);
        logButton.setOnClickListener(this);

        chipNavigationBar = findViewById(R.id.bottom_navigation);

        viewPager = findViewById(R.id.viewPager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(fragmentAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int iid = 0;

                switch (position) {
                    case 0:
                        iid = 2131230821;
                        break;
                    case 1:
                        iid = 2131230823;
                        break;
                    case 2:
                        iid = 2131230820;
                        break;
                    case 3:
                        iid = 2131230822;
                        break;
                }

                chipNavigationBar.setItemSelected(iid, true);
            }
        });

        bottomMenu();
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                int iid = 0;
                switch (i) {
                    case R.id.bottom_nav_search:
                        viewPager.setCurrentItem(0);
                        iid = chipNavigationBar.getSelectedItemId();
                        Log.d("search id", "Search " + String.valueOf(iid));
                        break;
                    case R.id.bottom_nav_wishlist:
                        viewPager.setCurrentItem(1);
                        iid = chipNavigationBar.getSelectedItemId();
                        Log.d("wishlist id", "Wishlist " + String.valueOf(iid));
                        break;
                    case R.id.bottom_nav_pending:
                        viewPager.setCurrentItem(2);
                        iid = chipNavigationBar.getSelectedItemId();
                        Log.d("pending id", "Pending " + String.valueOf(iid));
                        break;
                    case R.id.bottom_nav_trading:
                        viewPager.setCurrentItem(3);
                        iid = chipNavigationBar.getSelectedItemId();
                        Log.d("trading id", "Trading " + String.valueOf(iid));
                        break;
                }
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_post_button:
                startActivity(new Intent(this, CreatePostActivity.class));
                break;
            case R.id.profile_button:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.log_button:
                startActivity(new Intent(this, TradeLog.class));
                break;
        }
    }

}