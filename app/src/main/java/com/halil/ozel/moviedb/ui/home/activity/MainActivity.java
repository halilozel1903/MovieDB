package com.halil.ozel.moviedb.ui.home.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.ui.home.fragments.FavoriteFragment;
import com.halil.ozel.moviedb.ui.home.fragments.MoviesFragment;
import com.halil.ozel.moviedb.ui.home.fragments.SettingsFragment;
import com.halil.ozel.moviedb.ui.home.fragments.TvSeriesFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.navigation_tv) {
                fragment = new TvSeriesFragment();
            } else if (id == R.id.navigation_favorites) {
                fragment = new FavoriteFragment();
            } else if (id == R.id.navigation_settings) {
                fragment = new SettingsFragment();
            } else {
                fragment = new MoviesFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        });

        nav.setSelectedItemId(R.id.navigation_movies);
    }
}
