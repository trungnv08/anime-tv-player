package com.example.anime.player.data.models;

import java.io.Serializable;
import java.util.StringJoiner;

public class MovieResource implements Serializable {
    String serverName;
    String url;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MovieResource.class.getSimpleName() + "[", "]")
                .add("serverName='" + serverName + "'")
                .add("url='" + url + "'")
                .toString();
    }
}
