package io.zhenye.crawler.processor;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public abstract class BasePageProcessor implements PageProcessor {

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(2)
                .setSleepTime(5_000)
                .setTimeOut(30_000);
    }

}
