package io.zhenye.crawler.domain.dto;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class SmzdmQueryDTO extends PageDTO {
    /**
     * 页面 id
     */
    @Positive
    private Long pageId;

}
