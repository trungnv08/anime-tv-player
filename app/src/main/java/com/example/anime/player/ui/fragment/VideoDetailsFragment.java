package com.example.anime.player.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.app.DetailsSupportFragmentBackgroundController;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.anime.R;
import com.example.anime.player.data.models.Holder;
import com.example.anime.player.data.models.Movie;
import com.example.anime.player.data.models.MovieCard;
import com.example.anime.player.data.models.MovieChapter;
import com.example.anime.player.service.WebCrawler;
import com.example.anime.player.service.impl.AnimeHayWebCrawler;
import com.example.anime.player.ui.activity.DetailsActivity;
import com.example.anime.player.ui.activity.MainActivity;
import com.example.anime.player.ui.activity.PlaybackActivity;
import com.example.anime.player.ui.presenter.DetailsDescriptionPresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsSupportFragment {
    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH = -1;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 340;

    private final WebCrawler webCrawler = AnimeHayWebCrawler.getInstance();

    private MovieCard mSelectedMovieCard;
    private final Holder<Movie> mSelectedMovie = new Holder<>();

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private DetailsSupportFragmentBackgroundController mDetailsBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsSupportFragmentBackgroundController(this);

        mSelectedMovieCard =
                (MovieCard) requireActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE_CARD);
        if (mSelectedMovieCard != null) {

            webCrawler.getMovieDetail(mSelectedMovieCard)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterNext(mSelectedMovie::setValue)
                    .doOnComplete(() -> {
                        mPresenterSelector = new ClassPresenterSelector();
                        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
                        setupDetailsOverviewRow();
                        setupDetailsOverviewRowPresenter();
                        setAdapter(mAdapter);
                        initializeBackground(mSelectedMovie.getValue());
                        setOnItemViewClickedListener(new ItemViewClickedListener());
                    })
                    .doOnError(e -> Log.e(TAG, "error while fetching categories: " + e.getMessage(), e))
                    .subscribe();


        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void initializeBackground(Movie data) {
        mDetailsBackground.enableParallax();
        Glide.with(requireActivity())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .load(data.getCardImageUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {
                        mDetailsBackground.setCoverBitmap(bitmap);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.getValue().toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie.getValue());
        row.setImageDrawable(
                ContextCompat.getDrawable(requireActivity(), R.drawable.default_background));
        int width = convertDpToPixel(requireActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(requireActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(requireActivity())
                .load(mSelectedMovie.getValue().getBackgroundImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new CustomTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable,
                                                @Nullable Transition<? super Drawable> transition) {
                        Log.d(TAG, "details overview card image url ready: " + drawable);
                        row.setImageDrawable(drawable);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        actionAdapter.add(
                new Action(
                        ACTION_WATCH,
                        getResources().getString(R.string.watch),
                        getResources().getString(R.string.watch_2)));

        List<MovieChapter> chapters = mSelectedMovie.getValue().getChapters();
        for (int i = 0; i < chapters.size(); i++) {
            MovieChapter movieChapter = chapters.get(i);
            Action action = new Action(i, movieChapter.getTitle());
            actionAdapter.add(action);
        }
        row.setActionsAdapter(actionAdapter);
        mAdapter.add(row);
//        mAdapter.add(chapterAdapter);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(requireActivity(), R.color.selected_background));

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(action -> {
            Intent intent = new Intent(getActivity(), PlaybackActivity.class);
            Movie movie = mSelectedMovie.getValue();

            if (action.getId() == ACTION_WATCH) {
                movie.setSelectedChapter(movie.getChapters().size() - 1);
            } else {
                movie.setSelectedChapter((int) (action.getId()));
            }
            intent.putExtra(DetailsActivity.MOVIE, movie);
            startActivity(intent);
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }


    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {

            if (item instanceof Movie) {
                Log.d(TAG, "Item: " + item);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie.getValue());

                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                requireActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                DetailsActivity.SHARED_ELEMENT_NAME)
                                .toBundle();
                requireActivity().startActivity(intent, bundle);
            }
        }
    }
}