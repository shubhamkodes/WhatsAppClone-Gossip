package com.itsthetom.gossip.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itsthetom.gossip.R;
import com.itsthetom.gossip.databinding.StatusRvBinding;
import com.itsthetom.gossip.interfaces.ShowStatusListener;
import com.itsthetom.gossip.model.Status;
import com.itsthetom.gossip.model.StatusContainer;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class UserStatusAdapter extends RecyclerView.Adapter<UserStatusAdapter.UserStatusViewHolder>  {

    ShowStatusListener listener;
    ArrayList<StatusContainer> statusContainers;
    Context context;
    PrettyTime prettyTime;

    public UserStatusAdapter(Context context,ArrayList<StatusContainer> list,ShowStatusListener listener){
        statusContainers=list;
        this.context=context;
        prettyTime=new PrettyTime();
        this.listener=listener;
    }
    @Override
    public int getItemCount() {
        return statusContainers.size();
    }

    @NonNull
    @NotNull
    @Override
    public UserStatusViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.status_rv,parent,false);
        return new UserStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserStatusAdapter.UserStatusViewHolder holder, int position) {
                StatusContainer container=statusContainers.get(position);
                holder.binding.tvUserStatus.setText(container.getUserName());
                ArrayList<Status> userStatuses=container.getStatusArrayList();
                String lastImgUrl=userStatuses.get(userStatuses.size()-1).getStatusImgUrl();
                holder.binding.circularStatusView.setPortionsCount(container.getStatusArrayList().size());
                Glide.with(context).load(lastImgUrl).into(holder.binding.circularStatusImageView);

                Date date=new Date(container.getStatusLastTime());
                holder.binding.userStatusTime.setText(prettyTime.format(date));
                holder.binding.userStatusLayout.setOnClickListener((view)->{
                    listener.showStatus(container);
                });

    }

    public class UserStatusViewHolder extends RecyclerView.ViewHolder{
        StatusRvBinding binding;
        public UserStatusViewHolder(View view){
            super(view);
            binding=StatusRvBinding.bind(view);
        }
    }


}
