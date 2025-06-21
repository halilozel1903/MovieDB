package com.halil.ozel.moviedb.ui.detail;

import static com.halil.ozel.moviedb.data.Api.TMDbAPI.IMAGE_BASE_URL_500;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.R;
import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.PersonDetail;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class CastDetailActivity extends Activity {

    @Inject
    TMDbAPI tmDbAPI;

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvBirthday;
    private TextView tvBiography;

    private int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        setContentView(R.layout.activity_cast_detail);

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvBiography = findViewById(R.id.tvBiography);

        personId = getIntent().getIntExtra("person_id", 0);
        loadPersonDetail();
    }

    private void loadPersonDetail() {
        tmDbAPI.getPersonDetail(personId, TMDb_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindPerson,
                        e -> {
                            Timber.e(e, "Error fetching person detail: %s", e.getMessage());
                            Toast.makeText(this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                        });
    }

    private void bindPerson(PersonDetail detail) {
        tvName.setText(detail.getName());
        tvBirthday.setText(getString(R.string.birthday_format, detail.getBirthday()));
        tvBiography.setText(detail.getBiography());
        if (detail.getProfile_path() != null) {
            Picasso.get().load(IMAGE_BASE_URL_500 + detail.getProfile_path()).into(ivProfile);
        }
    }
}
