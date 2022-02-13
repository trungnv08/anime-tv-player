package com.example.anime.player.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import com.example.anime.player.data.models.Movie;
import com.example.anime.player.data.models.MovieChapter;
import com.example.anime.player.service.WebCrawler;
import com.example.anime.player.service.impl.AnimeHayWebCrawler;
import com.example.anime.player.ui.activity.DetailsActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Handles video playback with media controls.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    private PlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;
    private final WebCrawler webCrawler = AnimeHayWebCrawler.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Movie movie =
                (Movie) requireActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);

        VideoSupportFragmentGlueHost glueHost =
                new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);

        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getActivity());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        mTransportControlGlue = new PlaybackTransportControlGlue<>(getActivity(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(movie.getTitle());
        mTransportControlGlue.setSubtitle(movie.getDescription());
        mTransportControlGlue.playWhenPrepared();

        MovieChapter chapter = movie.getCurrentChapter();
        webCrawler.getMovieChapter(chapter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(fetchedChapter -> chapter.setResources(fetchedChapter.getResources()))
                .doOnComplete(() -> {
                    playerAdapter.setDataSource(Uri.parse(chapter.getResources().get(0).getUrl()));

                })
                .doOnError(e -> {
                    Log.e("Playback", "error while fetching video: " + e.getMessage(), e);
                    Toast.makeText(getActivity(), "error while fetching video: " + e.getMessage(), Toast.LENGTH_LONG).show();
                })
                .subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }
}