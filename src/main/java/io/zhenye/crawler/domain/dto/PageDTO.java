package io.zhenye.crawler.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PageDTO {

    @Max(50)
    @Min(1)
    private int page = 0;

    @Max(50)
    @Min(1)
    private int perPage = 10;

}
