package io.zhenye.crawler.controller;

import io.zhenye.crawler.domain.data.DailyCreateCountBO;
import io.zhenye.crawler.domain.vo.AdminVO;
import io.zhenye.crawler.domain.vo.ListVO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.domain.dto.PageDTO;
import io.zhenye.crawler.domain.dto.SmzdmQueryDTO;
import io.zhenye.crawler.domain.vo.DailyCreateCountVO;
import io.zhenye.crawler.domain.vo.SmzdmItemVO;
import io.zhenye.crawler.service.SmzdmItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/smzdm-item")
public class SmzdmItemController {

    private final SmzdmItemService smzdmItemService;

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
        List<DailyCreateCountBO> list = smzdmItemService.listCreateCountDaily();
        return AdminVO.success(new DailyCreateCountVO(list));
    }

}
