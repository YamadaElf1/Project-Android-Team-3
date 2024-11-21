package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText eTAccount, eTPassword;
    Button btnSignIn, btnRegister, btnForgetPassword;
    RadioGroup roleGroup;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eTAccount = findViewById(R.id.eTAccount);
        eTPassword = findViewById(R.id.eTPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgetPassword = findViewById(R.id.btnForgetPassword);
        roleGroup = findViewById(R.id.roleGroup);

        database = FirebaseDatabase.getInstance().getReference("accounts");

        btnSignIn.setOnClickListener(v -> loginUser());

        btnRegister.setOnClickListener(view -> {
            Intent registerIntent = new Intent(Login.this, Register.class);
            startActivity(registerIntent);
        });

        btnForgetPassword.setOnClickListener(view -> {
            Intent forgetPasswordIntent = new Intent(Login.this, ForgetPassword.class);
            startActivity(forgetPasswordIntent);
        });
    }
    private void loginUser() {
        String accountName = eTAccount.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();

        if (!accountName.isEmpty() && !password.isEmpty() && selectedRoleId != -1) {
            String selectedRole = selectedRoleId == R.id.rbCustomer ? "customer" : "restaurant";

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean userFound = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Account account = snapshot.getValue(Account.class);
                        if (account != null && account.getName().equals(accountName) && account.getPassword().equals(password)) {
                            userFound = true;

                            if (account.getRole().equals(selectedRole)) {
                                if ("customer".equals(selectedRole)) {
                                    Intent customerIntent = new Intent(Login.this, CustomerMainActivity.class);
                                    customerIntent.putExtra("userName", account.getName());
                                    customerIntent.putExtra("userId", account.getId());
                                    startActivity(customerIntent);
                                } else if ("restaurant".equals(selectedRole)) {
                                    Intent restaurantIntent = new Intent(Login.this, Home.class);
                                    restaurantIntent.putExtra("userName", account.getName());
                                    restaurantIntent.putExtra("userId", account.getId());
                                    startActivity(restaurantIntent);
                                }
                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Invalid role selected", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }

                    if (!userFound) {
                        Toast.makeText(Login.this, "Invalid account name or password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show();
        }
    }

}
