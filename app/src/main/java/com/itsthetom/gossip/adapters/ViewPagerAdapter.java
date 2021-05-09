package com.itsthetom.gossip.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.itsthetom.gossip.fragments.CallsFragment;
import com.itsthetom.gossip.fragments.ChatsFragment;
import com.itsthetom.gossip.fragments.StatusFragment;
import com.itsthetom.gossip.interfaces.UserAdapterListener;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    UserAdapterListener listener;
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, UserAdapterListener listener) {
        super(fm, behavior);
        this.listener=listener;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new ChatsFragment(listener);
            case 1:return new StatusFragment();
            case 2:return new CallsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:return "Chats";
            case 1:return "Status";
            case 2:return "Calls";
        }
        return null;
    }
}
