package io.zhenye.crawler.service;

import io.zhenye.crawler.config.GridFsConfig;
import io.zhenye.crawler.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsConfig gridFsConfig;

    /**
     * 上传 smzdm 照片
     */
    public void uploadSmzdmPic(String url, Long pageId) {
        if (StringUtils.isEmpty(url) || pageId == null) {
            return;
        }
        upload(url, "smzdm", String.valueOf(pageId));
    }

    /**
     * 上传照片
     */
    public void upload(String url, String module, String id) {
        if (StringUtils.isAnyEmpty(url, module, id)) {
            log.error("[GridFS] upload some param is empty.[url={}, module={}, id={}]", url, module, id);
            return;
        }
        String name = getFileName(module, id);
        if (!this.exists(name)) {
            try (InputStream inputStream = FileUtils.url2InputStream(url)) {
                gridFsTemplate.store(inputStream, name);
                log.info("[GridFS] upload success.[url={}, module={}, id={}]", url, module, id);
            } catch (Exception e) {
                log.error("[GridFS] upload error", e);
            }
        } else {
            log.info("[GridFS] upload exist.[url={}, module={}, id={}]", url, module, id);
        }
    }

    /**
     * 获取图片
     */
    public void get(HttpServletResponse response, String module, String id) {
        String fileName = getFileName(module, id);
        try (OutputStream out = response.getOutputStream()) {
            GridFsResource resource = this.getResource(fileName);
            if (resource.exists()) {
                IOUtils.copy(resource.getInputStream(), out);
            }
        } catch (IOException e) {
            log.error("[GridFS] get error", e);
        }
    }

    /**
     * 获取 url
     */
    public String getUrl(Long pageId) {
        return  "http://" + gridFsConfig.getHost() + "/grid/smzdm/" + pageId;
    }

    /**
     * 删除 n 天之前的 GridFs
     */
    public void deleteFromAFewDaysAgo(int day, int limit) {
        Query query = Query
                .query(new Criteria()
                        .and("uploadDate").lte(LocalDateTime.now().minusDays(day))
                )
                .limit(limit);
        gridFsTemplate.delete(query);
    }

    /**
     * 获取 GridFs 资源
     */
    private GridFsResource getResource(String name) {
        return gridFsTemplate.getResource(name);
    }

    /**
     * 图片名是否存在
     */
    private boolean exists(String fileName) {
        return this.getResource(fileName).exists();
    }

    /**
     * 获取 gridFS filename
     */
    private static String getFileName(String module, String id) {
        return String.join("-", module, id);
    }

}
