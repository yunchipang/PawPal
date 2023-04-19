package edu.northeastern.pawpal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

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
    DatabaseReference reference;
    RecyclerView chatRecyclerView;

    MessageAdapter messageAdapter;
    ArrayList<MessageModel> list;

    //TODO: new add
    FirebaseAuth fAuth;
    FirebaseUser fuser;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//TODO: new add
        fAuth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = fAuth.getCurrentUser().getUid();

//        if(userName != null && otherName != null) {
        userName = getIntent().getExtras().getString("username");
        otherName = getIntent().getExtras().getString("othername");


        //Log.d("ChatActivity", "userName: " + userName + ", otherName: " + otherName);

        chatUserName = (TextView)findViewById(R.id.chatUserName);
        backImage = (ImageView)findViewById(R.id.backImage);
        sendButton = (ImageView)findViewById(R.id.sendImage);
        chatEditText = (EditText)findViewById(R.id.chatEditText);
        //chatUserName.setText(messageAdapter.received);
        chatUserName.setText(otherName);

        chatRecyclerView = (RecyclerView)findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 1);
        chatRecyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        //list.add(new MessageModel("one", "2", "3")); //TODO:
        messageAdapter = new MessageAdapter(ChatActivity.this, list);
        chatRecyclerView.setAdapter(messageAdapter);



        //TODO: add new
        Intent intent = getIntent();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = intent.getStringExtra("uid");
        String username= intent.getStringExtra("userName");

        //String u = dataSnapshot.child(userID).child("Username").getValue(String.class);
        //chatUserName.setText("1");

        if (fuser != null)  {
            reference = FirebaseDatabase.getInstance().getReference("users").child("uid");
        }

        //reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
        if (reference == null) {
            reference = FirebaseDatabase.getInstance().getReference("users");
        }
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

                    readMessages(fuser.getUid(), userid);

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
                    sendMessage(fuser.getUid(),userid,message);
                }
            }
        });

    }



    //TODO: new add
    private void sendMessage(String sender, final String receiver, String message) {

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        //String messageId =  chatRef.push().getKey();

        HashMap<String, String> hashMap = new HashMap<>();
        //hashMap.put("chatId", generateChatId(sender, receiver));
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        chatRef.child(userID).setValue(hashMap);


    }

    private void readMessages(String myid,String userid){
        //list.clear();
        reference=FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("TAG:400", "onDataChange" + snapshot.toString());
                    String senderId = snapshot.child("sender").getValue(String.class);
                    String receiverId = snapshot.child("receiver").getValue(String.class);
                    String message = snapshot.child("message").getValue(String.class);
                    //if (senderId.equals(myid) && receiverId.equals(userid) ||
                    //        senderId.equals(userid) && receiverId.equals(myid)) {
                        list.add(new MessageModel(senderId, receiverId, message));


//                    MessageModel chat = snapshot.getValue(MessageModel.class);
//                    Log.d("TAG:400", "onDataChange" + chat.getMessage());
////                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)||
////                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
//
//                        list.add(chat);

//                        messageAdapter = new MessageAdapter(ChatActivity.this, list);
//                        chatRecyclerView.setAdapter(messageAdapter);
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
