package com.mario.it.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mario.it.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    private TextInputLayout nameInputLayout;
    private TextInputEditText nameEditText;
    private MaterialButton saveBtn;
    private TextView txtAppVersion, txtDbVersion;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameInputLayout = binding.textInputLayoutName;
        nameEditText = binding.editName;
        saveBtn = binding.btnSaveName;
        txtAppVersion = binding.txtAppVersion;
        txtDbVersion = binding.txtDbVersion;

        // Load existing name from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String savedName = prefs.getString("username", "");
        if (!TextUtils.isEmpty(savedName)) {
            nameEditText.setText(savedName);
        }

        // Save button click
        saveBtn.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                nameInputLayout.setError("Name cannot be empty");
            } else {
                nameInputLayout.setError(null);
                prefs.edit().putString("username", newName).apply();
                Toast.makeText(getContext(), "Name updated to " + newName, Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Load Firebase Versions
        loadFirebaseVersions();

        return root;
    }

    private void loadFirebaseVersions() {
        DatabaseReference rootRef = FirebaseDatabase
                .getInstance("https://iti-it-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();

        // App version
        rootRef.child("app").child("version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String version = snapshot.getValue(String.class);
                txtAppVersion.setText("App Version: " + version);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtAppVersion.setText("App Version: Error");
            }
        });

        // Database version
        rootRef.child("database").child("version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String version = snapshot.getValue(String.class);
                txtDbVersion.setText("Database Version: " + version);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtDbVersion.setText("Database Version: Error");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}