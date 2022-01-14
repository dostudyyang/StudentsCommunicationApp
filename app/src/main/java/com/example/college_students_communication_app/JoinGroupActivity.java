package com.example.college_students_communication_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.college_students_communication_app.databinding.ActivityJoinGroupBinding;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityJoinGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinGroupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.joinGroupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.joinGroupButton){
            String groupCode = binding.groupCodeEditText.getText().toString();

            if (TextUtils.isEmpty(groupCode)){
                binding.groupCodeEditText.setError("Please enter Code");
            }
            else {
                Intent mainIntent = new Intent(JoinGroupActivity.this, GroupChatActivity.class);
                mainIntent.putExtra("groupCode", groupCode);
                startActivity(mainIntent);
            }
        }
    }
}