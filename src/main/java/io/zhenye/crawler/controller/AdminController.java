package io.zhenye.crawler.controller;

import io.zhenye.crawler.domain.AdminVO;
import io.zhenye.crawler.domain.ListVO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.domain.dto.PageDTO;
import io.zhenye.crawler.domain.dto.SmzdmQueryDTO;
import io.zhenye.crawler.domain.vo.SmzdmItemVO;
import io.zhenye.crawler.service.SmzdmItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SmzdmItemService smzdmItemService;

    @GetMapping("/smzdm-item")
    public AdminVO<ListVO<SmzdmItemVO>> list(@Valid SmzdmQueryDTO dto) {
        Page<SmzdmItemDO> page = smzdmItemService.list(dto);
        List<SmzdmItemVO> resp = page.stream()
                .map(i -> {
                    SmzdmItemVO res = new SmzdmItemVO();
                    BeanUtils.copyProperties(i, res);
                    return res;
                })
                .collect(Collectors.toList());
        return AdminVO.success(
                new ListVO<SmzdmItemVO>()
                        .setTotal(page.getTotalElements())
                        .setRows(resp));
    }

    @GetMapping("/smzdm-item/{pageId}")
    public AdminVO<SmzdmItemVO> get(@PathVariable("pageId") Long pageId) {
        SmzdmItemDO itemDO = smzdmItemService.get(pageId);
        SmzdmItemVO res = new SmzdmItemVO();
        BeanUtils.copyProperties(itemDO, res);
        return AdminVO.success(res);
    }

    @GetMapping("/smzdm-item/ranking")
    public AdminVO<ListVO<SmzdmItemVO>> ranking(@Valid PageDTO dto) {
        Page<SmzdmItemDO> page = smzdmItemService.listRanking(dto);
        List<SmzdmItemVO> resp = page.stream()
                .map(i -> {
                    SmzdmItemVO res = new SmzdmItemVO();
                    BeanUtils.copyProperties(i, res);
                    return res;
                })
                .collect(Collectors.toList());
        return AdminVO.success(
                new ListVO<SmzdmItemVO>()
                        .setTotal(page.getTotalElements())
                        .setRows(resp));
    }

}
