package com.example.college_students_communication_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.college_students_communication_app.databinding.ActivityJoinGroupBinding;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logOut) {

            FirebaseAuth.getInstance().signOut();

            Intent mainIntent = new Intent(JoinGroupActivity.this, LoginActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

            return true;
        }

        return false;
    }
}