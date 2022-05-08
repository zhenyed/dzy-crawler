package io.zhenye.crawler.constant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmzdmProperties {

    @Value("${smzdm.ranking.size}")
    private int rankingSize;

}
