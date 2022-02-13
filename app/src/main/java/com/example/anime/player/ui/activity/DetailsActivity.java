package com.example.anime.player.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.anime.R;
import com.example.anime.player.ui.fragment.VideoDetailsFragment;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends FragmentActivity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";
    public static final String MOVIE_CARD = "MovieCard";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, new VideoDetailsFragment())
                    .commitNow();
        }
    }

}