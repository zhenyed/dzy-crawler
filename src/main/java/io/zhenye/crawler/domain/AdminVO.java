package io.zhenye.crawler.domain;

import io.zhenye.crawler.constant.RespStatusEnum;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Data
public class AdminVO<T> {

    private String status;
    private String msg;
    private T data;

    public static AdminVO<Void> success() {
        return success(RespStatusEnum.SUCCESS, null, null);
    }

    public static <T> AdminVO<T> success(T data) {
        return success(RespStatusEnum.SUCCESS, null, data);
    }

    public static <T> AdminVO<T> success(RespStatusEnum status, String msg, T data) {
        return new AdminVO<T>()
                .setStatus(status.getStatus())
                .setMsg(StringUtils.isEmpty(msg) ? status.getMsg() : msg)
                .setData(data);
    }

}
