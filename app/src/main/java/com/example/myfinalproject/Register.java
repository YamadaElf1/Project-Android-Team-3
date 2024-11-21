package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText eTAccount, eTEmail, eTPassword;
    RadioGroup roleGroup;
    Button btnRegister, btnExistingAccount;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        eTAccount = findViewById(R.id.eTAccount);
        eTEmail = findViewById(R.id.eTEmail);
        eTPassword = findViewById(R.id.eTPassword);
        roleGroup = findViewById(R.id.radio_group_role);
        btnRegister = findViewById(R.id.btnRegister);
        btnExistingAccount = findViewById(R.id.btnExistingAccount);

        database = FirebaseDatabase.getInstance().getReference("accounts");

        btnRegister.setOnClickListener(v -> saveAccount());

        btnExistingAccount.setOnClickListener(view -> {
            Intent registerIntent = new Intent(Register.this, Login.class);
            startActivity(registerIntent);
        });
    }

    private void saveAccount() {
        String accountName = eTAccount.getText().toString().trim();
        String email = eTEmail.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();

        if (!accountName.isEmpty() && !email.isEmpty() && !password.isEmpty() && selectedRoleId != -1) {
            String role = selectedRoleId == R.id.radio_customer ? "customer" : "restaurant";
            String id = database.push().getKey();
            Account account = new Account(id, accountName, email, password, role);

            database.child(id).setValue(account)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Register.this, "Account Registered successfully", Toast.LENGTH_SHORT).show();
                        eTAccount.setText("");
                        eTEmail.setText("");
                        eTPassword.setText("");

                        Intent registerIntent = new Intent(Register.this, Login.class);
                        startActivity(registerIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show();
        }
    }
}
