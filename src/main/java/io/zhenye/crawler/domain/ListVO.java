package io.zhenye.crawler.domain;

import lombok.Data;

import java.util.List;

@Data
public class ListVO<T> {

    private long total;
    private List<T> rows;

}
