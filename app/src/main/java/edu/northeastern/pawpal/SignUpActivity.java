package edu.northeastern.pawpal;

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

public class SignUpActivity extends AppCompatActivity {
    Button btn2_signup;
    EditText user_email, pass_word, user_name;
    FirebaseAuth mAuth;
    private FirebaseDatabase pawpalFB = FirebaseDatabase.getInstance();
    private DatabaseReference dbParent = pawpalFB.getReference();
    private DatabaseReference dbChild = dbParent.child("users");
    private SharedPreferences sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user_email=findViewById(R.id.sign_username);
        user_name=findViewById(R.id.sign_userid);
        pass_word=findViewById(R.id.sign_password);
        btn2_signup=findViewById(R.id.sign);
        mAuth=FirebaseAuth.getInstance();
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
                            createNewUser(pawpalFB, name);
//                            sharedPrefs.edit().putString("username", String.valueOf(user_name)).apply();
                            Toast.makeText(SignUpActivity.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
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
        DatabaseReference newUserRoot = pawpalFB.getReference();
        DatabaseReference newUserLeaf = newUserRoot.child("users").push();
        newUserLeaf.setValue(username);
    }

}