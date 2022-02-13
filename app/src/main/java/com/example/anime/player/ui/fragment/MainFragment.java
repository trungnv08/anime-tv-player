package com.example.anime.player.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.anime.R;
import com.example.anime.player.data.PageRequest;
import com.example.anime.player.data.models.Category;
import com.example.anime.player.data.models.Holder;
import com.example.anime.player.data.models.MovieCard;
import com.example.anime.player.service.WebCrawler;
import com.example.anime.player.service.impl.AnimeHayWebCrawler;
import com.example.anime.player.ui.activity.BrowseErrorActivity;
import com.example.anime.player.ui.activity.DetailsActivity;
import com.example.anime.player.ui.presenter.CardPresenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends VerticalGridSupportFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
    private final WebCrawler webCrawler = AnimeHayWebCrawler.getInstance();
    private final List<Category> categories = new ArrayList<>();
    private final Holder<PageRequest> movieCardPageReq = new Holder<>(PageRequest.of(1));
    private final ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new CardPresenter());
    private final Holder<Boolean> isLoadingCards = new Holder<>(false);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VerticalGridPresenter verticalGridPresenter = new VerticalGridPresenter();
        verticalGridPresenter.setNumberOfColumns(4);
        setGridPresenter(verticalGridPresenter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();
        setAdapter(rowsAdapter);
        webCrawler.getAllCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(categories::addAll)
                .doOnComplete(this::setupEventListeners)
                .doOnError(e -> {
                    Log.e(TAG, "error while fetching categories: " + e.getMessage(), e);
                    Toast.makeText(requireActivity(), "error while fetching categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                })
                .doFinally(this::loadRows)
                .subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer);
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows() {
        if (isLoadingCards.getValue()) {
            return;
        }
        isLoadingCards.setValue(true);
        List<MovieCard> list = new LinkedList<>();
        PageRequest currentPage = movieCardPageReq.getValue();
        webCrawler.findByCategory(categories.get(0), currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(paged -> list.addAll(paged.getElements()))
                .doOnComplete(this::setupEventListeners)
                .doOnError(e -> {
                    Log.e(TAG, "error while fetching MovieCards: " + e.getMessage(), e);
                    Toast.makeText(requireActivity(), "error while fetching MovieCards: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .doFinally(() -> {
                    movieCardPageReq.setValue(PageRequest.of(currentPage.getPage() + 1, currentPage.getPageSize(), currentPage.getTotal()));
                    list.forEach(rowsAdapter::add);
                    isLoadingCards.setValue(false);
                })
                .subscribe();

    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(requireActivity());
        mBackgroundManager.attach(requireActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(requireActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title) + webCrawler.getResourceName()); // Badge, when set, takes precedent
        // over title
//        setHeadersState(HEADERS_ENABLED);
//        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
//        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(requireActivity(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(view -> Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                .show());

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(requireActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new CustomTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable,
                                                @Nullable Transition<? super Drawable> transition) {
                        mBackgroundManager.setDrawable(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof MovieCard) {
                MovieCard movieCard = (MovieCard) item;
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE_CARD, movieCard);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME)
                        .toBundle();
                requireActivity().startActivity(intent, bundle);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if (item instanceof MovieCard) {
                MovieCard movieCard = ((MovieCard) item);
                mBackgroundUri = movieCard.getCardImageUrl();
                startBackgroundTimer();
                int index = rowsAdapter.indexOf(item);
                int reloadPosition = rowsAdapter.size() - 5;
                if (index > reloadPosition && !movieCardPageReq.getValue().isLastPage()) {
                    loadRows();
                }
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(() -> updateBackground(mBackgroundUri));
        }
    }


}