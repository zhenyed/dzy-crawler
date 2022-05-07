package io.zhenye.crawler.domain;

import io.zhenye.crawler.domain.dto.PageDTO;
import lombok.Data;

import java.util.List;

@Data
public class ListVO<T> {
    /**
     * 最大总数
     */
    private static final long MAX_TOTAL = PageDTO.MAX_PAGE * PageDTO.MAX_PER_PAGE;

    private long total;
    private List<T> rows;

    public ListVO<T> setTotal(long total) {
        this.total = Math.min(total, MAX_TOTAL);
        return this;
    }
}
