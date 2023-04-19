package edu.northeastern.pawpal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private static final int SIGN_IN_REQUEST_CODE = 1;
    private EditText user_name, pass_word;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name=findViewById(R.id.edit_username);
        pass_word=findViewById(R.id.edit_password);
        Button btn_login = findViewById(R.id.log_in);
        Button btn_sign = findViewById(R.id.SignUpBtn);
        mAuth=FirebaseAuth.getInstance();
        btn_login.setOnClickListener(v -> {
            String email= user_name.getText().toString().trim();
            String password=pass_word.getText().toString().trim();
            if(email.isEmpty())
            {
                user_name.setError("Email is empty");
                user_name.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                user_name.setError("Enter the valid email");
                user_name.requestFocus();
                return;
            }
            if(password.isEmpty())
            {
                pass_word.setError("Password is empty");
                pass_word.requestFocus();
                return;
            }
            if(password.length()<6)
            {
                pass_word.setError("Length of password is more than 6");
                pass_word.requestFocus();
                return;
            }
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    //TODO: ADD USER TO DATABASE
                    FirebaseUser user = mAuth.getCurrentUser();
//                    Intent intent = new Intent(LoginActivity.this,
//                            ProfileActivity.class);
//                    intent.putExtra("username", user_name);
//                    startActivity(intent);
                    startActivity(new Intent(this, MainActivity.class));

                    Intent intent2 = new Intent(LoginActivity.this, ChatActivity.class);
                    intent2.putExtra("username", String.valueOf(user_name));
//                    //startActivity(intent2);

                }
                else
                {
                    Toast.makeText(this,
                            "Please Check Your email and password.",
                            Toast.LENGTH_SHORT).show();
                }

            });
        });
        btn_sign.setOnClickListener(v -> startActivity(new Intent(this,SignUpActivity.class )));
    }
}