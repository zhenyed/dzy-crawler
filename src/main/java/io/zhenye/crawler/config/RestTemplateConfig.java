package io.zhenye.crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        ClientHttpRequestFactory httpRequestFactory = new OkHttp3ClientHttpRequestFactory();
        return new RestTemplate(httpRequestFactory);
    }

}
