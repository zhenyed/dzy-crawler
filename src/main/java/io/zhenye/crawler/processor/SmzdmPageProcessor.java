package io.zhenye.crawler.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import io.zhenye.crawler.util.JsUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

import static io.zhenye.crawler.constant.RegexConstant.*;

public class SmzdmPageProcessor extends BasePageProcessor {

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(SMZDM_ITEM_REG).match()) {
            processDetail(page);
        } else {
            processList(page);
        }
    }

    /**
     * 处理列表页
     */
    private void processList(Page page) {
        List<String> urlList = page.getHtml().xpath("/html/body/div/div/ul/*/div/h5/a/@href").all();
        // 添加详情页url
        if (CollUtil.isNotEmpty(urlList)) {
            page.addTargetRequests(urlList);
        }
        // 列表页不用后续处理
        page.setSkip(true);
    }

    /**
     * 处理详情页
     */
    private void processDetail(Page page) {
        try {
            Document doc = page.getHtml().getDocument();
            // 是否重复爬取
            long sourceDetailId = Long.parseLong(page.getUrl().regex(SMZDM_ITEM_REG, 1).get());

            // 分类映射Map
            Elements categoryEle = doc.select("div.crumbs").select("span");
            if (categoryEle.isEmpty()) {
                page.setSkip(true);
                return;
            }

            // 电商名称
            String mallName = doc.select(".canal > .item-subtitle > span").text();
            if (StringUtils.isEmpty(mallName)) {
                page.setSkip(true);
                return;
            }

            // 优惠券
            Elements couponEle = doc.select(".preferential > .item-subtitle > a");
            if (StringUtils.isNotEmpty(couponEle.text())) {
                // 转换优惠券链接
                page.putField("couponUrl", this.revertLink(couponEle.attr("href")));
                // 提取优惠券金额
                page.putField("couponPrice", ReUtil.getGroup1(SMZDM_COUPON_PRICE_REG, couponEle.text()));
            }

            // 标题
            String title = doc.select(".J_title").text();
            if (StringUtils.isEmpty(title)) {
                page.setSkip(true);
                return;
            }

            // 封面
            String cover = doc.select(".main-img").attr("src");
            if (StringUtils.isEmpty(cover)) {
                page.setSkip(true);
                return;
            }
            // 返利链接，后面会利用javascript的API实现链接中转功能
            String source = doc.select(".go-buy").get(0).attr("href");
            if (StringUtils.isEmpty(source)) {
                page.setSkip(true);
                return;
            }

            // 价格 + 价格描述
            String price = doc.select(".price-large").text() + doc.select(".price-desc").text();
            if (StringUtils.isEmpty(price)) {
                page.setSkip(true);
                return;
            }

            int worthy = Integer.parseInt(doc.select("#rating_worthy_num").text());
            int unworthy = Integer.parseInt(doc.select("#rating_unworthy_num").text());

            page.putField("源地址", source);
            page.putField("解析后地址", revertLink(source));
            page.putField("标题", title.trim());
            page.putField("电商", mallName);
            page.putField("价格", price);
            page.putField("值", worthy);
            page.putField("不值", unworthy);
            page.putField("图片url", cover);
            page.putField("id", sourceDetailId);
            page.putField("详情url", page.getUrl().get());
        } catch (Exception e) {
            page.setSkip(true);
        }
    }

    /**
     * 获取原始的电商链接，什么值得买中转页面是一段加密js，浏览器执行后跳转到电商终端页
     * 在java中使用js引擎执行js代码可以得到电商链接
     */
    private String revertLink(String url) {
        Html html = new HttpClientDownloader().download(url);
        String transferPageCode = html.get();

        // 获取js代码
        String javaScript = "";

        int beginIndex = transferPageCode.indexOf("eval(") + 4;
        int endIndex = transferPageCode.indexOf("</script>");
        if (beginIndex >= 4 && endIndex > -1 && beginIndex < endIndex) {
            javaScript = transferPageCode.substring(beginIndex, endIndex);
        }

        javaScript = JsUtils.eval(javaScript);
        if (StringUtils.isEmpty(javaScript)) {
            return Strings.EMPTY;
        }
        String newUrl = ReUtil.getGroup1(SMZDM_SKIP_LINK_REG, javaScript);
        newUrl = newUrl.contains("&to=") ? newUrl.substring(newUrl.indexOf("&to=") + 4) : newUrl;
        return URLUtil.decode(newUrl);
    }

}
