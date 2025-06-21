package com.halil.ozel.moviedb.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.halil.ozel.moviedb.data.models.Results;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {

    private static final String PREF_NAME = "favorites";
    private static final String KEY_LIST = "list";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static JsonAdapter<List<Results>> adapter = new Moshi.Builder().build()
            .adapter(Types.newParameterizedType(List.class, Results.class));

    public static List<Results> load(Context context) {
        String json = prefs(context).getString(KEY_LIST, null);
        if (json == null) return new ArrayList<>();
        try {
            return adapter.fromJson(json);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void save(Context context, List<Results> list) {
        prefs(context).edit().putString(KEY_LIST, adapter.toJson(list)).apply();
    }

    public static void add(Context context, Results item) {
        List<Results> list = load(context);
        for (Results r : list) {
            if (r.getId().equals(item.getId())) return;
        }
        list.add(item);
        save(context, list);
    }

    public static void remove(Context context, int id) {
        List<Results> list = load(context);
        List<Results> newList = new ArrayList<>();
        for (Results r : list) {
            if (!r.getId().equals(id)) {
                newList.add(r);
            }
        }
        save(context, newList);
    }
}
