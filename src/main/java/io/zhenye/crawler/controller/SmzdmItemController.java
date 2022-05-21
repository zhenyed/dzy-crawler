package io.zhenye.crawler.controller;

import io.zhenye.crawler.domain.data.GroupByBO;
import io.zhenye.crawler.domain.vo.*;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.domain.dto.PageDTO;
import io.zhenye.crawler.domain.dto.SmzdmQueryDTO;
import io.zhenye.crawler.service.SmzdmItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/smzdm-item")
public class SmzdmItemController {

    private final SmzdmItemService smzdmItemService;

    @Value("${host}")
    private String host;

    @GetMapping("/")
    public AdminVO<ListVO<SmzdmItemVO>> list(@Valid SmzdmQueryDTO dto) {
        Page<SmzdmItemDO> page = smzdmItemService.list(dto);
        List<SmzdmItemVO> resp = SmzdmItemVO.ofList(page.toList());
        return AdminVO.success(
                new ListVO<SmzdmItemVO>()
                        .setTotal(page.getTotalElements())
                        .setRows(resp));
    }

    @GetMapping("/{pageId}")
    public AdminVO<SmzdmItemVO> get(@PathVariable("pageId") Long pageId) {
        SmzdmItemDO itemDO = smzdmItemService.get(pageId);
        SmzdmItemVO res = SmzdmItemVO.of(itemDO);
        res.setCoverUrl("http://" + host + "/grid/smzdm/" + res.getPageId());
        return AdminVO.success(res);
    }

    @GetMapping("/ranking")
    public AdminVO<ListVO<SmzdmItemVO>> ranking(@Valid PageDTO dto) {
        Page<SmzdmItemDO> page = smzdmItemService.listRanking(dto);
        List<SmzdmItemVO> resp = SmzdmItemVO.ofList(page.toList());
        return AdminVO.success(
                new ListVO<SmzdmItemVO>()
                        .setTotal(page.getSize())
                        .setRows(resp));
    }

    @GetMapping("/bi/daily-create")
    public AdminVO<DailyCreateCountVO> biDailyCreateCount() {
        List<GroupByBO> list = smzdmItemService.listCreateCountDaily();
        return AdminVO.success(new DailyCreateCountVO(list));
    }

    @GetMapping("/bi/mall-create")
    public AdminVO<Map<String, List<ChartMallVO>>> biMallCount() {
        List<GroupByBO> list = smzdmItemService.listMallCount();
        List<ChartMallVO> res = list.stream().map(ChartMallVO::new).collect(Collectors.toList());
        return AdminVO.success(Collections.singletonMap("mall", res));
    }

}
