package edu.northeastern.pawpal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//part of code learn from: https://stackoverflow.com/questions/44968837/why-i-can-not-read-and-display-data-from-firebase-real-time-database

public class ChatActivity extends AppCompatActivity {

    String userName,otherName;
    String senderRoom, getRoom;
    TextView chatUserName;
    String getMessage;
    ImageView sendButton;
    EditText chatEditText;
    DataSnapshot dataSnapshot;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, mDatabase;
    RecyclerView chatRecyclerView;

    MessageAdapter messageAdapter;
    ArrayList<MessageModel> list;

    //TODO: new add
    FirebaseAuth fAuth;
    FirebaseUser fuser;
    String userID, uid;

    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        intent=getIntent();


        userName = getIntent().getExtras().getString("username");
        otherName= getIntent().getStringExtra("othername");





        //Log.d("ChatActivity", "userName: " + userName + ", otherName: " + otherName);

        chatUserName = (TextView) findViewById(R.id.chatUserName);
        sendButton = (ImageView) findViewById(R.id.sendImage);
        chatEditText = (EditText) findViewById(R.id.chatEditText);
        chatUserName.setText(otherName);



        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 1);
        chatRecyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        messageAdapter = new MessageAdapter(ChatActivity.this, list);
        chatRecyclerView.setAdapter(messageAdapter);





        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().
                getReference().child("users");


        if (fuser != null)  {
            reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        }

        //reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
//        if (reference == null) {
//            reference = FirebaseDatabase.getInstance().getReference("users");
//        }

        Query query = userRef.orderByChild("Username").equalTo(otherName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.exists()) {
                    // 如果找到了与用户名相匹配的UID，将其赋值给uid变量
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        uid = snapshot1.getKey();
                    }
                    senderRoom = userID + uid;
                    getRoom = uid + userID;
                    DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
                    // Attach listener to read messages
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren())
                            {
                                MessageModel messages=snapshot1.getValue(MessageModel.class);
                                list.add(messages);
                            }
                            messageAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else{
                    // 如果没有找到与用户名相匹配的UID，可能需要处理一些错误情况
                    Log.d("TAG100: ", "onDataChange: snapshot is null or does not exist");
                }
                Log.d("TAG100: ", "uid: " + uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG100: ", "onCancelled: " + error.getMessage());
            }
        });
//
//        senderRoom = userID + uid;
//        Log.d("TAG:r", "onCreate: " + senderRoom.toString());
//        getRoom = uid + userID;



//        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for(DataSnapshot snapshot1:snapshot.getChildren())
//                {
//                    MessageModel messages=snapshot1.getValue(MessageModel.class);
//                    list.add(messages);
//                    Log.d("TAG1919:", "onDataChange: " + list.toString());
//                }
//                messageAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });







        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessage = chatEditText.getText().toString();
                Log.d("TAG1212", "onClick: " + getMessage);
                chatEditText.setText(""); //TODO:
                if (getMessage.equals("")) {

                    Toast.makeText(getApplicationContext(), "Enter message first", Toast.LENGTH_SHORT).show();
                } else {
                    MessageModel chat = new MessageModel(getMessage, fuser.getUid());
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push().setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("TAG1111", "onComplete: " + chat.toString());
                                    firebaseDatabase.getReference()
                                            .child("chats")
                                            .child(getRoom)
                                            .child("messages")
                                            .push()
                                            .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });

                    //chatEditText.setText(null);


                    // 如果uid不为null，将消息发送给uid
//                    if (uid != null) {
//                        sendMessage(fuser.getUid(),uid,message);
//                    } else {
//                        // 如果uid为null，则可能需要处理错误情况
//                    }
                }




            }
        });
    }


    @Override
    public void onStart () {
        super.onStart();
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop () {
        super.onStop();
        if (messageAdapter != null) {
            messageAdapter.notifyDataSetChanged();
        }
    }
}