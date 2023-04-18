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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String userName,otherName;
    TextView chatUserName;
    ImageView backImage, sendImage;
    EditText chatEditText;
    DataSnapshot dataSnapshot;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    RecyclerView chatRecyclerView;

    MessageAdapter messageAdapter;
    List<MessageModel> list;

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
//        userName = getIntent().getExtras().getString("username");
//        otherName = getIntent().getExtras().getString("othername");

        //Log.d("ChatActivity", "userName: " + userName + ", otherName: " + otherName);

        chatUserName = (TextView)findViewById(R.id.chatUserName);
        backImage = (ImageView)findViewById(R.id.backImage);
        sendImage = (ImageView)findViewById(R.id.sendImage);
        chatEditText = (EditText)findViewById(R.id.chatEditText);
        //chatUserName.setText(otherName);

        chatRecyclerView = (RecyclerView)findViewById(R.id.chatRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 1);
        chatRecyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(ChatActivity.this, list);
        chatRecyclerView.setAdapter(messageAdapter);

        list = new ArrayList<>();

        //TODO: add new
        Intent intent = getIntent();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String userid=intent.getStringExtra("uid");
        String username= intent.getStringExtra("userName");

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

//                if (snapshot.getValue() == null) {
//                    return;
//                }
                dataSnapshot = snapshot;
                User user = snapshot.getValue(User.class);
                HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                if (user!= null && data != null) {
                    //String username = (String) data.get("username");
                    chatUserName.setText(user.toString());
                    readMessages(fuser.getUid(), userid);

                }
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
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.putExtra("UserName", userName);
                startActivity(intent);
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatEditText.getText().toString();
                chatEditText.setText("");
                sendMessage(fuser.getUid(), userid, message);
            }
        });
           // loadMessage();



    }



    //TODO: new add
    private void sendMessage(String sender, final String receiver, String message) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Chats").push().setValue(hashMap);

    }
    private void readMessages(String myid,String userid){
        list =new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    MessageModel chat=snapshot.getValue(MessageModel.class);
                    assert chat != null;
                    if (chat.getReciver().equals(myid) && chat.getSender().equals(userid)||
                            chat.getReciver().equals(userid) && chat.getSender().equals(myid)){
                        list.add(chat);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, list);
                    chatRecyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }







//
//    public void sendMessage(String text){
//        if (userName != null && otherName != null) {
//            final String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
//            final Map messageMap = new HashMap();
//            messageMap.put("text" , text);
//            messageMap.put("from" , userName);
//
//            reference.child("Messages").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        reference.child("Messages").child(otherName).child(userName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    }
//
//    public void loadMessage(){
//        if(userName != null && otherName != null) {
//        reference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
//                MessageModel messageModel = snapshot.getValue(MessageModel.class);
//                list.add(messageModel);
//                messageAdapter.notifyDataSetChanged();
//                chatRecyclerView.scrollToPosition(list.size()-1);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });
//    }
//}
    }
