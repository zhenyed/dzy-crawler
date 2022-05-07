package io.zhenye.crawler.processor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.Set;

@Slf4j
class SmzdmPageProcessorTest {

    @BeforeAll
    static void beforeAll() {
        // 设置日志输出级别
        Set<String> loggers = CollUtil.newHashSet("org.apache.http");
        for (String log : loggers) {
            Logger logger = (Logger) LoggerFactory.getLogger(log);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }
    }

    @Disabled
    @Test
    void testLocal() {
        Spider.create(new SmzdmPageProcessor())
                .test("https://www.smzdm.com/p/52475470/");
//                .addUrl("https://faxian.smzdm.com/h3s183t0f0c1p1")
//                .addUrl("https://search.smzdm.com/?c=home&s=%E8%82%AF%E5%BE%B7%E5%9F%BA&v=b")
    }

}