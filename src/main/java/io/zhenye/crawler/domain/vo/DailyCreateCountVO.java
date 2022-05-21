package io.zhenye.crawler.domain.vo;

import io.zhenye.crawler.domain.data.GroupByBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DailyCreateCountVO {

    private List<String> date = new ArrayList<>();
    private List<Integer> value = new ArrayList<>();

    public DailyCreateCountVO(List<GroupByBO> list) {
        for (GroupByBO bo : list) {
            this.date.add(bo.getId());
            this.value.add(bo.getValue());
        }
    }

}
