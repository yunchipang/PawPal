package edu.northeastern.pawpal.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.pawpal.R;
import edu.northeastern.pawpal.model.singlePost;
import edu.northeastern.pawpal.postViewHolder;

public class postAdapter extends RecyclerView.Adapter<postViewHolder>{
    private List<singlePost> postList;
    private Context mContext;
    public OnPressed onPressed;
    public postAdapter(Context context, List<singlePost> postList) {
        mContext = context.getApplicationContext();
        this.postList = postList;
        notifyDataSetChanged();
    }

    @Override
    public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_post, parent, false);
        return new postViewHolder(itemView, onPressed);
    }

    @Override
    public void onBindViewHolder(@NonNull postViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.bind(postList.get(position));
        List<String> likeList = postList.get(position).getLikeCount();
//
//        int count = likeList.size();
//
//        if (count == 0) {
//            holder.likesTextView.setText("0 Like");
//        } else if (count == 1) {
//            holder.likesTextView.setText(count + " Like");
//        } else {
//            holder.likesTextView.setText(count + " Likes");
//        }

        if (likeList != null) {
            int count = likeList.size();
            if (count == 0) {
                holder.likesTextView.setText(count + "Like");
            } else if (count == 1) {
                holder.likesTextView.setText(count + " Like");
            } else {
                holder.likesTextView.setText(count + " Likes");
            }
        } else {
            holder.likesTextView.setText("0 Like");
        }

        //check if already like
        holder.likeCheckBox.setChecked(likeList.contains(user.getUid()));
        holder.clickListener(position,
                postList.get(position).getPostId(),
                postList.get(position).getName(),
                postList.get(position).getUid(),
                postList.get(position).getLikeCount(),
                postList.get(position).getPostImageUrl()
        );

    }

    public void OnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }

    public interface OnPressed {
        void onLiked(int position, String postId, String uid, List<String> likeList, boolean isChecked);

//        void setCommentCount(TextView textView);

    }
    @Override
    public int getItemCount() {
        return postList.size();
    }
}
