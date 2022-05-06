package io.zhenye.crawler.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RespStatusEnum {

    SUCCESS("0", "操作成功"),
    FAIL("-1", "操作失败"),
    ;

    /**
     * 响应状态
     */
    private final String status;
    /**
     * 响应编码
     */
    private final String msg;
}
