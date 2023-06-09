package edu.northeastern.pawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    Button btn2_signup;
    EditText user_email, pass_word, user_name;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();;
    private FirebaseDatabase pawpalFB = FirebaseDatabase.getInstance();
    private DatabaseReference dbParent = pawpalFB.getReference();
    private DatabaseReference dbChild = dbParent.child("users");
    private SharedPreferences sharedPrefs;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = pawpalFB.getReference("users");
//    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    //get sign in user's uid

    // 创建一个HashMap来存储用户信息
    HashMap<String, String> userInfo = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user_email=findViewById(R.id.sign_username);
        user_name=findViewById(R.id.sign_userid);
        pass_word=findViewById(R.id.sign_password);
        btn2_signup=findViewById(R.id.sign);
//        mAuth=FirebaseAuth.getInstance();
        btn2_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user_email.getText().toString().trim();
                String password= pass_word.getText().toString().trim();
                String name = user_name.getText().toString().trim();
                if(email.isEmpty())
                {
                    user_email.setError("Email is empty");
                    user_email.requestFocus();
                    return;
                }
                if(name.isEmpty())
                {
                    user_name.setError("Username is empty");
                    user_name.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    user_email.setError("Enter the valid email address");
                    user_email.requestFocus();
                    return;
                }
                if(password.isEmpty())
                {
                    pass_word.setError("Enter the password");
                    pass_word.requestFocus();
                    return;
                }
                if(password.length()<6)
                {
                    pass_word.setError("Length of the password should be more than 6");
                    pass_word.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String uid = mAuth.getCurrentUser().getUid();
//                            createNewUser(pawpalFB, name);
                            userInfo.put("Username", name);
                            userInfo.put("Gender", "");
                            userInfo.put("Birthday", "");
                            userInfo.put("Phone", "");
                            userInfo.put("Breed", "");
                            userInfo.put("Status", "");
                            userInfo.put("profile_URL", "https://firebasestorage.googleapis.com/v0/b/pawpal-383322.appspot.com/o/profile_images%2Fc10dacd6-cdba-491d-98de-9f3c5701293e.jpg?alt=media&token=5bdf19cf-9bfb-41f6-971b-71525331ad69");
                            usersRef.child(uid).setValue(userInfo);
//                            sharedPrefs.edit().putString("username", String.valueOf(user_name)).apply();
                            Toast.makeText(SignUpActivity.this,"You are successfully Registered, Please Sign in", Toast.LENGTH_LONG).show();
//                            sharedPrefs.edit().putString("username", String.valueOf(user_name)).apply();
 //                           Toast.makeText(SignUpActivity.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent(SignUpActivity.this,
                                    LoginActivity.class);
                            intent.putExtra("username", String.valueOf(user_name));
                            startActivity(intent);

                            Intent intent2 = new Intent(SignUpActivity.this, ChatListActivity.class);
                            intent2.putExtra("username", String.valueOf(user_name));


                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this,"You are not Registered! Try again", Toast.LENGTH_SHORT).show();
                        }
                    }

//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful())
//                        {
//                            Toast.makeText(SignUpActivity.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(SignUpActivity.this,"You are not Registered! Try again", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                });

            }
        });

    }
    private void createNewUser(FirebaseDatabase pawpalFB, String username) {
//        userInfo.put("Username", username);
//        userInfo.put("Gender", null);
//        userInfo.put("Birthday", null);
//        userInfo.put("Phone", null);
//        userInfo.put("Breed", null);
//        userInfo.put("Status", null);
//        usersRef.child(uid).setValue(userInfo);
//        DatabaseReference newUserRoot = pawpalFB.getReference();
//        DatabaseReference newUserLeaf = newUserRoot.child("users").push();
//        newUserLeaf.setValue(username);
    }

}