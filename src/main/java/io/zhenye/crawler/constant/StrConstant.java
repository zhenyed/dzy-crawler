package io.zhenye.crawler.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrConstant {
    public static final String SMZDM_PAGE_PREFIX = "https://www.smzdm.com/p/";
    /**
     * “什么值得买” 爆料路径
     */
    public static final String SMZDM_PAGE_REG = "https://www.smzdm.com/p/\\d+/";
    public static final String SMZDM_PAGE_ID_REG = "https://www.smzdm.com/p/(\\d+)/";
    /**
     * “什么值得买” 跳转解析路径
     */
    public static final Pattern SMZDM_SKIP_LINK_REG = Pattern.compile("smzdmhref='(https://[^']+)'");

}
