package com.itsthetom.gossip.adapters;

import android.content.Context;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itsthetom.gossip.R;
import com.itsthetom.gossip.databinding.UserRvBinding;
import com.itsthetom.gossip.interfaces.UserAdapterListener;
import com.itsthetom.gossip.model.User;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    Context context;
    ArrayList<User> userArrayList;
    UserAdapterListener listener;
    FirebaseDatabase database;
    public UsersAdapter(Context con, ArrayList<User> userArrayList, UserAdapterListener listener){
        context=con;
        this.userArrayList=userArrayList;
        this.listener=listener;
        database=FirebaseDatabase.getInstance();
    }
    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull   ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_rv,parent,false);
        return new UserViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull  UsersAdapter.UserViewHolder holder, int position) {
        User user= userArrayList.get(position);
        holder.binding.rvLastMsg.setText("Tap to Chat");

        PrettyTime prettyTime=new PrettyTime();
        String senderRoom=FirebaseAuth.getInstance().getUid()+user.getUid();
        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                           try{
                               String lastMessage=snapshot.child("last_message").getValue(String.class);
                               long lastMessageTime=snapshot.child("last_message_time").getValue(Long.class);
                               Date lastTimeDate=new Date();
                               lastTimeDate.setTime(lastMessageTime);
                               holder.binding.rvTime.setText(prettyTime.format(lastTimeDate));
                               if(lastMessage.length()>40)
                                   holder.binding.rvLastMsg.setText(lastMessage.substring(0,40));
                               else holder.binding.rvLastMsg.setText(lastMessage);
                           }catch (Exception e){

                           }

                         }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });



        holder.binding.rvName.setText(user.getName());
        if(!user.getProfileImgUrl().equals("")){
            Glide.with(context).load(user.getProfileImgUrl()).into(holder.binding.rvProfilePic);
        }else{
            holder.binding.rvProfilePic.setImageResource(R.drawable.aavatar);
        }

       try{
           holder.binding.userRv.setOnClickListener((view)->{
               listener.startThisActivity(user);
           });
       }catch (Exception e){
           Log.d("XXX",e.getMessage().toString());
       }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        UserRvBinding binding;
        public UserViewHolder(View view){
            super(view);
            binding=UserRvBinding.bind(view);
        }
    }
}
