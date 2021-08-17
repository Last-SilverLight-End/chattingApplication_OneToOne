package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chattingapp.fragment.ChatFragment;
import com.example.chattingapp.fragment.PeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

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
                } else if (itemId == Item_Chatting) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_FrameLayout, new ChatFragment()).commit();
                    return true;
                }

                return false;
            }
        });




    }
}