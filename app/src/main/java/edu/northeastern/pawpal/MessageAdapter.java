package edu.northeastern.pawpal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

//part of code learn from: https://stackoverflow.com/questions/44968837/why-i-can-not-read-and-display-data-from-firebase-real-time-database

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<MessageModel> list;
//    Activity activity;
//    String userName;

    int send = 0, received = 1;
    FirebaseUser fuser;

    public MessageAdapter() {
    }

    public MessageAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        //this.list = list;
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }

        //this.state = false;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==send)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.send_layout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.received_layout,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messages= list.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());

        }
        else
        {
            RecieverViewHolder viewHolder=(RecieverViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());

        }








    }


    @Override
    public int getItemViewType(int position) {
        MessageModel messages = list.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId()))

        {
            return send;
        }
        else
        {
            return received;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }








    class SenderViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewmessaage;



        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendView);
        }
    }

    class RecieverViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewmessaage;



        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.receivedView);
        }
    }




}
