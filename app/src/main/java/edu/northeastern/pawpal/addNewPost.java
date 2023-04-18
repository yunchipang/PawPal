package edu.northeastern.pawpal;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class addNewPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CAMERA_PERMISSION_CODE = 3;
    private Uri mImageUri;
    Bitmap photo;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    private ImageView mImageView;
    private StorageReference mStorageRef;

// TODO: features - comment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        Button choosePhotoButton = findViewById(R.id.button2);
        mImageView = findViewById(R.id.imageView3);
        mStorageRef = FirebaseStorage.getInstance().getReference();

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
                            case 0:
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

//        if (intent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "/storage/emulated/O/Pictures/",
//                        photoFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        }

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
                                    // Do something with the download URL
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
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("users").child(uid).child("imagePath").setValue(fileName);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveUrlToFirestore(Uri downloadUri) {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", downloadUri.toString());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(addNewPost.this, "Image added to Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addNewPost.this, "Error adding image to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}