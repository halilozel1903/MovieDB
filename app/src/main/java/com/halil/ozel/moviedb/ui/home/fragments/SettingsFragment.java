package com.halil.ozel.moviedb.ui.home.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private static final String PREFS = "settings";
    private static final String KEY_DARK = "dark";
    private static final String KEY_LANG = "language";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner langSpinner = view.findViewById(R.id.spinnerLanguage);
        String lang = requireContext().getSharedPreferences(PREFS, 0).getString(KEY_LANG, "en");
        langSpinner.setSelection("tr".equals(lang) ? 1 : 0);
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                String selected = position == 1 ? "tr" : "en";
                SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, 0);
                if (!selected.equals(prefs.getString(KEY_LANG, "en"))) {
                    prefs.edit().putString(KEY_LANG, selected).apply();
                    App.instance().applyLocale(selected);
                    requireActivity().recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

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
