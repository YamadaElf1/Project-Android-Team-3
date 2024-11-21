package com.example.myfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button customerButton = findViewById(R.id.btn_customer);
        Button restaurantButton = findViewById(R.id.btn_restaurant);

        customerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            intent.putExtra("role", "customer");
            startActivity(intent);
        });

        restaurantButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            intent.putExtra("role", "restaurant");
            startActivity(intent);
        });

    }
}
