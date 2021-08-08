package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chattingapp.fragment.PeopleFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_FrameLayout,new PeopleFragment()).commit();

    }
}