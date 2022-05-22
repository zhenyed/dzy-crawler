package io.zhenye.crawler.webmagic.processor;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public abstract class BasePageProcessor implements PageProcessor {

    public static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36";

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(2)
                .setSleepTime(1_000)
                .setTimeOut(30_000)
                .setUserAgent(UA);
    }

}
