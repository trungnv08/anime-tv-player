package com.example.anime.player.service;

import com.example.anime.player.data.Page;
import com.example.anime.player.data.PageRequest;
import com.example.anime.player.data.models.Category;
import com.example.anime.player.data.models.Movie;
import com.example.anime.player.data.models.MovieCard;
import com.example.anime.player.data.models.MovieChapter;

import java.util.List;

import io.reactivex.Observable;

public interface WebCrawler {

    Observable<List<MovieCard>> search(String keyword);

    Observable<List<Category>> getAllCategories();

    Observable<Page<MovieCard>> findByCategory(Category category, PageRequest pageRequest);

    Observable<Movie> getMovieDetail(MovieCard movieCard);

    Observable<MovieChapter> getMovieChapter(MovieChapter chapter);

    String getResourceName();
}
