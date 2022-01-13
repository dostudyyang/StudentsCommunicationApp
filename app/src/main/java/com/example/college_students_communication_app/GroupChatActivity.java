package com.example.college_students_communication_app;

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
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private ActivityGroupChatBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private final List<Chat> chats = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter groupMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }
}