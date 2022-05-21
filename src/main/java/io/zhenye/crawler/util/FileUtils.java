package io.zhenye.crawler.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static InputStream url2InputStream(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(30 * 1000);  // 连接超时: 30s
        conn.setReadTimeout(1000 * 1000); // IO超时: 1min
        conn.connect();
        return conn.getInputStream();
    }

}
