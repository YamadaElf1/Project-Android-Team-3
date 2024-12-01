package com.example.myfinalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AboutFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvAboutUs;
    private TextView tvMakeSuggestion;
    private DatabaseReference suggestionDatabase;

    private static final String TAG = "AboutFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        tvUsername = view.findViewById(R.id.me_username);
        tvAboutUs = view.findViewById(R.id.me_guanyu);
        tvMakeSuggestion = view.findViewById(R.id.me_suggestion);

        suggestionDatabase = FirebaseDatabase.getInstance().getReference("suggestions");

        tvAboutUs.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("About Us")
                    .setMessage("Developers: " +
                            "\nSun Wentao" +
                            "\nHao Juncheng" +
                            "\nJia Changru" +
                            "\nLiao Zhiyu" +
                            "\nWe are a passionate team dedicated to delivering high-quality software solutions.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        tvMakeSuggestion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Make A Suggestion");

            View dialogView = inflater.inflate(R.layout.dialog_suggestion, null);
            builder.setView(dialogView);

            final EditText input = dialogView.findViewById(R.id.suggestion_input);

            builder.setPositiveButton("Submit", (dialog, which) -> {
                String suggestion = input.getText().toString().trim();
                if (!suggestion.isEmpty()) {
                    String suggestionId = suggestionDatabase.push().getKey();
                    if (suggestionId != null) {
                        suggestionDatabase.child(suggestionId).setValue(suggestion)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Suggestion submitted successfully.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to submit suggestion: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(getContext(), "Suggestion cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        if (getActivity() != null) {
            TextView toolbarUserName = getActivity().findViewById(R.id.toolbar_user_name);
            if (toolbarUserName != null) {
                String userName = toolbarUserName.getText().toString();
                Log.d(TAG, "Retrieved user name from toolbar: " + userName);
                tvUsername.setText(userName.replace("Welcome, ", ""));
            } else {
                Log.e(TAG, "Toolbar user name TextView not found.");
                Toast.makeText(getContext(), "Unable to find toolbar user name.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Activity is null.");
            Toast.makeText(getContext(), "Activity is not available.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
