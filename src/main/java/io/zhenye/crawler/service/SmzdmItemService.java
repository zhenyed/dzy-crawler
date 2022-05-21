package io.zhenye.crawler.service;

import io.zhenye.crawler.domain.data.GroupByBO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.domain.dto.PageDTO;
import io.zhenye.crawler.domain.dto.SmzdmQueryDTO;
import io.zhenye.crawler.dao.SmzdmItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmzdmItemService {

    private final SmzdmItemRepository smzdmItemRepository;
    private final MongoTemplate mongoTemplate;

    public Page<SmzdmItemDO> list(SmzdmQueryDTO dto) {
        if (StringUtils.isNotEmpty(dto.getTitle())) {
            Query query = Query
                    .query(Criteria.where("title").regex(dto.getTitle()))
                    .with(Sort.by(Sort.Direction.DESC, "createTime"))
                    .limit(dto.getPerPage())
                    .skip((long) (dto.getPage()) * dto.getPerPage());
            return new PageImpl<>(mongoTemplate.find(query, SmzdmItemDO.class));
        }
        PageRequest pageRequest = PageRequest.of(dto.getPage() + 1, dto.getPerPage(), Sort.Direction.DESC, "createTime");
        return smzdmItemRepository.findAll(pageRequest);
    }

    public SmzdmItemDO get(Long pageId) {
        return smzdmItemRepository.findByPageId(pageId);
    }

    public Page<SmzdmItemDO> listRanking(PageDTO dto) {
        Query query = Query
                .query(new Criteria()
                        .and("createTime").gte(LocalDateTime.now().minusDays(1))
                        .and("worthyPercent").gte(70)
                        .and("worthy").gte(20)
                ).with(Sort.by(Sort.Direction.DESC, "worthy", "worthyPercent"))
                .limit(dto.getPerPage())
                .skip((long) (dto.getPage() - 1) * dto.getPerPage());

        long total = mongoTemplate.count(query, SmzdmItemDO.class);
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList());
        }
        List<SmzdmItemDO> result = mongoTemplate.find(query, SmzdmItemDO.class);
        return new PageImpl<>(result, Pageable.unpaged(), total);
    }

    public List<GroupByBO> listCreateCountDaily() {
        LocalDate localDate = LocalDate.now().minusDays(6);
        return smzdmItemRepository.biByCreateCountDaily(localDate);
    }

    public List<GroupByBO> listMallCount() {
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return smzdmItemRepository.countGroupByMall(startDate, endDate);
    }
}
