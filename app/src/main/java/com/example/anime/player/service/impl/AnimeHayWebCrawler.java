package com.example.anime.player.service.impl;

import com.example.anime.player.data.Page;
import com.example.anime.player.data.PageRequest;
import com.example.anime.player.data.models.Category;
import com.example.anime.player.data.models.Movie;
import com.example.anime.player.data.models.MovieCard;
import com.example.anime.player.data.models.MovieChapter;
import com.example.anime.player.data.models.MovieResource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.reactivex.Observable;

public class AnimeHayWebCrawler extends BaseWebCrawler {
    private static final String ID_EXTRACT_REGEX = "-([0-9]+)\\.html$";
    final Pattern pattern = Pattern.compile(ID_EXTRACT_REGEX, Pattern.MULTILINE);
    private final Gson gson = new Gson();

    public AnimeHayWebCrawler() {
        super("https://animehay.club", "animeHay.club");
    }

    public static AnimeHayWebCrawler getInstance() {
        return AnimeHayWebCrawlerHolder.ANIME_HAY_WEB_CRAWLER;
    }


    @Override
    public Observable<List<MovieCard>> search(String keyword) {
        try {
            String searchQuery = String.format("/tim-kiem/%s.html", URLEncoder.encode(keyword, "utf-8"));
            return Observable.fromCallable(() -> getPageSource(searchQuery))
                    .map(document -> document.select("movies-list movie-item a"))
                    .map(elements -> elements.stream()
                            .map(this::buildMovieCard)
                            .collect(Collectors.toList())
                    );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<List<Category>> getAllCategories() {
        return Observable.fromCallable(this::getPageSource)
                .map(document -> document.select("#tab-cate a"))
                .map(elements -> elements.stream()
                        .map(element -> {
                            Category category = new Category();

                            String path = element.attr("href");
                            category.setId(extractNumberFromPath(path));

                            category.setName(element.text());
                            category.setPath(path.replace(".html", ""));

                            return category;
                        })
                        .collect(Collectors.toList()));

    }

    @Override
    public Observable<Page<MovieCard>> findByCategory(Category category, PageRequest pageRequest) {
        return Observable.fromCallable(() -> getPageSource(String.format("%s/trang-%s.html", category.getPath(), pageRequest.getPage())))
                .map(document -> {
                    Elements pagination = document.select(".pagination a");
                    String path = pagination.last().attr("href");
                    Elements elements = document.select(".movies-list .movie-item a");
                    List<MovieCard> result = elements.stream()
                            .map(this::buildMovieCard)
                            .collect(Collectors.toList());
                    if (pageRequest.getPageSize() == 0) {
                        pageRequest.setPageSize(result.size());
                        pageRequest.setTotal(extractNumberFromPath(path).intValue() * result.size());
                    }

                    return Page.of(pageRequest.getPage(),
                            pageRequest.getPageSize(),
                            pageRequest.getTotal(),
                            result);
                });

    }

    @Override
    public Observable<Movie> getMovieDetail(MovieCard movieCard) {
        return Observable.fromCallable(() -> getPageSourceFullUrl(movieCard.getDetailUrl()))
                .map(document -> {
                    Movie movie = Movie.fromCard(movieCard);
                    Element description = document.select(".desc.ah-frame-bg").first();
                    movie.setDescription(description.text());
                    Elements episodes = document.select(".list-item-episode a");
                    episodes.stream()
                            .map(element -> {
                                MovieChapter chapter = new MovieChapter();
                                chapter.setId(element.text());
                                chapter.setTitle(element.text());
                                chapter.setChapterUrl(element.attr("href"));
                                return chapter;
                            })
                            .forEach(movie.getChapters()::add);
                    return movie;
                });

    }

    @Override
    public Observable<MovieChapter> getMovieChapter(MovieChapter chapter) {
        return Observable.fromCallable(() -> getPageSourceFullUrl(chapter.getChapterUrl()))
                .map(document -> document.select("script"))
                .map(elements -> elements.stream()
                        .filter(element -> element.data().contains("var $info_play_video ="))
                        .findFirst())
                .map(element -> {
                    String scriptData = element.orElse(new Element("script")).data();
                    String jsonRaw = scriptData.split("source_fbo:")[1].split(",")[0]
                            .trim();
                    List<Map<String, String>> list = gson.fromJson(jsonRaw, new TypeToken<List<Map<String, String>>>() {
                    }.getType());

                    list.forEach(item -> {
                        MovieResource resource = new MovieResource();
                        resource.setServerName("FBO");
                        resource.setUrl(item.get("file"));

                        chapter.getResources().add(resource);
                    });

                    return chapter;
                });
    }

    private Long extractNumberFromPath(String path) {
        final Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return Long.parseLong(Objects.requireNonNull(matcher.group(1)));
        }
        return -1L;
    }

    private MovieCard buildMovieCard(Element element) {
        MovieCard movieCard = new MovieCard();

        String detailUrl = element.attr("href");
        movieCard.setId(extractNumberFromPath(detailUrl));

        movieCard.setTitle(element.attr("title"));
        movieCard.setDetailUrl(detailUrl);
        Element imgTag = element.select("img").first();

        movieCard.setCardImageUrl(imgTag.attr("src"));

        return movieCard;
    }

    private static class AnimeHayWebCrawlerHolder {
        public static final AnimeHayWebCrawler ANIME_HAY_WEB_CRAWLER = new AnimeHayWebCrawler();
    }
}
