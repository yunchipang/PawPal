package edu.northeastern.pawpal;

import static android.content.ContentValues.TAG;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import com.google.android.gms.fido.fido2.api.common.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

//code learn from: blog.csdn.net/m0_46181409/article/details/120468737
public class ProfileActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 3;
    private ImageView mProfileImage;
    private AlertDialog dlg;
    private TextView tv_user;
    private ImageView btn_user;

    private AlertDialog dlg_phone;
    private TextView tv_phone;
    private ImageView btn_phone;

    private AlertDialog dlg_lable;
    private TextView tv_lable;
    private ImageView btn_lable;

    private AlertDialog dlg_per;
    private TextView tv_per;
    private ImageView btn_per;

    private AlertDialog dlg_sex;
    private TextView tv_sex;
    private ImageView btn_sex;
    private int flag=0;
    private int choice=0;

    private TextView tv_birth;
    private ImageView btn_birth;
    private DatePickerDialog dlg_birth;
//    FirebaseAuth mAuth;

    private String input_username;
    private String input_phone;
    private String input_sex;
    private String input_birth;
    private String input_breed;
    private String input_status;
    private Bitmap mCirclePhoto;
    //make a map to store profile data in realtime database

    FirebaseDatabase database = FirebaseDatabase.getInstance();
FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    //get sign in user's uid
    String uid = mAuth.getCurrentUser().getUid();
    DatabaseReference usersRef = database.getReference().child("users");
    // 创建一个HashMap来存储用户信息--move to signup le
    HashMap<String, String> userInfo = new HashMap<>();

//change tou xiang
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);tv_user=findViewById(R.id.tv_user);
        btn_user=findViewById(R.id.btn_user);
        tv_phone=findViewById(R.id.tv_phone);
        btn_phone=findViewById(R.id.btn_phone);
        tv_lable=findViewById(R.id.tv_lable);
        btn_lable=findViewById(R.id.btn_lable);
        tv_per=findViewById(R.id.tv_per);
        btn_per=findViewById(R.id.btn_per);
        tv_sex=findViewById(R.id.tv_sex);
        btn_sex=findViewById(R.id.btn_sex);
        tv_birth=findViewById(R.id.tv_birth);
        btn_birth=findViewById(R.id.btn_birth);
        Listener listener=new Listener();
        btn_user.setOnClickListener(listener);
        btn_phone.setOnClickListener(listener);
        btn_lable.setOnClickListener(listener);
        btn_per.setOnClickListener(listener);
        btn_sex.setOnClickListener(listener);
        btn_birth.setOnClickListener(listener);

        mProfileImage = findViewById(R.id.iv_avatar);

        String status = usersRef.child(uid).child("profile_URL").toString();
        if(status == ""){
        initiate();
        }
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
// 添加 ValueEventListener 监听器
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 从数据快照中获取对应节点的值
                String username = dataSnapshot.child(uid).child("Username").getValue(String.class);
                String phone = dataSnapshot.child(uid).child("Phone").getValue(String.class);
                String gender = dataSnapshot.child(uid).child("Gender").getValue(String.class);
                String birthday = dataSnapshot.child(uid).child("Birthday").getValue(String.class);
                String breed = dataSnapshot.child(uid).child("Breed").getValue(String.class);
                String status = dataSnapshot.child(uid).child("Status").getValue(String.class);
                loadProfileImageFromFirebaseDatabase();
                // 在 TextView 中显示对应节点的值
                tv_user.setText(username);
                tv_phone.setText(phone);
                tv_sex.setText(gender);
                tv_birth.setText(birthday);
                tv_lable.setText(breed);
                tv_per.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 处理监听器取消的情况
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
// 使用setValue()方法将用户信息写入实时数据库中
//        usersRef.child(uid).setValue(userInfo);

    }

    //default profile image
    private void initiate() {

//        mDatabase = FirebaseDatabase.getInstance();
//        imageView = findViewById(R.id.imageView4);
        StorageReference imageRef = storage.getReference().child("dog_app_profile_round.jpg");
        imageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        // 显示图像
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .into(mProfileImage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 处理获取图像 URL 失败的情况
                    }
                });

    }
    //use intent to help open the camera or gallery
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");

        String[] options = {"Camera", "Gallery"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        }
                        break;
                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    //use camera or gallery to upload images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                mCirclePhoto = getRoundedBitmap(photo);
                mProfileImage.setImageBitmap(mCirclePhoto);
                uploadImageToFirebaseStorage(getBytesFromBitmap(mCirclePhoto));
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    mCirclePhoto = getRoundedBitmap(bitmap);
                    mProfileImage.setImageBitmap(mCirclePhoto);
                    uploadImageToFirebaseStorage(getBytesFromBitmap(mCirclePhoto));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        int a=Math.min(bitmap.getWidth(), bitmap.getHeight());
        int diameter = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        output.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.TRANSPARENT);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawARGB(0,245, 245, 220);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // rotate
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        canvas.drawBitmap(bitmap, null, rect, paint);

        return output;
    }





    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    private void uploadImageToFirebaseStorage(byte[] data) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("profile_images/" + UUID.randomUUID().toString() + ".jpg");

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        saveImageToFirebaseDatabase(getRoundedBitmap(mCirclePhoto), imageUrl); // Pass circlePhoto as the first argument
                    }
                });
            }
        });
    }


    private void saveImageToFirebaseDatabase(Bitmap circlePhoto, String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile_URL");


        myRef.setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mCirclePhoto = circlePhoto;
                // Set the circle photo to the ImageView
                mProfileImage.setImageBitmap(getRoundedBitmap(mCirclePhoto));
                Toast.makeText(ProfileActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }






    //download image
    private void loadProfileImageFromFirebaseDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile_URL");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileImageUrl = dataSnapshot.getValue(String.class);
                if (profileImageUrl != null) {
                    Glide.with(ProfileActivity.this)
                            .load(profileImageUrl)
                            .circleCrop()
                            .into(mProfileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Drawable getCircleBackground() {
        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(Color.TRANSPARENT);
        circleDrawable.setStroke(5, Color.WHITE);
        return circleDrawable;
    }



    class Listener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
//            signOutbtn.setOnClickListener(v -> {
//                auth.signOut();
//                startActivity(new Intent(getActivity(), ReplacerActivity.class));
//            });
            switch(view.getId()){
                case R.id.btn_user:
                    View dlgview = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_layout,null);
                    EditText etname=dlgview.findViewById(R.id.etname);
                    Button btnok=dlgview.findViewById(R.id.btnok);
                    btnok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            input_username = etname.getText().toString();
                            tv_user.setText(etname.getText().toString());
//                            userInfo.put("Username", input_username);
                            usersRef.child(uid).child("Username").setValue(input_username);
                            dlg.dismiss();
//                            handleInput(input);
                        }
                    });
                    dlg= new AlertDialog.Builder(ProfileActivity.this)
                            .setView(dlgview)
                            .create();
                    dlg.show();
                    break;
                case R.id.btn_phone:
                    View dlgview_phone = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_layout,null);
                    EditText etname_phone=dlgview_phone.findViewById(R.id.etname);
                    Button btnok_phone=dlgview_phone.findViewById(R.id.btnok);
                    btnok_phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            input_phone = etname_phone.getText().toString();
                            tv_phone.setText(etname_phone.getText().toString());
//                            userInfo.put("Phone", input_phone);
                            usersRef.child(uid).child("Phone").setValue(input_phone);
                            dlg_phone.dismiss();
                        }
                    });
                    dlg_phone= new AlertDialog.Builder(ProfileActivity.this)
                            .setView(dlgview_phone)
                            .create();
                    dlg_phone.show();
                    break;
                case R.id.btn_lable://breed
                    View dlgview_lable = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_layout,null);
                    EditText etname_lable=dlgview_lable.findViewById(R.id.etname);
                    Button btnok_lable=dlgview_lable.findViewById(R.id.btnok);
                    btnok_lable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            input_breed = etname_lable.getText().toString();
                            tv_lable.setText(etname_lable.getText().toString());
//                            userInfo.put("Breed", input_breed);
                            usersRef.child(uid).child("Breed").setValue(input_breed);
                            dlg_lable.dismiss();
                        }
                    });
                    dlg_lable= new AlertDialog.Builder(ProfileActivity.this)
                            .setView(dlgview_lable)
                            .create();
                    dlg_lable.show();
                    break;
                case R.id.btn_per:
                    String status[]={"puppy","adult"};
                    //flag=0;
                    choice=0;
                    dlg_per=new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("select the status")
//                        .setMessage("this is a dialog")
                            //数组选项0：默认项的数组下标
                            .setSingleChoiceItems(status, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i("test",i+"");
                                    flag=1;
                                    choice=i;
                                }
                            })
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(flag==0){
                                        input_status = status[0];
                                        tv_sex.setText(status[0]);
//                                        userInfo.put("Status", input_status);
                                        usersRef.child(uid).child("Status").setValue(input_status);
                                    }else{
                                        input_status = status[choice];
                                        tv_sex.setText(status[choice]);
                                        //userInfo.put("Status", input_status);
                                        usersRef.child(uid).child("Status").setValue(input_status);
                                    }
                                }
                            })
                            .create();
                    dlg_per.show();
                    break;


                case R.id.btn_sex:
                    String sex[]={"girl","boy"};
                    //flag=0;
                    choice=0;
                    dlg_sex=new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("select the gender")
//                        .setMessage("this is a dialog")
                            //数组选项0：默认项的数组下标
                            .setSingleChoiceItems(sex, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i("test",i+"");
                                    flag=1;
                                    choice=i;
                                }
                            })
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(flag==0){
                                        input_sex = sex[0];
                                        tv_sex.setText(sex[0]);
//                                        userInfo.put("Status", input_status);
                                        usersRef.child(uid).child("Gender").setValue(input_sex);
                                    }else{
                                        input_sex = sex[choice];
                                        tv_sex.setText(sex[choice]);
//                                        userInfo.put("Status", input_status);
                                        usersRef.child(uid).child("Gender").setValue(input_sex);
                                    }
                                }
                            })
                            .create();
                    dlg_sex.show();
                    break;

                case R.id.btn_birth:
                    Calendar cal=Calendar.getInstance();
                    int year=cal.get(Calendar.YEAR);
                    int month=cal.get(Calendar.MONTH);
                    int day=cal.get(Calendar.DAY_OF_MONTH);
                    dlg_birth=new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            tv_birth.setText(i+"-"+(i1+1)+"-"+i2);
                            input_birth = i+"-"+(i1+1)+"-"+i2;
//                                        userInfo.put("Status", input_status);
                            usersRef.child(uid).child("Birthday").setValue(input_birth);
                        }
                    },year,month,day);
                    dlg_birth.show();
                    break;
            }

        }
//        public void uploadDataToDataBase(){
//            if(input_birth!= null){
//                userInfo.put("username", "user1");
//            }
//        }
    }
}