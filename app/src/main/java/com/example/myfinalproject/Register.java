package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText eTAccount, eTEmail, eTPassword;
    private RadioGroup roleGroup;
    private Button btnRegister, btnExistingAccount;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化控件
        initializeViews();

        // 初始化数据库引用
        database = FirebaseDatabase.getInstance().getReference("accounts");

        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> saveAccount());

        // 已有账号按钮点击事件
        btnExistingAccount.setOnClickListener(view -> navigateToLogin());
    }

    // 初始化控件
    private void initializeViews() {
        eTAccount = findViewById(R.id.eTAccount);
        eTEmail = findViewById(R.id.eTEmail);
        eTPassword = findViewById(R.id.eTPassword);
        roleGroup = findViewById(R.id.radio_group_role);
        btnRegister = findViewById(R.id.btnRegister);
        btnExistingAccount = findViewById(R.id.btnExistingAccount);
    }

    // 保存账户到 Firebase
    private void saveAccount() {
        String accountName = eTAccount.getText().toString().trim();
        String email = eTEmail.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();

        // 验证输入
        if (!validateInputs(accountName, email, password, selectedRoleId)) {
            return; // 验证失败时停止
        }

        // 获取用户角色
        String role = selectedRoleId == R.id.radio_customer ? "customer" : "restaurant";

        // 为用户生成唯一 ID
        String id = database.push().getKey();
        Account account = new Account(id, accountName, email, password, role);

        // 将账户保存到 Firebase
        database.child(id).setValue(account)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register.this, "Account Registered successfully", Toast.LENGTH_SHORT).show();
                    clearFields(); // 清空输入框
                    navigateToLogin(); // 跳转到登录页面
                })
                .addOnFailureListener(e -> Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // 验证用户输入
    private boolean validateInputs(String accountName, String email, String password, int selectedRoleId) {
        if (accountName.isEmpty()) {
            eTAccount.setError("Account name is required");
            eTAccount.requestFocus();
            return false;
        } else if (accountName.length() < 3) {
            eTAccount.setError("Account name must be at least 3 characters long");
            eTAccount.requestFocus();
            return false;
        }

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

        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 清空输入框
    private void clearFields() {
        eTAccount.setText("");
        eTEmail.setText("");
        eTPassword.setText("");
        roleGroup.clearCheck();
    }

    // 跳转到登录页面
    private void navigateToLogin() {
        Intent loginIntent = new Intent(Register.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
