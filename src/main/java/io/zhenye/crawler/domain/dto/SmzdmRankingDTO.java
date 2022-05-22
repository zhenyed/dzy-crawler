package io.zhenye.crawler.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SmzdmRankingDTO extends PageDTO {

    @Max(7 * 24)
    @Min(1)
    @NotNull
    private Integer hourBefore;
    @Max(100)
    @Min(1)
    @NotNull
    private Integer worth;
    @Max(100)
    @Min(1)
    @NotNull
    private Integer worthyPercent;

}
