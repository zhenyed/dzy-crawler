package io.zhenye.crawler.domain.dto;

import lombok.Data;

@Data
public class PageDTO {

    /**
     * 最大页数
     */
    public static final int MAX_PAGE = 20;
    /**
     * 每页最大数量
     */
    public static final int MAX_PER_PAGE = 20;

    private int page = 1;
    private int perPage = 5;

    public void setPage(int page) {
        this.page = Math.min(page, MAX_PAGE);
    }

    public void setPerPage(int perPage) {
        this.perPage = Math.min(perPage, MAX_PER_PAGE);
    }
}
