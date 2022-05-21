package io.zhenye.crawler.domain.vo;

import io.zhenye.crawler.domain.data.GroupByBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartMallVO {

    private String name;
    private Integer value;

    public ChartMallVO(GroupByBO bo) {
        this.name = bo.getId();
        this.value = bo.getValue();
    }

}
