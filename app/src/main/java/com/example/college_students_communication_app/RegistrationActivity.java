package com.example.college_students_communication_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import com.example.college_students_communication_app.databinding.ActivityLoginBinding;
import com.example.college_students_communication_app.databinding.ActivityRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

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

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        loadingBar = new ProgressDialog(this);

        binding.loginLink.setOnClickListener(this);
        binding.signupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.signupButton){

        }

        if (view == binding.loginLink){

        }
    }

    public boolean validateInputs(String email, String password, String phone, String confirmPassword, String username){
        if (TextUtils.isEmpty(username))
        {
            binding.usernameEditText.setError("Please enter username...");
            return  false;
        }
        else if (TextUtils.isEmpty(phone))
        {
            binding.phoneEditText.setError("Please enter phone...");
            return  false;
        }
        else if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.emailEditText.setError("Please enter email...");
            return false;
        }
        else if (TextUtils.isEmpty(password))
        {
            binding.passwordEditText.setError("Please enter password...");
            return  false;
        }
        else if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword))
        {
            binding.passwordEditText.setError("Password should match Confirm Password");
            binding.confirmPasswordEditText.setError("Password should match Confirm Password");
            return  false;
        }
        else {
            return true;
        }
    }
}