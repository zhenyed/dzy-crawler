package io.zhenye.crawler.schedule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.zhenye.crawler.dao.CrawlApiRepository;
import io.zhenye.crawler.dao.CrawlUrlRepository;
import io.zhenye.crawler.domain.data.CrawlApiDO;
import io.zhenye.crawler.domain.data.CrawlUrlDO;
import io.zhenye.crawler.manager.smzdm.SmzdmRankManager;
import io.zhenye.crawler.webmagic.pipeline.DbPipeLine;
import io.zhenye.crawler.webmagic.processor.SmzdmPageProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Sets;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CrawlerSchedule {

    private final DbPipeLine dbPipeLine;
    private final CrawlUrlRepository crawlUrlRepository;
    private final CrawlApiRepository crawlApiRepository;
    private final SmzdmRankManager smzdmRankManager;
    private final RedissonClient redissonClient;

    /**
     * 抓取指定列表页
     */
    @XxlJob("smzdmListSchedule")
    public void smzdmListSchedule() {
        List<CrawlUrlDO> crawlUrlSet = crawlUrlRepository.findEnableUrl();
        if (CollUtil.isEmpty(crawlUrlSet)) {
            XxlJobHelper.log("[Schedule] smzdmListSchedule stop. Url is empty");
            return;
        }

        Spider spider = Spider.create(new SmzdmPageProcessor());
        for (CrawlUrlDO urlDO : crawlUrlSet) {
            for (String url : urlDO.getUrl()) {
                spider.addUrl(url);
            }
        }
        spider.addPipeline(dbPipeLine)
                .thread(4)
                .run();
        XxlJobHelper.log("[Schedule] smzdmListSchedule end");
    }

    /**
     * 倒序抓取
     */
    @XxlJob("smzdmRankingInitSchedule")
    public void smzdmRankingInitSchedule() {
        XxlJobHelper.log("[Schedule] smzdmListSchedule start");

        // 参数
        String param = ObjectUtil.defaultIfEmpty(XxlJobHelper.getJobParam(), "{}");
        JSONObject object = JSON.parseObject(param);
        int perSize = ObjectUtils.defaultIfNull(object.getInteger("perSize"), 100);
        JSONArray array = object.getJSONArray("specialPageIds");

        Spider spider = Spider.create(new SmzdmPageProcessor());
        if (CollectionUtils.isNotEmpty(array)) {
            for (Object pageId : array) {
                spider.addUrl("https://www.smzdm.com/p/" + pageId + "/");
            }
        } else {
            RAtomicLong init = redissonClient.getAtomicLong("init");
            long start = init.get();
            long end = start - perSize;
            for (; start > end; start--) {
                spider.addUrl("https://www.smzdm.com/p/" + start + "/");
            }
            init.set(start);
        }
        spider.addPipeline(dbPipeLine)
                .thread(4)
                .run();
        XxlJobHelper.log("[Schedule] smzdmListSchedule end");
    }

    /**
     * 抓取指定页面
     */
    @XxlJob("smzdmSinglePageSchedule")
    public void smzdmSinglePageSchedule() {
        String param = XxlJobHelper.getJobParam();
        if (StringUtils.isEmpty(param)) {
            XxlJobHelper.handleFail();
            return;
        }
        Spider.create(new SmzdmPageProcessor())
                .addUrl("https://www.smzdm.com/p/" + param + "/")
                .addPipeline(dbPipeLine)
                .run();
    }

    /**
     * 抓取排行榜 api
     */
    @XxlJob("smzdmRankListApiSchedule")
    public void smzdmRankListApiSchedule() {
        Set<String> pageUrlSet = Sets.newHashSet();

        List<CrawlApiDO> crawlApiDOList = crawlApiRepository.findApiUrl("smzdm");
        Set<String> rankListUrls = crawlApiDOList.stream().map(CrawlApiDO::getUrl).flatMap(Collection::stream).collect(Collectors.toSet());
        for (String rankListUrl : rankListUrls) {
            pageUrlSet.addAll(smzdmRankManager.getRankList(rankListUrl));
        }

        XxlJobHelper.log("[Schedule] smzdmRankListApiSchedule has {} page", pageUrlSet.size());

        Spider spider = Spider.create(new SmzdmPageProcessor());
        for (String pageUrl : pageUrlSet) {
            spider.addUrl(pageUrl);
        }
        spider.addPipeline(dbPipeLine)
                .run();
    }

}
