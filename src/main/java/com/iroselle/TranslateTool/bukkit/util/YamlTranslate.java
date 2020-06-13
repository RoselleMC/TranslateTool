package com.iroselle.TranslateTool.bukkit.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.iroselle.TranslateTool.bukkit.Config.Config;
import com.iroselle.TranslateTool.bukkit.util.translate.Baidu;
import com.iroselle.TranslateTool.bukkit.util.translate.Google;
import com.iroselle.TranslateTool.bukkit.util.translate.Tencent;
import com.iroselle.TranslateTool.bukkit.util.translate.Youdao;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlTranslate {

    public static YamlLoader translateConfig(File file) {
        YamlLoader config = YamlLoader.loadConfiguration(file);
        return translateConfig(config);
    }

    public static YamlLoader translateConfig(ConfigurationSection section) {
        Debug.msg("开始一个新的 Section: &b"+section.getCurrentPath());
        YamlLoader cache = new YamlLoader();
        for (String s : section.getKeys(false)) {
            Debug.msg("正在遍历 &a"+section.getCurrentPath()+" &7中的 &b"+s);
            if (section.isConfigurationSection(s)) {
                Debug.msg("检测到该 Path 为 &eConfigurationSection");
                cache.set(s,translateConfig(section.getConfigurationSection(s)));
            } else if (section.isString(s)) {
                Debug.msg("检测到该 Path 为 &eString");
                cache.set(s,TranslateUtils.translateAndColor(section.getString(s)));
            } else if (section.isList(s)) {
                Debug.msg("检测到该 Path 为 &eList");
                cache.set(s,listHandle(section.getList(s)));
            } else {
                cache.set(s,section.get(s));
            }

        }
        return cache;
    }

    public static List<Object> listHandle(List<?> list) {
        List<Object> cache = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof ConfigurationSection) {
                Debug.msg("检测到该 &eList &7数据为 &bConfigurationSection");
                cache.add(translateConfig((ConfigurationSection) o));
            } else if (o instanceof String) {
                Debug.msg("检测到该 &eList &7数据为 &bString");
                String s = o.toString();
                cache.add(TranslateUtils.translateAndColor(s));
            } else if (o instanceof List) {
                Debug.msg("检测到该 &eList &7数据为 &bList");
                cache.add(listHandle((List<?>) o));
            } else {
                Debug.msg("检测到该 &eList &7数据为 &b不支持翻译的类型");
                cache.add(o);
            }

        }
        return cache;
    }

    
}
