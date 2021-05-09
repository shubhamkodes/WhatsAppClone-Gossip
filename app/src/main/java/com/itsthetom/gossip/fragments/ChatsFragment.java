package com.itsthetom.gossip.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itsthetom.gossip.R;
import com.itsthetom.gossip.adapters.UsersAdapter;
import com.itsthetom.gossip.databinding.FragmentChatsBinding;
import com.itsthetom.gossip.interfaces.UserAdapterListener;
import com.itsthetom.gossip.model.User;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {
    FragmentChatsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    ArrayList<User> userArrayList;
    UsersAdapter usersAdapter;
    UserAdapterListener listener;
    public ChatsFragment() {
        // Required empty public constructor
    }
    public ChatsFragment(UserAdapterListener listener){
        this.listener=listener;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        binding=FragmentChatsBinding.inflate(getLayoutInflater());

        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        userArrayList=new ArrayList<>();


        usersAdapter=new UsersAdapter(getContext(),userArrayList,listener);
        binding.usersRecyclerView.setAdapter(usersAdapter);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadUser();
        return binding.getRoot();
    }

    private void loadUser(){
        binding.usersRecyclerView.showShimmerAdapter();
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userArrayList.clear();
                    for(DataSnapshot userSnap:snapshot.getChildren()){
                        User user=userSnap.getValue(User.class);
                        if(!user.getUid().equals(firebaseAuth.getUid()))
                            userArrayList.add(user);
                    }

                    usersAdapter.notifyDataSetChanged();
                    binding.usersRecyclerView.hideShimmerAdapter();

                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }





}