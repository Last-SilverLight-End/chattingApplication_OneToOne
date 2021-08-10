package com.example.chattingapp.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chattingapp.R;
import com.example.chattingapp.model.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //채팅을 하는 아이디
        destinationUid= getIntent().getStringExtra("destinationUid");  //  채팅을 당하는 아이디


        button = (Button)findViewById(R.id.messageActivity_button);
        editText = (EditText)findViewById(R.id.messageActivity_editText);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ChatModel chatModel = new ChatModel();
                chatModel.Users.put(uid,true);
                chatModel.Users.put(destinationUid,true);

                if(chatRoomUid == null) {
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                    checkChatRoom();
                }
                else
                {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid= uid;
                    comment.message = editText.getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment);
                }
            }
        });
        checkChatRoom();
    }


    void checkChatRoom() {

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    ChatModel newRoom = new ChatModel();
                    newRoom.Users.put(uid, true);
                    newRoom.Users.put(destinationUid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                    return;
                }

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.Users.containsKey(destinationUid) && chatModel.Users.size() == 2) {
                        chatRoomUid = item.getKey();
                       // button.setEnabled(true);
                       // recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                       // recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}