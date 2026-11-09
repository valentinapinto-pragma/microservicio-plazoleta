package com.pragma.plazoleta.domain.model;

import java.util.List;

public class CustomPage<T> {

    private final List<T> content;
    private final int currentPage;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public CustomPage(List<T> content, int currentPage, int pageSize, long totalElements, int totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}