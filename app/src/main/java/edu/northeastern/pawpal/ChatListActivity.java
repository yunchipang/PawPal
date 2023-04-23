package edu.northeastern.pawpal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {



    FirebaseDatabase firebaseDatabase;


    DatabaseReference reference;
    List<String> list;
    String username, uid;
    RecyclerView userList;
    UserAdapter userAdapter;

    FirebaseAuth mAuth;
    String currentUserID;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("users");

        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("users");
        list = new ArrayList<>();
        userList = (RecyclerView)findViewById(R.id.userList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatListActivity.this,1);
        userList.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(ChatListActivity.this,list,ChatListActivity.this, username);
        userList.setAdapter(userAdapter);
        list();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            uid = mFirebaseUser.getUid();
        }

        Intent intent2 = new Intent(ChatListActivity.this, ChatActivity.class);
        intent2.putExtra("userName", String.valueOf(username));
        Log.d("TAG:200", "onCreate: " );
        //startActivity(intent2);

    }

    public void list (){
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                String key = snapshot.getKey();

                if(!key.equals(username)){
                    DataSnapshot usernameSnapshot = snapshot.child("Username"); //TODO: new
                    String name = snapshot.child("Username").getValue(String.class);
                    list.add(name);
                    userAdapter.notifyDataSetChanged();;
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}