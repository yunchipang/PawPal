package edu.northeastern.pawpal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpCookie;
import java.util.List;

import edu.northeastern.pawpal.adapter.postAdapter;
import edu.northeastern.pawpal.model.singlePost;

public class postViewHolder extends RecyclerView.ViewHolder {
    public TextView descriptionTextView;
    public TextView nameTextView;
    public ImageView postImageView;
    public ImageView profileImageView;
    public TextView likesTextView;
    public CheckBox likeCheckBox;
    public String postId;
    private int likeCount;
    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts");
    private postAdapter.OnPressed onPressed;


//    public void setOnPressed(postAdapter.OnPressed onPressed) {
//        this.onPressed = onPressed;
//    }

    public postViewHolder(@NonNull View itemView, postAdapter.OnPressed onPressed) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameTv);
        descriptionTextView = itemView.findViewById(R.id.descTv);
        postImageView = itemView.findViewById(R.id.imageView);
        profileImageView = itemView.findViewById(R.id.profileImage);
        likesTextView = itemView.findViewById(R.id.likeCount);
        likeCheckBox = itemView.findViewById(R.id.likeBtn);
        this.onPressed = onPressed;
    }

    public void bind(singlePost post) {
        nameTextView.setText(post.getName());
        descriptionTextView.setText(post.getDescription());
        String imageUrl = post.getPostImageUrl();
        Glide.with(postImageView)
                .load(imageUrl)
                .into(postImageView);

        String profileUrl = post.getProfileImageUrl();
        Glide.with(profileImageView)
                .load(profileUrl)
                .transform(new CircleCrop())
                .into(profileImageView);
//        int position;
//        String id;
//        String uid;
//        List<String> likes;
//        likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> onPressed.onLiked(position, id, uid, likes, isChecked));
//        postRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                postId = dataSnapshot.getKey();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });

//        likesTextView.setText(Integer.toString(post.getLikeCount()));
////        likeCheckBox.setChecked(false);
//        likeCheckBox.setButtonDrawable(R.drawable.heart_check);
//        likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            @Override
////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                int likeCount = post.getLikeCount();
//////                int likeCountInt = Integer.valueOf(likeCount);
////                if (isChecked) {
////                    likeCount++;
////                } else if (!isChecked){
////                    likeCount--;
////                }
//                int likeCount = post.getLikeCount();
////                int likeCountInt = Integer.valueOf(likeCount);
//                if (isChecked) {
//                    likeCount++;
//                } else {
//                    likeCount--;
//                }
//                updateLikeCount();
//                post.setLikeCount(likeCount);
//
//        });
//        likeCheckBox.setChecked(false);
//    }
//    private void updateLikeCount() {
//        postRef.child(postId).child("likeCount").setValue(likeCount);
//    }
//
    }

//    public void clickListener(final int position, final String id, String name, final String uid, final List<String> likes, final String imageUrl) {
//
////
////        // same as comment
////        imageView.setOnClickListener(v -> {
////
////            Intent intent = new Intent(context, ReplacerActivity.class);
////            intent.putExtra("id", id);
////            intent.putExtra("uid", uid);
////            intent.putExtra("isComment", true);
////            intent.putExtra("imageUrl", imageUrl);
////
////            context.startActivity(intent);
////        });
//
//        likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> onPressed.onLiked(position, id, uid, likes, isChecked));
//    }
    public void clickListener(final int position, final String postId, String name, final String uid, final List<String> likes, final String imageUrl) {
        likeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("this is 142 line" + likes);
                onPressed.onLiked(position, postId, uid, likes, isChecked);

            }
        });
    }


}
