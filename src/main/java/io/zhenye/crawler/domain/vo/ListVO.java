package io.zhenye.crawler.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ListVO<T> {

    private long total;
    private List<T> rows;

    public ListVO<T> setTotal(long total) {
        this.total = Math.min(total, 10000);
        return this;
    }
}
