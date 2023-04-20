package edu.northeastern.pawpal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<String> list;
    Activity activity;
    String currentUser;

    private DatabaseReference reference;

    public UserAdapter(Context context, List<String> list, Activity activity, String userName) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.currentUser = userName;
        reference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String username = list.get(position);
        holder.usernameTextView.setText(username);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("username", currentUser);
                intent.putExtra("othername", list.get(position).toString());
                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
       // LinearLayout userID;

        public ViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.userName);
        }
    }
}
