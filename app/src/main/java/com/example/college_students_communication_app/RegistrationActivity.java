package com.example.college_students_communication_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.example.college_students_communication_app.databinding.ActivityLoginBinding;
import com.example.college_students_communication_app.databinding.ActivityRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}