package io.zhenye.crawler.domain.dto;

import lombok.Data;

@Data
public class PageDTO {

    private int page = 1;
    private int perPage = 5;

}
