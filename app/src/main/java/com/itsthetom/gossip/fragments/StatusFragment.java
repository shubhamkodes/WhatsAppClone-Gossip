package com.itsthetom.gossip.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itsthetom.gossip.MainActivity;
import com.itsthetom.gossip.R;
import com.itsthetom.gossip.adapters.UserStatusAdapter;
import com.itsthetom.gossip.databinding.FragmentStatusBinding;
import com.itsthetom.gossip.interfaces.ShowStatusListener;
import com.itsthetom.gossip.model.Status;
import com.itsthetom.gossip.model.StatusContainer;
import com.itsthetom.gossip.model.User;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusFragment extends Fragment implements ShowStatusListener {
    public static final int STATUS_IMG=6969;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FragmentStatusBinding binding;
    ArrayList<StatusContainer> statusContainers;
    UserStatusAdapter adapter;
    StatusContainer currentUserStatuses;
    public StatusFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentStatusBinding.inflate(inflater);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        statusContainers=new ArrayList<>();
        loadStatus();



        binding.btnUploadStatus.setOnClickListener((view)->{
                Intent i=new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,STATUS_IMG);
        });

        binding.myStatusLayout.setOnClickListener((view)->{
            try {
                showStatus(currentUserStatuses);
            }catch (Exception e){

            }

        });


        adapter=new UserStatusAdapter(getContext(),statusContainers,this);
        binding.statusRecyclerView.setAdapter(adapter);
        binding.statusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }
    private void loadStatus(){
        database.getReference().child("statuses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   statusContainers.clear();
                   for(DataSnapshot containerSnap:snapshot.getChildren()){
                       StatusContainer container=new StatusContainer();
                       container.setUserName(containerSnap.child("name").getValue(String.class));
                       try{
                           container.setStatusLastTime((long)containerSnap.child("lastStatus").getValue(Long.class));
                       }catch (Exception e){Log.d("XXX",e.getMessage().toString());}
                       container.setUserUid(containerSnap.getKey());
                       ArrayList<Status> statusArrayList=new ArrayList<>();
                       for(DataSnapshot snapList:containerSnap.child("user_statuses").getChildren()){
                           Status status=snapList.getValue(Status.class);
                           statusArrayList.add(status);
                       }
                       container.setStatusArrayList(statusArrayList);
                       if(container.getUserUid().equals(auth.getUid())) {
                           currentUserStatuses = container;
                           updateCurrentUserStatus();
                       }
                       else statusContainers.add(container);
                   }
                   adapter.notifyDataSetChanged();


               }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void updateCurrentUserStatus(){
        ArrayList<Status> statusArrayList=currentUserStatuses.getStatusArrayList();
        binding.circularMystatusView.setPortionsCount(statusArrayList.size());
        String imgUrl=statusArrayList.get(statusArrayList.size()-1).getStatusImgUrl();
        Glide.with(getContext()).load(imgUrl).into(binding.myStatusImage);


        PrettyTime prettyTime=new PrettyTime();
        Date date=new Date(currentUserStatuses.getStatusLastTime());
        binding.myLastStatusTime.setText(prettyTime.format(date));
    }

    private void uploadStatus(Uri uri){
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading status...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String statusChild=database.getReference().child("statuses").child(auth.getUid()).child("user_statuses").push().getKey();
        StorageReference reference= storage.getReference().child("statuses").child(auth.getUid()).child(statusChild);
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Date date=new Date();
                        Status status=new Status();
                        status.setStatusId(statusChild);
                        status.setStatusImgUrl(uri.toString());
                        status.setTimeStamp(date.getTime());
                        HashMap<String, Object> obj=new HashMap<>();
                        obj.put("name", MainActivity.currentUser.getName());
                        obj.put("lastStatus",date.getTime());
                        database.getReference().child("statuses").child(auth.getUid()).child("user_statuses").child(statusChild)
                                .setValue(status)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("XXX", "Status Uploaded Successfully");
                                    }
                                });
                        database.getReference().child("statuses").child(auth.getUid()).updateChildren(obj);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==STATUS_IMG){
            if(data!=null){
                if(data.getData()!=null){
                    Uri imgUrl=data.getData();
                    uploadStatus(imgUrl);
                }
            }
        }
    }

    public void showStatus(StatusContainer container){
        ArrayList<MyStory> myStories = new ArrayList<>();
        Date date;
        for(Status status: container.getStatusArrayList()){
            date=new Date(status.getTimeStamp());
            myStories.add(new MyStory(
                    status.getStatusImgUrl(),
                    date
            ));
        }
        new StoryView.Builder(getFragmentManager())
                .setStoriesList(myStories) // Required
                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                .setTitleText(container.getUserName()) // Default is Hidden
                .setTitleLogoUrl(MainActivity.currentUser.getProfileImgUrl()) // Default is Hidden
                .setStoryClickListeners(new StoryClickListeners() {
                    @Override
                    public void onDescriptionClickListener(int position) {
                        //your action
                    }

                    @Override
                    public void onTitleIconClickListener(int position) {
                        //your action
                    }
                }) // Optional Listeners
                .build() // Must be called before calling show method
                .show();
    }
}