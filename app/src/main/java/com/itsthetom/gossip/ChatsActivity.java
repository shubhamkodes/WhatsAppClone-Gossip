package com.itsthetom.gossip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itsthetom.gossip.adapters.ChatsAdapter;
import com.itsthetom.gossip.databinding.ActivityChatsBinding;
import com.itsthetom.gossip.interfaces.ChatsAdapterListener;
import com.itsthetom.gossip.model.Message;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatsActivity extends AppCompatActivity implements ChatsAdapterListener   {
    ActivityChatsBinding binding;
    private String senderRoom,receiverRoom;
    ArrayList<Message> messageArrayList;
    FirebaseDatabase firebaseDatabase;
    ChatsAdapter chatsAdapter;
    String receiverName,receiverImgUrl;
    Date date;
    String senderUid,receiverUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDatabase=FirebaseDatabase.getInstance();
        date=new Date();
        senderUid=FirebaseAuth.getInstance().getUid();
        receiverUid=getIntent().getStringExtra("receiverUid");
        receiverImgUrl=getIntent().getStringExtra("receiverImgUrl");
        receiverName=getIntent().getStringExtra("receiverName");


        senderRoom=senderUid+receiverUid;
        receiverRoom=receiverUid+senderUid;

        messageArrayList=new ArrayList<>();
        chatsAdapter=new ChatsAdapter(this,messageArrayList ,this);
        binding.chatsRecyclerView.setAdapter(chatsAdapter);

        loadChats();

        Glide.with(this).load(receiverImgUrl).into(binding.userProfile);
        binding.chatToolbarTitle.setText(receiverName);
        try {
            binding.btnSendMessage.setOnClickListener((view)->{
                sendMessage();
            });

            binding.btnBackChat.setOnClickListener((view)->{
                super.onBackPressed();
            });


        }catch (Exception e){
            Log.d("XXX",e.getMessage().toString());
        }

    }

    private void loadChats(){
        firebaseDatabase.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                       messageArrayList.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot chatSnap:snapshot.getChildren()){
                                Message message=chatSnap.getValue(Message.class);
                                messageArrayList.add(message);
                            }
                            chatsAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }
    public void sendMessage(){
        String text=binding.inputMsg.getText().toString().trim();
        if(text.equals("")){
            binding.inputMsg.setError("Message is empty");
        }else{
            binding.inputMsg.setText("");
            String msgId=firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages").push().getKey();
            Message message=new Message();
            message.setMsg(text);
            message.setReactions(-1);
            message.setMsgId(msgId);
            message.setTimeStamp(date.getTime());
            message.setSenderUid(senderUid);

            HashMap<String,Object> lastMsgObj=new HashMap<>();
            lastMsgObj.put("last_message",text);
            lastMsgObj.put("last_message_time",date.getTime());



            firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages").child(msgId).setValue(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                firebaseDatabase.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                Log.d("XXX","Message Sent to Sender Room");
                            }else{
                                Log.d("XXX", "Message Fail to send to Sender Room");
                            }
                        }
                    });
            firebaseDatabase.getReference().child("chats").child(receiverRoom).child("messages").child(msgId).setValue(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                firebaseDatabase.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                                Log.d("XXX","Message Sent to Receiver Room");
                            }else{
                                Log.d("XXX", "Message Fail to send to Receiver Room");
                            }
                        }
                    });
        }
    }

    public void updateReaction(Message message){
                firebaseDatabase.getReference().child("chats").child(senderRoom).child(message.getMsgId()).setValue(message);
                firebaseDatabase.getReference().child("chats").child(receiverRoom).child(message.getMsgId()).setValue(message);
    }
}