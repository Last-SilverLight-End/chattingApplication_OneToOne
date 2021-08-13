package com.example.chattingapp.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattingapp.R;
import com.example.chattingapp.model.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;
    private  RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //채팅을 하는 아이디

        destinationUid = getIntent().getStringExtra("destinationUid");  //  채팅을 당하는 아이디
        button = (Button) findViewById(R.id.messageActivity2_button);
        editText = (EditText) findViewById(R.id.messageActivity2_editText);

        recyclerView = (RecyclerView)findViewById(R.id.messageActivity2_recyclerview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();

                chatModel.Users.put(uid, true);
                chatModel.Users.put(destinationUid, true);

                if(chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            checkChatRoom();
                        }
                    });
                    checkChatRoom();


                }else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
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
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               for(DataSnapshot item : snapshot.getChildren()){
                   ChatModel chatModel = item.getValue(ChatModel.class);
                   if(chatModel.Users.containsKey(destinationUid)){
                       chatRoomUid = item.getKey();
                       button.setEnabled(true);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }


    });
}

//    void checkChatRoom() {
//
//        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue() == null){
//                    ChatModel newRoom = new ChatModel();
//                    newRoom.Users.put(uid, true);
//                    newRoom.Users.put(destinationUid, true);
//                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            checkChatRoom();
//                        }
//                    });
//                    return;
//                }
//
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    ChatModel chatModel = item.getValue(ChatModel.class);
//                    if (chatModel.Users.containsKey(destinationUid) && chatModel.Users.size() == 2) {
//
//                        chatRoomUid = item.getKey();
//                        button.setEnabled(true);
//                        //recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
//                      //  recyclerView.setAdapter(new RecyclerViewAdapter());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//    class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
//
//        List<ChatModel.Comment> comments;
//        public RecyclerViewAdapter(){
//                comments= new ArrayList<>();
//
//                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                            comments.clear();
//                            for(DataSnapshot item : snapshot.getChildren())
//                            {
//                                comments.add(item.getValue(ChatModel.Comment.class));
//                            }
//                            notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
//        }
//
//        @NonNull
//        @NotNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
//            return new MessageViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
//
//            ((MessageViewHolder)holder).textView_message.setText(comments.get(position).message);
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return comments.size();
//        }
//        private class MessageViewHolder extends RecyclerView.ViewHolder{
//            public TextView textView_message;
//            public MessageViewHolder(View view){
//                super(view);
//                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
//            }
//        }
//    }
}




