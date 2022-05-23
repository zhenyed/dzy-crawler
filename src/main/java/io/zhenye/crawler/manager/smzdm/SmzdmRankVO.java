package io.zhenye.crawler.manager.smzdm;

import lombok.Data;

import java.util.List;

@Data
public class SmzdmRankVO {

    private String error_code;
    private String error_msg;
    private Result data;

    @Data
    public static class Result {
        private List<List<Item>> list;
    }

    @Data
    public static class Item {
        private String article_url;
    }

}