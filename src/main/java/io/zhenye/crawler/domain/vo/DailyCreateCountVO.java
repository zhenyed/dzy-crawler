package io.zhenye.crawler.domain.vo;

import io.zhenye.crawler.domain.data.DailyCreateCountBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DailyCreateCountVO {

    private List<String> date = new ArrayList<>();
    private List<Integer> value = new ArrayList<>();

    public DailyCreateCountVO(List<DailyCreateCountBO> list) {
        for (DailyCreateCountBO bo : list) {
            this.date.add(bo.getId());
            this.value.add(bo.getValue());
        }
    }

}
