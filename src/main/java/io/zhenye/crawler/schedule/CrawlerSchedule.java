package io.zhenye.crawler.schedule;

import cn.hutool.core.collection.CollUtil;
import io.zhenye.crawler.dao.CrawlUrlRepository;
import io.zhenye.crawler.domain.data.CrawlUrlDO;
import io.zhenye.crawler.pipeline.DbPipeLine;
import io.zhenye.crawler.pipeline.RankingPipeLine;
import io.zhenye.crawler.processor.SmzdmPageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrawlerSchedule {

    private final DbPipeLine dbPipeLine;
    private final RankingPipeLine rankingPipeLine;
    private final CrawlUrlRepository crawlUrlRepository;
    private final RedissonClient redissonClient;

    /**
     * 【每小时】12h排行榜
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void smzdmRankingListSchedule() {
        log.info("[Schedule] smzdmRankingListSchedule start");

        List<CrawlUrlDO> crawlUrlSet = crawlUrlRepository.findEnableUrl();
        if (CollUtil.isEmpty(crawlUrlSet)) {
            log.info("[Schedule] smzdmRankingListSchedule stop. Url is empty");
            return;
        }

        Spider spider = Spider.create(new SmzdmPageProcessor());
        for (CrawlUrlDO urlDO : crawlUrlSet) {
            for (String url : urlDO.getUrl()) {
                spider.addUrl(url);
            }
        }
        spider.addPipeline(dbPipeLine)
//                .addPipeline(rankingPipeLine)
                .thread(4)
                .run();
        log.info("[Schedule] smzdmRankingListSchedule end");
    }

    @Scheduled(cron = "0 30 * * * ?")
    public void smzdmRankingInitSchedule() {
        log.info("[Schedule] smzdmRankingInitSchedule start");

        Spider spider = Spider.create(new SmzdmPageProcessor());
        RAtomicLong init = redissonClient.getAtomicLong("init");
        long start = init.get();
        long end = start - 100;
        for (; start > end; start--) {
            spider.addUrl("https://www.smzdm.com/p/" + start + "/");
        }
        spider.addPipeline(dbPipeLine)
                .thread(4)
                .run();
        init.set(start);
        log.info("[Schedule] smzdmRankingInitSchedule end");
    }

}
