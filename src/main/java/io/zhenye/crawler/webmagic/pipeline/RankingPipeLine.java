package io.zhenye.crawler.webmagic.pipeline;

import io.zhenye.crawler.constant.SmzdmConstant;
import io.zhenye.crawler.constant.SmzdmProperties;
import io.zhenye.crawler.domain.dto.SmzdmParseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Slf4j
@RequiredArgsConstructor
@Component
public class RankingPipeLine implements Pipeline {

    private final RedissonClient redissonClient;
    private final SmzdmProperties smzdmProperties;

    @Override
    public void process(ResultItems resultItems, Task task) {
        SmzdmParseDTO dto = resultItems.get("dto");

        // 直接添加（已存在的 obj 会更新 score）
        RScoredSortedSet<Long> scoredSortedSet = redissonClient.getScoredSortedSet(SmzdmConstant.SMZDM_RANKING);
        double score = dto.getWorthy() * 100.0d + dto.getWorthyPercent();
        scoredSortedSet.add(-score, dto.getPageId());

        // 超过指定数量的移除
        int moreThan = scoredSortedSet.size() - smzdmProperties.getRankingSize();
        if (moreThan > 0) {
            scoredSortedSet.pollLast(moreThan);
        }
    }

}
