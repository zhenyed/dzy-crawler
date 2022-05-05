package io.zhenye.crawler.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsUtils {

    private static final ScriptEngine JS_ENGINE = new ScriptEngineManager().getEngineByName("js");

    public static String eval(String script) {
        try {
            return (String) JS_ENGINE.eval(script);
        } catch (ScriptException e) {
            log.error("Eval javaScript error.", e);
            return null;
        }
    }
}
