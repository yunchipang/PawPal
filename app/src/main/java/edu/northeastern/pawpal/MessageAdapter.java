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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    List<MessageModel> list;
//    Activity activity;
//    String userName;
//    Boolean state;
    int send = 0, received = 1;
    FirebaseUser fuser;

    public MessageAdapter() {
    }

    public MessageAdapter(Context context, List<MessageModel> list) {
        this.context = context;
        //this.list = list;
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
//        this.activity = activity;
//        this.userName = userName;
//        state = false;
    }

    @NonNull

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == send){
            view = LayoutInflater.from(context).inflate(R.layout.send_layout, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.received_layout, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, final int position) {
        MessageModel  chat = list.get(position);
        holder.textView.setText(chat.getMessage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;


        public ViewHolder(View itemView) {
            super(itemView);
//            if(state == true){
//                textView = itemView.findViewById(R.id.sendView);
//            }
//            else {
//                textView = itemView.findViewById(R.id.receivedView);
//            }
            textView = itemView.findViewById(R.id.chatUserName);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(fuser.getUid())){
            return send;
        }
        else {
            return received;
        }
//        if(list.get(position).getFrom().equals(userName)){
//            state = true;
//            return send;
//        }
//        else {
//            state = false;
//            return received;
//        }
    }
}
