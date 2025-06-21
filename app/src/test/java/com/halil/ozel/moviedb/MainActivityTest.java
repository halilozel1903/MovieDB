import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.halil.ozel.moviedb.data.Api.TMDbAPI.TMDb_API_KEY;

import androidx.recyclerview.widget.RecyclerView;

import com.halil.ozel.moviedb.data.Api.TMDbAPI;
import com.halil.ozel.moviedb.data.models.ResponseNowPlaying;
import com.halil.ozel.moviedb.data.models.Results;
import com.halil.ozel.moviedb.ui.home.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public class MainActivityTest {

    @Mock
    TMDbAPI api;

    @Mock
    RecyclerView.Adapter popularAdapter;

    @Mock
    RecyclerView.Adapter nowPlayingAdapter;

    private MainActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override public rx.Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        RxJavaHooks.setOnIOScheduler(s -> Schedulers.immediate());

        activity = new MainActivity();
        activity.tmDbAPI = api;
        activity.popularMovieDataList = new ArrayList<>();
        activity.nowPlayingDataList = new ArrayList<>();
        activity.popularMovieAdapter = popularAdapter;
        activity.nowPlayingMovieAdapter = nowPlayingAdapter;
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
        RxJavaHooks.reset();
    }

    @Test
    public void getNowPlaying_populatesPopularList() {
        Results r = new Results();
        r.setTitle("Movie A");
        List<Results> list = Arrays.asList(r);
        ResponseNowPlaying response = new ResponseNowPlaying();
        response.setResults(list);

        when(api.getNowPlaying(TMDb_API_KEY, 1)).thenReturn(Observable.just(response));

        activity.getNowPlaying();

        assertEquals(list, activity.popularMovieDataList);
        verify(popularAdapter).notifyDataSetChanged();
    }

    @Test
    public void getPopularMovies_populatesNowPlayingList() {
        Results r = new Results();
        r.setTitle("Movie B");
        List<Results> list = Arrays.asList(r);
        ResponseNowPlaying response = new ResponseNowPlaying();
        response.setResults(list);

        when(api.getPopularMovie(TMDb_API_KEY, 1)).thenReturn(Observable.just(response));

        activity.getPopularMovies();

        assertEquals(list, activity.nowPlayingDataList);
        verify(nowPlayingAdapter).notifyDataSetChanged();
    }
}
