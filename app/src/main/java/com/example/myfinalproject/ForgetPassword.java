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

        // 初始化控件
        initializeViews();

        // 初始化数据库引用
        database = FirebaseDatabase.getInstance().getReference("accounts");

        // 设置按钮点击事件
        btnResetPassword.setOnClickListener(v -> resetPassword());
        btnBackToLogIn.setOnClickListener(view -> navigateToLogin());
    }

    // 初始化控件
    private void initializeViews() {
        eTEmail = findViewById(R.id.email);
        eTPassword = findViewById(R.id.password);
        eTConfirmPassword = findViewById(R.id.confirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogIn = findViewById(R.id.back_to_login);
    }

    // 密码重置逻辑
    private void resetPassword() {
        String email = eTEmail.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();
        String confirmPassword = eTConfirmPassword.getText().toString().trim();

        // 验证输入
        if (!validateInputs(email, password, confirmPassword)) {
            return; // 验证失败时停止
        }

        // 查找用户并更新密码
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

    // 验证用户输入
    private boolean validateInputs(String email, String password, String confirmPassword) {
        // 验证邮箱
        if (email.isEmpty()) {
            eTEmail.setError("Email is required");
            eTEmail.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eTEmail.setError("Enter a valid email address");
            eTEmail.requestFocus();
            return false;
        }

        // 验证密码
        if (password.isEmpty()) {
            eTPassword.setError("Password is required");
            eTPassword.requestFocus();
            return false;
        } else if (password.length() < 6) {
            eTPassword.setError("Password must be at least 6 characters long");
            eTPassword.requestFocus();
            return false;
        }

        // 验证确认密码
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

    // 跳转到登录页面
    private void navigateToLogin() {
        Intent loginIntent = new Intent(ForgetPassword.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
