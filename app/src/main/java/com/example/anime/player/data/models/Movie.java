package com.example.anime.player.data.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 */
public class Movie implements Serializable {
    static final long serialVersionUID = 727566175075960653L;
    private long id;
    private String title;
    private String description;
    private String bgImageUrl;
    private String cardImageUrl;
    private String videoUrl;
    private String studio;

    private int selectedChapter;

    private List<MovieChapter> chapters = new LinkedList<>();

    public Movie() {
    }

    public static Movie fromCard(MovieCard movieCard) {
        Movie movie = new Movie();
        movie.setId(movieCard.getId());
        movie.setTitle(movieCard.getTitle());
        movie.setBgImageUrl(movieCard.getCardImageUrl());
        movie.setCardImageUrl(movieCard.getCardImageUrl());
        return movie;
    }

    public MovieChapter getChapter(int chapter) {
        if (chapters.isEmpty()) {
            return null;
        }
        if (chapter == 0) {
            return chapters.get(0);
        }
        if (chapter > chapters.size()) {
            return chapters.get(chapters.size() - 1);
        }
        return chapters.get(chapter - 1);
    }

    public MovieChapter getCurrentChapter() {
        return getChapter(selectedChapter);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public List<MovieChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<MovieChapter> chapters) {
        this.chapters = chapters;
    }

    public int getSelectedChapter() {
        return selectedChapter;
    }

    public void setSelectedChapter(int selectedChapter) {
        this.selectedChapter = selectedChapter;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Movie.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("description='" + description + "'")
                .add("bgImageUrl='" + bgImageUrl + "'")
                .add("cardImageUrl='" + cardImageUrl + "'")
                .add("videoUrl='" + videoUrl + "'")
                .add("studio='" + studio + "'")
                .add("selectedChapter=" + selectedChapter)
                .add("chapters=" + chapters)
                .toString();
    }
}