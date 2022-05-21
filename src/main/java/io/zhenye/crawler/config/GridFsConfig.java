package io.zhenye.crawler.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GridFsConfig {

    @Value("${host}")
    private String host;

}
