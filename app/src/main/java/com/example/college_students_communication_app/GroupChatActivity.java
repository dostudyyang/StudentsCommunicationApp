package com.example.college_students_communication_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.college_students_communication_app.Adapters.GroupMessageAdapter;
import com.example.college_students_communication_app.databinding.ActivityGroupChatBinding;
import com.example.college_students_communication_app.databinding.ActivityJoinGroupBinding;
import com.example.college_students_communication_app.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityGroupChatBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String messageSenderID;
    private String groupCode;

    private final List<Chat> chats = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter groupMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        groupCode = getIntent().getExtras().get("groupCode").toString();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setTitle(groupCode);

        groupMessageAdapter = new GroupMessageAdapter(chats);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.groupChatMessages.setLayoutManager(linearLayoutManager);
        binding.groupChatMessages.setAdapter(groupMessageAdapter);

        binding.sendMessageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.sendMessageButton){

        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        chats.clear();
        RootRef.child("Chats").child(groupCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                //chats.add(chat);

                groupMessageAdapter.notifyDataSetChanged();

                binding.groupChatMessages.smoothScrollToPosition(binding.groupChatMessages.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}