package com.example.myfinalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckSuggestionFragment extends Fragment {

    private ListView suggestionsListView;
    private List<String> suggestionsList;
    private ArrayAdapter<String> suggestionsAdapter;
    private DatabaseReference suggestionsDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_suggestion, container, false);

        suggestionsListView = view.findViewById(R.id.suggestions_list_view);
        suggestionsList = new ArrayList<>();

        suggestionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, suggestionsList);
        suggestionsListView.setAdapter(suggestionsAdapter);

        suggestionsDatabase = FirebaseDatabase.getInstance().getReference("suggestions");

        loadSuggestions();

        return view;
    }

    private void loadSuggestions() {
        suggestionsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suggestionsList.clear();
                for (DataSnapshot suggestionSnapshot : snapshot.getChildren()) {
                    String suggestion = suggestionSnapshot.getValue(String.class);
                    if (suggestion != null) {
                        suggestionsList.add(suggestion);
                    }
                }
                suggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load suggestions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
