package com.example.anime.player.data;

import java.util.List;

public class Page<T> {

    private final int page;
    private final int pageSize;
    private final int total;
    List<T> elements;


    public Page(int page, int pageSize, int total, List<T> elements) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.elements = elements;
    }

    public Page(Page<T> page, List<T> elements) {
        this.page = page.page;
        this.pageSize = page.pageSize;
        this.total = page.total;
        this.elements = elements;
    }

    public static <U> Page<U> of(int page, int pageSize, int total, List<U> elements) {
        return new Page<>(page, pageSize, total, elements);
    }

    public static <U> Page<U> of(Page<U> page, List<U> elements) {
        return new Page<>(page, elements);
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getElements() {
        return elements;
    }

}
