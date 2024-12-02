package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgetPassword extends AppCompatActivity {

    private EditText eTEmail, eTPassword, eTConfirmPassword;
    private Button btnResetPassword, btnBackToLogIn;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initializeViews();

        database = FirebaseDatabase.getInstance().getReference("accounts");

        btnResetPassword.setOnClickListener(v -> resetPassword());
        btnBackToLogIn.setOnClickListener(view -> navigateToLogin());
    }

    private void initializeViews() {
        eTEmail = findViewById(R.id.email);
        eTPassword = findViewById(R.id.password);
        eTConfirmPassword = findViewById(R.id.confirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogIn = findViewById(R.id.back_to_login);
    }

    private void resetPassword() {
        String email = eTEmail.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();
        String confirmPassword = eTConfirmPassword.getText().toString().trim();

        if (!validateInputs(email, password, confirmPassword)) {
            return;
        }

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null && account.getEmail().equals(email)) {
                        String accountId = snapshot.getKey();
                        account.setPassword(password);
                        database.child(accountId).setValue(account);

                        Toast.makeText(ForgetPassword.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                        userFound = true;
                        break;
                    }
                }

                if (!userFound) {
                    Toast.makeText(ForgetPassword.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ForgetPassword.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            eTEmail.setError("Email is required");
            eTEmail.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eTEmail.setError("Enter a valid email address");
            eTEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            eTPassword.setError("Password is required");
            eTPassword.requestFocus();
            return false;
        } else if (password.length() < 6) {
            eTPassword.setError("Password must be at least 6 characters long");
            eTPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            eTConfirmPassword.setError("Please confirm your password");
            eTConfirmPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)) {
            eTConfirmPassword.setError("Passwords do not match");
            eTConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(ForgetPassword.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
