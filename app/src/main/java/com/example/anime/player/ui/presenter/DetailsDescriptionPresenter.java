package com.example.anime.player.ui.presenter;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.example.anime.player.data.models.Movie;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {


    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.getTitle());
            viewHolder.getSubtitle().setText(movie.getTitle());
            viewHolder.getBody().setText(movie.getDescription());
        }
    }
}