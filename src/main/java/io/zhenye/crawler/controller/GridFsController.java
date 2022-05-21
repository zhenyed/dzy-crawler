package io.zhenye.crawler.controller;

import io.zhenye.crawler.service.GridFsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/grid")
public class GridFsController {

    private final GridFsService gridFsService;

    @GetMapping("/{module}/{id}")
    public void get(HttpServletResponse response,
                          @PathVariable String module,
                          @PathVariable String id) {
        gridFsService.get(response, module, id);
    }

}
