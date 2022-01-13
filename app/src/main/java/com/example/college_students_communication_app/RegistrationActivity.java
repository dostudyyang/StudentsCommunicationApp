package com.example.college_students_communication_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.college_students_communication_app.databinding.ActivityLoginBinding;
import com.example.college_students_communication_app.databinding.ActivityRegistrationBinding;
import com.example.college_students_communication_app.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

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

    private void CreateNewAccount()
    {
        String username = binding.usernameEditText.getText().toString();
        String phone = binding.phoneEditText.getText().toString();
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString();

        if(!validateInputs(email, password, phone, confirmPassword, username))
        {

            validateInputs(email, password, phone, confirmPassword, username);

        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                User user = new User(currentUserID, email, username, phone);
                                mRootRef.child("Users").child(currentUserID).setValue(user);

                                //SendUserToJoinGroupActivity();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }

                            loadingBar.dismiss();
                        }
                    });
        }
    }
}