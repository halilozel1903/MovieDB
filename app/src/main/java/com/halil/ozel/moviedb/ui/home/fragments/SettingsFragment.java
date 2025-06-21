package com.halil.ozel.moviedb.ui.home.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.halil.ozel.moviedb.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private static final String PREFS = "settings";
    private static final String KEY_DARK = "dark";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SwitchMaterial themeSwitch = view.findViewById(R.id.switchTheme);
        boolean dark = requireContext().getSharedPreferences(PREFS, 0).getBoolean(KEY_DARK, false);
        themeSwitch.setChecked(dark);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            requireContext().getSharedPreferences(PREFS, 0).edit().putBoolean(KEY_DARK, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        return view;
    }
}
