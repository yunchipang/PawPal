package edu.northeastern.pawpal;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.UUID;

import edu.northeastern.pawpal.model.singlePost;

public class addNewPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CAMERA_PERMISSION_CODE = 3;
    private Uri mImageUri;
    Bitmap photo;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid;

    {
        assert currentUser != null;
        uid = currentUser.getUid();
    }

    private ImageView mImageView;
    private EditText mContentEditText;
    private StorageReference mStorageRef;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference postsRef = mDatabase.getReference("posts");
    DatabaseReference currentUserRef = mDatabase.getReference("users").child(uid);
// TODO: features - comment
    HashMap<String, String> postsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        Button choosePhotoButton = findViewById(R.id.button2);
        mImageView = findViewById(R.id.imageView3);
        mContentEditText = findViewById(R.id.textViewContent);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Glide.with(mImageView)
                .load(mImageUri)
                .placeholder(R.drawable.kele_start) // 设置默认图片
                .into(mImageView);

        choosePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        Button postButton = findViewById(R.id.button3);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void pickImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
/**/                            case 0:
                                openGallery();
                                break;
                            case 1:
                                openCamera();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {

        if (ActivityCompat.checkSelfPermission(addNewPost.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(addNewPost.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(photo);
        }
    }
    private void uploadImage() {

        StorageReference fileRef = mStorageRef.child("postImages");
        String fileName = UUID.randomUUID() + "-" + uid + ".jpg";
        StorageReference imageRef = fileRef.child(fileName);
        if (mImageUri != null) {
//            StorageReference imageRef = mStorageRef.child(UUID.randomUUID().toString() + ".jpg");

            StorageTask<UploadTask.TaskSnapshot> mUploadTask = imageRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(addNewPost.this, "Upload successful", Toast.LENGTH_LONG).show();
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Do something with the download URL
                                    String postUrl = uri.toString();
                                    saveImageToFirebaseDatabase(postUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addNewPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageTask<UploadTask.TaskSnapshot> mUploadTask = imageRef.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(addNewPost.this, "Upload successful", Toast.LENGTH_LONG).show();
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                    String imageUrl = userSnapshot.child("imagePath").getValue(String.class);
                                    String postUrl = uri.toString();
                                    saveImageToFirebaseDatabase(postUrl);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addNewPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveImageToFirebaseDatabase(String postUrl) {
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 获取当前用户的用户名
                String username = dataSnapshot.child("Username").getValue(String.class);
//                Log.d("test username", username);
                String comment = mContentEditText.getText().toString();
                String profileUrl = dataSnapshot.child("profile_URL").getValue(String.class);
//                List<String> likeCount = new ArrayList<>(0);
                List<String> likeCount = new ArrayList<>();
                String postId = UUID.randomUUID().toString();
                singlePost singlePost = new singlePost(username, postId, comment, postUrl, profileUrl, likeCount, uid);
                long timestamp = System.currentTimeMillis();
                singlePost.setTimestamp(timestamp);
//        Log.d("test singlePost", singlePost.toString());
                postsRef.child(singlePost.getPostId()).setValue(singlePost);
//                postsRef.push().setValue(singlePost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}