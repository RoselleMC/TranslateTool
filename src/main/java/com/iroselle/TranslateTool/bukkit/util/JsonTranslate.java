package com.iroselle.TranslateTool.bukkit.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonTranslate {

    public static JSONObject translateConfig(File file) {
        if (!file.exists()) return null;
        JSONObject config = JSONObject.parseObject(FileUtils.ReadFile(file));
        return translateConfig(config);
    }


    public static JSONObject translateConfig(JSONObject jsonObject) {
        Debug.msg("开始一个新的 JSONObject");
        JSONObject cache = new JSONObject();
        for (String s : jsonObject.keySet()) {
            Object object = jsonObject.get(s);
            Debug.msg("正在遍历 &b"+s);
            if (object instanceof JSONObject) {
                Debug.msg("检测到该 Path 为 &eJSONObject");
                cache.put(s,translateConfig((JSONObject) object));
            } else if (object instanceof String) {
                Debug.msg("检测到该 Path 为 &eString");
                cache.put(s,TranslateUtils.translateAndColor((String) object));
            } else if (object instanceof JSONArray) {
                Debug.msg("检测到该 Path 为 &eJSONArray");
                cache.put(s,listHandle((JSONArray) object));
            } else {
                cache.put(s,object);
            }

        }
        return cache;
    }


    public static JSONArray listHandle(JSONArray list) {
        JSONArray cache = new JSONArray();
        for (Object o : list) {
            if (o instanceof JSONObject) {
                Debug.msg("检测到该 &eJSONArray &7数据为 &bJSONObject");
                cache.add(translateConfig((JSONObject) o));
            } else if (o instanceof JSONArray) {
                Debug.msg("检测到该 &eJSONArray &7数据为 &bJSONArray");
                cache.add(listHandle((JSONArray) o));
            } else {
                Debug.msg("检测到该 &eJSONArray &7数据为 &b不支持的类型, 跳过翻译");
                cache.add(o);
            }

        }
        return cache;
    }
}
