package com.example.chattingapp.fragment;

import android.os.Bundle;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.chattingapp.R;
import com.example.chattingapp.SignupActivity;
import com.example.chattingapp.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// 호출이 안되고 있으므로 MainActivity 에서 호출 해야 한다 말 그대로 frag 이기 때문에 가져와야 한다
public class PeopleFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.frag_people,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peopleFrag_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragRecyclerViewAdapter());
        return view;
    }

    class PeopleFragRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        @NonNull @NotNull
        List<UserModel> userModels;


        public PeopleFragRecyclerViewAdapter(){
            userModels = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                // 서버에서 데이터 넘어욤
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    userModels.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        userModels.add(snapshot.getValue(UserModel.class));
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                 //  Toast.makeText(,"ErrorOccured",Toast.LENGTH_LONG).show();
                    return;
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
            Glide.with(holder.itemView.getContext())
                    .load(userModels.get(position).ProfileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);
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
