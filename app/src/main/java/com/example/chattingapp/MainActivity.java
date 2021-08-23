package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chattingapp.fragment.AccountFragment;
import com.example.chattingapp.fragment.ChatFragment;
import com.example.chattingapp.fragment.PeopleFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.example.chattingapp.R.id.Item_Chatting;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.mainactivity_bottomnavigationview);



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.Item_People) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_FrameLayout, new PeopleFragment()).commit();
                    return true;
                } else if (itemId == R.id.Item_Chatting) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_FrameLayout, new ChatFragment()).commit();
                    return true;
                }else if(itemId == R.id.Item_Account){
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_FrameLayout, new AccountFragment()).commit();
                    return true;
                }

                return false;
            }
        });
        passPushTokenToServer();



    }
    void passPushTokenToServer() { //푸시 알림을 위해 토큰을 서버로 전송
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //현재 계정의 uid
        String token = FirebaseMessaging.getInstance().getToken().toString();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(map);
    }
}