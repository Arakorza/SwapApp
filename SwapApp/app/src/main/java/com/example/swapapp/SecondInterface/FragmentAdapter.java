package com.example.swapapp.SecondInterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.swapapp.SecondInterface.Fragments.PendingFragment;
import com.example.swapapp.SecondInterface.Fragments.SearchFragment;
import com.example.swapapp.SecondInterface.Fragments.TradingFragment;
import com.example.swapapp.SecondInterface.Fragments.WishlistFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new SearchFragment();
            case 1:
                return new WishlistFragment();
            case 2:
                return new PendingFragment();
        }

        return new TradingFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
