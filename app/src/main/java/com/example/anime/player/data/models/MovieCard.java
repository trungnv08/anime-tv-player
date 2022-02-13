package com.example.anime.player.data.models;

import java.io.Serializable;
import java.util.StringJoiner;

public class MovieCard implements Serializable {

    private long id;
    private String title;
    private String cardImageUrl;
    private String detailUrl;

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

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MovieCard.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("cardImageUrl='" + cardImageUrl + "'")
                .add("detailUrl='" + detailUrl + "'")
                .toString();
    }
}
