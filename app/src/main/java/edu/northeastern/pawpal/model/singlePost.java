package edu.northeastern.pawpal.model;

import android.net.Uri;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class singlePost {

    private  String description;
    private  String name;
    private String imageUrl;
    private String profileImageUrl;
    private String postId;
    private long timestamp;
    private List<String> likeCount;
    private String uid;

//    @ServerTimestamp
//    private Date timestamp;

    public singlePost() {
    }

    public singlePost(String name, String postId, String description, String imageUrl, String profileImageUrl, List<String> likeCount, String uid) {
        this.postId = postId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.profileImageUrl = profileImageUrl;
        this.likeCount = likeCount;
        this.uid = uid;
    }

    public  String getPostId() {
        return postId;
    }
    public void setPostId(String uid) {
        this.postId = postId;
    }
    public  String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public  String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImageUrl() {
        return imageUrl;
    }

    public void setPostImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("name: %s\ndescription: %s\nimageUrl: %s\nprofileImageUrl: %s\n", name, description, imageUrl, profileImageUrl);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public List<String> getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(List<String> likeCount) {
        this.likeCount = likeCount;
    }
}
