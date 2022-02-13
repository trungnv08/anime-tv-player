package com.example.anime.player.data;

public class PageRequest {
    private final int page;
    private int pageSize;
    private int total;

    public PageRequest(int page, int pageSize, int total) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public static PageRequest of(int page) {
        return new PageRequest(page, 0, 0);
    }

    public static PageRequest of(int page, int pageSize) {
        return new PageRequest(page, pageSize, 0);
    }

    public static PageRequest of(int page, int pageSize, int total) {
        return new PageRequest(page, pageSize, total);
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isLastPage() {
        return page * pageSize >= total;
    }
}
