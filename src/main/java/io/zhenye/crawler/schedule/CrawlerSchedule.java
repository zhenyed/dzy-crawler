package io.zhenye.crawler.schedule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.zhenye.crawler.dao.CrawlApiRepository;
import io.zhenye.crawler.dao.CrawlUrlRepository;
import io.zhenye.crawler.domain.data.CrawlApiDO;
import io.zhenye.crawler.domain.data.CrawlUrlDO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.manager.smzdm.SmzdmRankManager;
import io.zhenye.crawler.service.GridFsService;
import io.zhenye.crawler.webmagic.pipeline.DbPipeLine;
import io.zhenye.crawler.webmagic.processor.SmzdmPageProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Sets;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.zhenye.crawler.constant.StrConstant.SMZDM_PAGE_PREFIX;

@RequiredArgsConstructor
@Component
public class CrawlerSchedule {

    private final DbPipeLine dbPipeLine;
    private final CrawlUrlRepository crawlUrlRepository;
    private final CrawlApiRepository crawlApiRepository;
    private final SmzdmRankManager smzdmRankManager;
    private final RedissonClient redissonClient;
    private final MongoTemplate mongoTemplate;
    private final GridFsService gridFsService;

    @Value("${email.receive}")
    private String receive;
    @Value("${email.htmlPath}")
    private String htmlPath;
    @Value("${email.settingPath}")
    private String settingPath;

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
                spider.addUrl(SMZDM_PAGE_PREFIX + pageId + "/");
            }
        } else {
            RAtomicLong init = redissonClient.getAtomicLong("init");
            long start = init.get();
            long end = start - perSize;
            for (; start > end; start--) {
                spider.addUrl(SMZDM_PAGE_PREFIX + start + "/");
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
                .addUrl(SMZDM_PAGE_PREFIX + param + "/")
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

    /**
     * 发邮件：最近 x 小时的前 y 条爆料
     */
    @XxlJob("smzdmTopEmailSchedule")
    public void smzdmTopEmailSchedule() {
        // 参数
        String param = ObjectUtil.defaultIfEmpty(XxlJobHelper.getJobParam(), "{}");
        JSONObject object = JSON.parseObject(param);
        Integer topHours = object.getInteger("topHour");
        Integer topItem = object.getInteger("topItem");
        Integer moreThanWorthyPercent = object.getInteger("moreThanWorthyPercent");
        Integer moreThanWorthy = object.getInteger("moreThanWorthy");
        if (ObjectUtils.anyNull(topHours, topItem, moreThanWorthyPercent, moreThanWorthy)) {
            XxlJobHelper.handleFail("参数不能出现 null");
            return;
        }

        // 查询
        Query query = Query
                .query(new Criteria()
                        .and("createTime").gte(LocalDateTime.now().minusHours(topHours))
                        .and("worthyPercent").gte(moreThanWorthyPercent)
                        .and("worthy").gte(moreThanWorthy)
                ).with(Sort.by(Sort.Direction.DESC, "worthy", "worthyPercent"))
                .limit(topItem);
        List<SmzdmItemDO> result = mongoTemplate.find(query, SmzdmItemDO.class);

        // 发邮件
        final String templateHtml = new FileReader(htmlPath).readString();
        StringBuilder sb = new StringBuilder();
        for (SmzdmItemDO smzdmItemDO : result) {
            String subContent = templateHtml;
            subContent = subContent.replace("{{coverUrl}}", gridFsService.getUrl(smzdmItemDO.getPageId()));
            subContent = subContent.replace("{{title}}", smzdmItemDO.getTitle());
            subContent = subContent.replace("{{pageUrl}}", smzdmItemDO.getPageUrl());
            subContent = subContent.replace("{{price}}", smzdmItemDO.getPrice());
            subContent = subContent.replace("{{mallName}}", smzdmItemDO.getMallName());
            subContent = subContent.replace("{{worthyPercent}}", String.valueOf(smzdmItemDO.getWorthyPercent()));
            if (StringUtils.isNotEmpty(smzdmItemDO.getBuyUrl())) {
                subContent = subContent.replace("{{buyUrl}}", smzdmItemDO.getBuyUrl());
            }
            subContent = subContent.replace("{{worthy}}", String.valueOf(smzdmItemDO.getWorthy()));
            subContent = subContent.replace("{{unworthy}}", String.valueOf(smzdmItemDO.getUnworthy()));
            sb.append(subContent);
        }
        MailUtil.send(new MailAccount(settingPath), receive, "什么值得买 - 促销", sb.toString(), true);
    }

}
