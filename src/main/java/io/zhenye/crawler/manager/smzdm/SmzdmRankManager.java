package io.zhenye.crawler.manager.smzdm;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SmzdmRankManager {

    private final RestTemplate restTemplate;

    public Collection<String> getRankList(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException();
        }
        log.info("Request url: {}", url);
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        log.info("Response [code={}]", entity.getStatusCodeValue());

        SmzdmRankVO smzdmRankVO = JSON.parseObject(entity.getBody(), SmzdmRankVO.class);
        List<List<SmzdmRankVO.Item>> lists = Optional.ofNullable(smzdmRankVO)
                .map(SmzdmRankVO::getData)
                .map(SmzdmRankVO.Result::getList)
                .orElse(Collections.emptyList());
        return lists.stream().flatMap(Collection::stream).map(SmzdmRankVO.Item::getArticle_url).collect(Collectors.toSet());
    }

}
