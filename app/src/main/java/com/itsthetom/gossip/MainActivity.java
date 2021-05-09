package com.itsthetom.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itsthetom.gossip.adapters.UsersAdapter;
import com.itsthetom.gossip.adapters.ViewPagerAdapter;
import com.itsthetom.gossip.databinding.ActivityMainBinding;
import com.itsthetom.gossip.interfaces.UserAdapterListener;
import com.itsthetom.gossip.model.User;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements UserAdapterListener {
    ActivityMainBinding binding;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    public static User currentUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
    }

    private void initLayout(){


        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),1,this);
        ViewPager viewPager=findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout=findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        drawerLayout=findViewById(R.id.drawerLayout);

        Toolbar toolbar=findViewById(R.id.mainToolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        toggle.syncState();



        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        database.getReference().child("users")
                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            currentUser = snapshot.getValue(User.class);
                            setHeaderProfile(currentUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("CODEM",error.getMessage().toString());
                    }
                });
    }

    private void setHeaderProfile(User currentUser){
        if(currentUser!=null){
            TextView userName=findViewById(R.id.headerProfileName);
            userName.setText(currentUser.getName());
            CircleImageView userProfile=findViewById(R.id.headerProfilePic);
            Glide.with(getApplicationContext()).load(currentUser.getProfileImgUrl()).into(userProfile);
        }
    }

    public void startThisActivity(User user){
        if(user!=null){
            Intent i=new Intent(this,ChatsActivity.class);
            i.putExtra("receiverUid",user.getUid());
            i.putExtra("receiverImgUrl",user.getProfileImgUrl());
            i.putExtra("receiverName",user.getName());
            startActivity(i);
        }

    }





}