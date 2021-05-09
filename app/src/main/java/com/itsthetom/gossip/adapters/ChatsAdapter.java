package com.itsthetom.gossip.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.itsthetom.gossip.MainActivity;
import com.itsthetom.gossip.R;
import com.itsthetom.gossip.databinding.ReceiverMsgLayoutBinding;
import com.itsthetom.gossip.databinding.SenderMsgLayoutBinding;
import com.itsthetom.gossip.interfaces.ChatsAdapterListener;
import com.itsthetom.gossip.model.Message;
import com.itsthetom.gossip.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENDER_LAYOUT_ID=1;
    private static final int RECEIVER_LAYOUT_ID=2;
    ArrayList<Message> chatsArrayList;
    Context context;
    int[] reactions =new int[]{R.drawable.ic_angry,R.drawable.ic_heart,R.drawable.ic_in_love,R.drawable.ic_in_love,R.drawable.ic_laughing,R.drawable.ic_like};
    ChatsAdapterListener listener;

    public ChatsAdapter(Context context,ArrayList<Message> arrayList,ChatsAdapterListener listener ){
        this.chatsArrayList=arrayList;
        this.context=context;
        this.listener=listener;

    }
    @Override
    public int getItemCount() {
        return chatsArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Message message = chatsArrayList.get(position);
        int react=message.getReactions();


        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(pos>=0) {
                message.setReactions(pos);
                listener.updateReaction(message);
            }
            return true; // true is closing popup, false is requesting a new selection
        });


        if (holder.getClass() == SenderMsgViewHolder.class) {
            ((SenderMsgViewHolder) holder).senderBinding.senderMsg.setText(message.getMsg());
            if(react>=0){
                ((SenderMsgViewHolder) holder).senderBinding.senderReactions.setImageResource(reactions[react]);
                ((SenderMsgViewHolder) holder).senderBinding.senderReactions.setVisibility(View.VISIBLE);
              }

        } else if (holder.getClass() == ReceiverMsgViewHolder.class) {
            ((ReceiverMsgViewHolder) holder).receiverBinding.receiverMsg.setText(message.getMsg());
            ((ReceiverMsgViewHolder) holder).receiverBinding.receiverMsg.setOnTouchListener(popup);
            if (react >= 0) {
                ((ReceiverMsgViewHolder) holder).receiverBinding.receiverReactions.setImageResource(reactions[react]);
                ((ReceiverMsgViewHolder) holder).receiverBinding.receiverReactions.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        String whoIs=chatsArrayList.get(position).getSenderUid();
        if(MainActivity.currentUser.getUid().equals(whoIs))
            return SENDER_LAYOUT_ID;

        return RECEIVER_LAYOUT_ID;

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        if(viewType==SENDER_LAYOUT_ID){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_msg_layout,parent,false);
            return new SenderMsgViewHolder(view);
        }

        View view=LayoutInflater.from(context).inflate(R.layout.receiver_msg_layout,parent,false);
        return new ReceiverMsgViewHolder(view);

    }

    public class SenderMsgViewHolder extends RecyclerView.ViewHolder{
        SenderMsgLayoutBinding senderBinding;
        public SenderMsgViewHolder(View view){
            super(view);
            senderBinding=SenderMsgLayoutBinding.bind(view);
        }
    }
    public class ReceiverMsgViewHolder extends RecyclerView.ViewHolder{
        ReceiverMsgLayoutBinding receiverBinding;
        public ReceiverMsgViewHolder(View view){
            super(view);
            receiverBinding=ReceiverMsgLayoutBinding.bind(view);
        }
    }
}
