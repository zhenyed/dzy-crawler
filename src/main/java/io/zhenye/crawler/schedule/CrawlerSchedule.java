package io.zhenye.crawler.schedule;

import io.zhenye.crawler.pipeline.DbPipeLine;
import io.zhenye.crawler.processor.SmzdmPageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrawlerSchedule {

    private final DbPipeLine dbPipeLine;

    /**
     * 【每小时】12h排行榜
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void smzdmRankingListSchedule() {
        log.info("[Schedule] smzdmRankingListSchedule start");
        Spider.create(new SmzdmPageProcessor())
                .addUrl("https://faxian.smzdm.com/h3s0t0f0c0p1/")
                .addPipeline(dbPipeLine)
                .thread(4)
                .run();
        log.info("[Schedule] smzdmRankingListSchedule end");
    }

}
