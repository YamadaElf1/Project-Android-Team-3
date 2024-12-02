package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends AppCompatActivity {

    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userId = intent.getStringExtra("userId");

        TextView toolbarUserName = findViewById(R.id.toolbar_user_name);
        TextView toolbarLogout = findViewById(R.id.toolbar_logout);

        if (userName != null) {
            toolbarUserName.setText("Welcome, " + userName);
        }

        toolbarLogout.setOnClickListener(v -> {
            Intent loginIntent = new Intent(CustomerMainActivity.this, Login.class);
            startActivity(loginIntent);
            finish();
        });

        if (savedInstanceState == null) {
            loadFragment(new MenuFragment());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_menu) {
                selectedFragment = new MenuFragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.nav_about_me) {
                selectedFragment = new AboutFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        try {
            if (fragment instanceof MenuFragment || fragment instanceof CartFragment || fragment instanceof OrderFragment) {
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                bundle.putString("userId", userId);
                fragment.setArguments(bundle);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading fragment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        }
}
