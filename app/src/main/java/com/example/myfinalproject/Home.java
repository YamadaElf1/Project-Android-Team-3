package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Restaurant Dashboard");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView headerUserName = headerView.findViewById(R.id.header_user_name);
        TextView headerUserId = headerView.findViewById(R.id.header_user_id);

        if (userName != null) {
            headerUserName.setText("Name: " + userName);
        }
        if (userId != null) {
            headerUserId.setText("ID: " + userId);
        }

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FoodMenuFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_menu_management);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_menu_management) {
            selectedFragment = new FoodMenuFragment();
        } else if (item.getItemId() == R.id.nav_order_history) {
            selectedFragment = new AllOrderHistoryFragment();
        } else if (item.getItemId() == R.id.nav_Check) {
            selectedFragment = new CheckSuggestionFragment();
        } else if (item.getItemId() == R.id.nav_logout) {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(Home.this, Login.class);
            startActivity(loginIntent);
            finish();
            return true;
        } else {
            Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
