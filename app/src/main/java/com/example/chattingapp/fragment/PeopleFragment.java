package com.example.chattingapp.fragment;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.chattingapp.MainActivity;
import com.example.chattingapp.R;
import com.example.chattingapp.SignupActivity;
import com.example.chattingapp.chat.MessageActivity;
import com.example.chattingapp.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

// 호출이 안되고 있으므로 MainActivity 에서 호출 해야 한다 말 그대로 frag 이기 때문에 가져와야 한다
public class PeopleFragment extends Fragment {
    final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.frag_people,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peopleFrag_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));  //레이아웃 매니저 설정
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());   //어뎁터 설정
        //FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        return view;
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> { //recyclerView 의 adapter
        List<UserModel> userModels;

        public PeopleFragmentRecyclerViewAdapter() { //생성자
            userModels = new ArrayList<>(); //유저목록 생성
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //나의 uid


            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {   //users의 이벤트 리스너 등록
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { //데이터가 변했을때
                    userModels.clear(); //목록 초기화

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //데이터를 가져와서
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if(userModel.uid.equals(myUid))
                        {
                            continue;
                        }
                        else {
                            userModels.add(userModel);  // 유저모델에 추가
                        }
                    }
                    notifyDataSetChanged(); //유저 목록 새로고침
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
            String pathUri = (userModels.get(position).ProfileImageUri);
            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).ProfileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);






           // 이미지 텍스트 상위 부분에서 메세지를 단다
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",userModels.get(position).uid);

                    ActivityOptions activityOptions=null;
                    activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromright,R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());

                }
            });

        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends  RecyclerView.ViewHolder{
            public TextView textView;
            public ImageView imageView;

            public CustomViewHolder(View view){
                super(view);
                imageView =(ImageView) view.findViewById(R.id.image_Friend);
                textView =(TextView) view.findViewById(R.id.textView_Friend);
            }
        }

    }
}
