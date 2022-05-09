package io.zhenye.crawler.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import io.zhenye.crawler.domain.dto.SmzdmParseDTO;
import io.zhenye.crawler.util.JsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.zhenye.crawler.constant.RegexConstant.*;

@Slf4j
public class SmzdmPageProcessor extends BasePageProcessor {

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(SMZDM_PAGE_REG).match()) {
            processDetail(page);
        } else {
            processList(page);
        }
    }

    private void processList(Page page) {
        List<String> urls = page.getHtml().$("#feed-main-list").links().regex(SMZDM_PAGE_REG).all()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        page.addTargetRequests(urls);
        page.setSkip(true);
    }

    private void processDetail(Page page) {
        Document doc = page.getHtml().getDocument();
        String pageUrl = page.getUrl().get();

        // 购物链接，有些页面没有购物链接 https://www.smzdm.com/p/52484842/
        String buyUrl = Optional.ofNullable(doc.select(".go-buy"))
                .map(CollUtil::getFirst)
                .map(i -> i.attr("href"))
                .map(this::revertLink)
                .orElse(null);

        String updateTime = doc.select(".J_author_info > .time").text();

        String matchCreateTime = ReUtil.getGroup1("更新时间：(\\d{2}-\\d{2} \\d{2}:\\d{2})", updateTime);
        if (matchCreateTime != null) {
            matchCreateTime = LocalDateTime.now().getYear() + "-" + matchCreateTime;
        } else {
            matchCreateTime = ReUtil.getGroup1("更新时间：(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})", updateTime);
        }

        SmzdmParseDTO smzdmParseDTO = new SmzdmParseDTO()
                .setPageId(Long.parseLong(page.getUrl().regex(SMZDM_PAGE_ID_REG, 1).get()))
                .setPageUrl(pageUrl)
                .setTitle(doc.select(".J_title").text())
                .setMallName(doc.select(".canal > .item-subtitle > span").get(0).text())
                .setPrice(doc.select(".price-large").text() + doc.select(".price-desc").text())
                .setCoverUrl(doc.select(".main-img").attr("src"))
                .setWorthy(Integer.parseInt(doc.select("#rating_worthy_num").text()))
                .setUnworthy(Integer.parseInt(doc.select("#rating_unworthy_num").text()))
                .setBuyUrl(buyUrl)
                .setCreateTime(DateUtil.parseLocalDateTime(matchCreateTime, "yyyy-MM-dd HH:mm"))
                ;
        page.putField("dto", smzdmParseDTO);
    }

    private String revertLink(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String html = new HttpClientDownloader().download(url).get();

        int beginIndex = html.indexOf("eval(") + 4;
        int endIndex = html.indexOf("</script>");
        if (beginIndex >= 4 && endIndex > -1 && beginIndex < endIndex) {
            String javaScript = html.substring(beginIndex, endIndex);
            javaScript = JsUtils.eval(javaScript);
            if (StringUtils.isEmpty(javaScript)) {
                return "";
            }
            String newUrl = ReUtil.getGroup1(SMZDM_SKIP_LINK_REG, javaScript);
            if (StringUtils.isEmpty(newUrl)) {
                return "";
            }
            newUrl = newUrl.contains("&to=") ? newUrl.substring(newUrl.indexOf("&to=") + 4) : newUrl;
            return URLUtil.decode(newUrl);
        }
        return "";
    }
}
