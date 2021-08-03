package com.example.chattingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
;
    private TextInputEditText Id;
    private TextInputEditText Password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button login,signup;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    LinearLayout btn_login;
    LinearLayout btn_signup;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        String splash_background = firebaseRemoteConfig.getString("splash_background");
        getWindow().setStatusBarColor(Color.parseColor(splash_background));
        Id= (TextInputEditText)findViewById(R.id.loginActivity_EditText_Id);
        Password = (TextInputEditText)findViewById(R.id.loginActivity_EditText_Password);
         btn_login= (LinearLayout) findViewById(R.id.LinearLayout_login);
        btn_signup= (LinearLayout) findViewById(R.id.LinearLayout_signup);
//        login.setBackgroundColor(Color.parseColor(splash_background));
//
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEvent();
            }
        });
       btn_signup.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View view){
               startActivity(new Intent(LoginActivity.this,SignupActivity.class));
           }
     });
       //로그인 인터페이스 리스너

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //로그인
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    // 로그아웃
                }
            }
        };
//
//
 //       btn_signup.setOnClickListener(new View.OnClickListener(){
 //           @Override
 //           public void onClick(View v){
//
 //           }
 //       });
    }
    void loginEvent(){
        firebaseAuth.signInWithEmailAndPassword(Id.getText().toString(),Password.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    // 로그인 실패할 경우
                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected  void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop(){
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
