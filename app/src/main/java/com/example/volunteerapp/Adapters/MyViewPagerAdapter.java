package com.example.volunteerapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.volunteerapp.Fragments.BfollowFragment;
import com.example.volunteerapp.Fragments.BintFragment;

public class MyViewPagerAdapter  extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new BintFragment();
            case 1:
                return new BfollowFragment();
            default:
                return new BintFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}