package io.zhenye.crawler.service;

import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.domain.dto.SmzdmQueryDTO;
import io.zhenye.crawler.dao.SmzdmItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmzdmItemService {

    private final SmzdmItemRepository smzdmItemRepository;

    public Page<SmzdmItemDO> list(SmzdmQueryDTO dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getPerPage(), Sort.Direction.DESC, "createTime");
        if (dto.getPageId() != null) {
            SmzdmItemDO item = smzdmItemRepository.findByPageId(dto.getPageId());
            if (item == null) {
                return new PageImpl<>(Collections.emptyList());
            } else {
                return new PageImpl<>(Collections.singletonList(item));
            }
//            return smzdmItemRepository.findByPageId(dto.getPageId(), pageRequest);
        }
        return smzdmItemRepository.findAll(pageRequest);
    }

    public SmzdmItemDO get(Long pageId) {
        return smzdmItemRepository.findByPageId(pageId);
    }
}
