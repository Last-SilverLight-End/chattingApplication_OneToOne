package com.example.chattingapp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chattingapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;


public class SignupActivity extends AppCompatActivity {

    private EditText Id;
    private EditText Password;
    private EditText PasswordCheck;
    private EditText Name;
    private EditText Age;
    private Button Agree;
    private Button Disagree;
    private ImageView Profile;
    private CheckBox CheckBox1;
    private CheckBox CheckBox2;
    private static final int PICK_FROM_ALBUM = 10;
    private Uri imageUri;
    private String imageUri_string="";

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM ) {
            if( resultCode == RESULT_OK) {
                try {
                    imageUri = data.getData(); //이미지 경로 원본
                    imageUri_string = data.getDataString(); // 이미지 존재여부 확인
                    Profile.setImageURI(data.getData()); // 가운데 뷰를 바꿈
                } catch (Exception e) {
                    Log.d(TAG, "Error about album preview: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Id= (EditText)findViewById(R.id.edit_Id);
        Password = (EditText)findViewById(R.id.edit_Password);
        PasswordCheck = (EditText)findViewById(R.id.edit_PasswordCheck);
        Name = (EditText)findViewById(R.id.edit_Name);
        Age = (EditText)findViewById(R.id.edit_Age);
        Agree = (Button)findViewById(R.id.Agree_btn);
        Disagree = (Button)findViewById(R.id.Disagree_btn);
        Profile = (ImageView)findViewById(R.id.signupActivity_imageview_profile);
        CheckBox1 = (CheckBox)findViewById(R.id.cb_btn1);
        CheckBox2 = (CheckBox)findViewById(R.id.cb_btn2);

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // startActivityForResult(intent,PICK_FROM_ALBUM);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });

        Disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Agree.setOnClickListener(new View.OnClickListener(){



            @Override
            public void onClick(View view){

                String CheckBox1_string = CheckBox1.getText().toString();
                String CheckBox2_string = CheckBox2.getText().toString();
                String Password_string = Password.getText().toString();
                String PasswordCheck_string = PasswordCheck.getText().toString();
                String Id_string = Id.getText().toString().trim();
                String Name_string = Name.getText().toString();
                String Age_string = Age.getText().toString();

                String[] arr ={Password_string,Id_string,Name_string,Age_string};
                for(String str : arr){
                    //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                    if(str.equals("")) {
                        Toast.makeText(getApplicationContext(), "빈 내용이 존재합니다", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                try {
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(Id_string, Password_string)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            // 위의 입력하는 정보들 가져오는거 성공하면 파베에서 입력 하고
                                            UserModel userModel = new UserModel();
                                            userModel.userName = Name_string;
                                            userModel.userAge = Age_string;
                                             String uid = task.getResult().getUser().getUid();
                                             FirebaseDatabase.getInstance().getReference().child("Users").child(uid).setValue(userModel);

                                             // 앨범에서 사진 가져오는데 성공 여부 판단 해서 결정하는곳


                                                if(!(imageUri_string.equals(""))) {
                                                    FirebaseStorage.getInstance().getReference().child("UserImage").child(uid).putFile(imageUri)
                                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NotNull Task<UploadTask.TaskSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        //@SuppressWarnings("VisibleForTests")

                                                                        String imgUri = task.getResult().getUploadSessionUri().toString();
                                                                        userModel.ProfileImageUri = imgUri;
                                                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "사진이 존재하지 않습니다", Toast.LENGTH_LONG).show();

                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull @NotNull Exception e) {

                                                        }
                                                    });
                                                }
                                                else{
                                                    Toast.makeText(getApplicationContext(), "이미지가 없습니다", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                        else {

                                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                            switch (errorCode) {

                                                case "ERROR_INVALID_CUSTOM_TOKEN":
                                                    Toast.makeText(getApplicationContext(), "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                                    Toast.makeText(getApplicationContext(), "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_INVALID_CREDENTIAL":
                                                    Toast.makeText(getApplicationContext(), "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_INVALID_EMAIL":
                                                    Toast.makeText(getApplicationContext(), "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                                    Id.setError("The email address is badly formatted.");
                                                    Id.requestFocus();
                                                    break;

                                                case "ERROR_WRONG_PASSWORD":
                                                    Toast.makeText(getApplicationContext(), "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                                                    Password.setError("password is incorrect ");
                                                    Password.requestFocus();
                                                    Password.setText("");
                                                    break;

                                                case "ERROR_USER_MISMATCH":
                                                    Toast.makeText(getApplicationContext(), "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                                    Toast.makeText(getApplicationContext(), "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                                    Toast.makeText(getApplicationContext(), "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                                    Toast.makeText(getApplicationContext(), "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                                                    Id.setError("The email address is already in use by another account.");
                                                    Id.requestFocus();
                                                    break;

                                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                                    Toast.makeText(getApplicationContext(), "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_USER_DISABLED":
                                                    Toast.makeText(getApplicationContext(), "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_USER_TOKEN_EXPIRED":
                                                    Toast.makeText(getApplicationContext(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_USER_NOT_FOUND":
                                                    Toast.makeText(getApplicationContext(), "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_INVALID_USER_TOKEN":
                                                    Toast.makeText(getApplicationContext(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_OPERATION_NOT_ALLOWED":
                                                    Toast.makeText(getApplicationContext(), "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                                    break;

                                                case "ERROR_WEAK_PASSWORD":
                                                    Toast.makeText(getApplicationContext(), "The given password is invalid.", Toast.LENGTH_LONG).show();
                                                    Password.setError("The password is invalid it must 6 characters at least");
                                                    Password.requestFocus();
                                                    break;
                                                default:
                                                    Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    }


                            }).addOnFailureListener(SignupActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                // 어느 부분이 비어 있을때 일으키는 예외처리
                catch(IllegalArgumentException e){
                    Toast.makeText(getApplicationContext(), "비어있는 부분이 존재합니다", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        });
    }



}