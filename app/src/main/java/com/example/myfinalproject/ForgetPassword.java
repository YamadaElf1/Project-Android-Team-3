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

import com.google.firebase.database.DatabaseReference;

public class ForgetPassword extends AppCompatActivity {

    EditText eTEmail, eTPassword, eTConfirmPassword;
    Button btnResetPassword, btnBackToLogIn;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        eTEmail = findViewById(R.id.email);
        eTPassword = findViewById(R.id.password);
        eTConfirmPassword = findViewById(R.id.confirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogIn = findViewById(R.id.back_to_login);


        database = FirebaseDatabase.getInstance().getReference("accounts");

        btnResetPassword.setOnClickListener(v -> resetPassword());

        btnBackToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ForgetPassword.this, Login.class);
                startActivity(registerIntent);
            }
        });
    }

    private void resetPassword() {
        String email = eTEmail.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();
        String confirmPassword = eTConfirmPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (password.equals(confirmPassword)) {
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
                                Intent loginIntent = new Intent(ForgetPassword.this, Login.class);
                                startActivity(loginIntent);
                                finish();
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
            } else {
                Toast.makeText(ForgetPassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ForgetPassword.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}