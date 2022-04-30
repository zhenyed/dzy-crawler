package io.zhenye.crawler.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.codecraft.webmagic.Spider;

class SmzdmPageProcessorTest {

    @Test
    void main() {
        Spider.create(new SmzdmPageProcessor())
//                .addUrl("https://faxian.smzdm.com/h3s183t0f0c1p1")
                .addUrl("https://www.smzdm.com/p/52369400/")
                .addPipeline((resultItems, task) -> {
                    Assertions.assertEquals(52369400, Integer.parseInt(resultItems.get("id")));
                    Assertions.assertEquals("https://item.jd.com/10044783662893.html", resultItems.get("解析后地址"));
                })
                .run();
    }

}