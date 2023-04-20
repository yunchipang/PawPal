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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    String userName,otherName;
    TextView chatUserName;
    ImageView backImage, sendButton;
    EditText chatEditText;
    EditText textsend;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//TODO: new add
        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = fAuth.getCurrentUser().getUid();



//        if(userName != null && otherName != null) {
        userName = getIntent().getExtras().getString("username");
        otherName = getIntent().getExtras().getString("othername");
//        otherName = getIntent().getStringExtra("otherName");



        //Log.d("ChatActivity", "userName: " + userName + ", otherName: " + otherName);

        chatUserName = (TextView) findViewById(R.id.chatUserName);
        backImage = (ImageView) findViewById(R.id.backImage);
        sendButton = (ImageView) findViewById(R.id.sendImage);
        chatEditText = (EditText) findViewById(R.id.chatEditText);
        //chatUserName.setText(messageAdapter.received);
        chatUserName.setText(otherName);

        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 1);
        chatRecyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        //list.add(new MessageModel("one", "2", "3")); //TODO:
        messageAdapter = new MessageAdapter(ChatActivity.this, list);
        chatRecyclerView.setAdapter(messageAdapter);


        //TODO: add new
//        Intent intent = getIntent();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //String userid = intent.getStringExtra("uid");
        //String username = intent.getStringExtra("userName");
        DatabaseReference userRef = FirebaseDatabase.getInstance().
                getReference().child("users");


        //String u = dataSnapshot.child(userID).child("Username").getValue(String.class);
        //chatUserName.setText("1");

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
                        Log.d("TAG100: ", "onDataChange: " + snapshot1.toString());
                    }
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

        reference.addValueEventListener(new ValueEventListener() {
            //@SuppressLint("RestrictedApi")
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataSnapshot = snapshot;
//TODO:
                //User user = snapshot.getValue(User.class);
                //HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                //if (data != null) {
                Log.d("TAG:320", snapshot.toString());

                readMessages(fuser.getUid(), uid);

                //}
                //chatUserName.setText(user != null ? user.toString() : null);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //firebaseDatabase = FirebaseDatabase.getInstance();
        // reference = firebaseDatabase.getReference(senderRoom);

        //reference = firebaseDatabase.getReference("Messages/" + userName + "/" + otherName);


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ChatListActivity.class);
                intent.putExtra("UserName", userName);
                startActivity(intent);
            }
        });




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatEditText.getText().toString();
                chatEditText.setText(""); //TODO:
                if (!message.equals("")){
                    // 如果uid不为null，将消息发送给uid
                    if (uid != null) {
                        sendMessage(fuser.getUid(),uid,message);
                    } else {
                        // 如果uid为null，则可能需要处理错误情况
                    }
                }
            }
        });

    }




    //TODO: new add
    private void sendMessage(String sender, final String receiver, String message) {

        if (receiver == null || receiver.isEmpty()) {
            Log.d("TAG100: ", "receiver is null or empty");
            return;
        }
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        String messageId =  chatRef.push().getKey();

        HashMap<String, String> hashMap = new HashMap<>();
        //hashMap.put("chatId", generateChatId(sender, receiver));
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        chatRef.child(messageId).setValue(hashMap);
    }

    private void readMessages(String myid,String userid){
        //list.clear();
        reference= (DatabaseReference) FirebaseDatabase.getInstance().getReference("Chats");

//        reference.addValueEventListener(new ValueEventListener() {
        Query query = reference.orderByChild("receiver").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("TAG:400", "onDataChange" + snapshot);
                    String senderId = snapshot.child("sender").getValue(String.class);
                    String receiverId = snapshot.child("receiver").getValue(String.class);
                    String message = snapshot.child("message").getValue(String.class);
                    //if (senderId.equals(myid) && receiverId.equals(userid) ||
                    //        senderId.equals(userid) && receiverId.equals(myid)) {
                    list.add(new MessageModel(senderId, receiverId, message));

                }
                messageAdapter.notifyDataSetChanged();
            }
            // }

//            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}