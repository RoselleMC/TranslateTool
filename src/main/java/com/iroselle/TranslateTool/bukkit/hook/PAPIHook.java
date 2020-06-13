package com.iroselle.TranslateTool.bukkit.hook;

import com.iroselle.TranslateTool.bukkit.TranslateTool;
import com.iroselle.TranslateTool.bukkit.util.TranslateUtils;
import com.iroselle.TranslateTool.bukkit.util.YamlTranslate;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PAPIHook extends PlaceholderExpansion {

    public static Map<String, String> translateMap = new HashMap<>();
    public static Map<String, String> placeholderMap = new HashMap<>();

    @Override
    public String getIdentifier() {
        return "translate";
    }

    @Override
    public String getAuthor() {
        return "roselle";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params == null) return "";
        String type = StringUtils.substring(params,0,params.indexOf("_"));
        String text = StringUtils.substring(params,params.indexOf("_"));

        switch (type) {
            case "translate":{
                if (translateMap.containsKey(text)) {
                    return translateMap.get(text);
                }
                if (translateMap.get(text).equals("getting")) {
                    return "获取中...";
                }

                Bukkit.getScheduler().runTaskAsynchronously(TranslateTool.instance, () -> {
                    translateMap.put(text,"getting");
                    String translate = TranslateUtils.translate(text);
                    if (translate.equals(text)) {
                        return;
                    }
                    translateMap.put(text,translate);
                });
                break;
            }
            case "placeholder":{
                String string = PlaceholderAPI.setPlaceholders(player, text);
                if (placeholderMap.containsKey(string)) {
                    return placeholderMap.get(string);
                }
                if (placeholderMap.get(string).equals("getting")) {
                    return "获取中...";
                }

                Bukkit.getScheduler().runTaskAsynchronously(TranslateTool.instance, () -> {
                    placeholderMap.put(string,"getting");
                    String translate = TranslateUtils.translate(string);
                    if (translate.equals(string)) {
                        return;
                    }
                    placeholderMap.put(string,translate);
                });
                break;
            }
        }

        return "获取中...";
    }
}
