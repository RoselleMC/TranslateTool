package com.iroselle.TranslateTool.bukkit.util;

import com.iroselle.TranslateTool.bukkit.Config.Config;
import com.iroselle.TranslateTool.bukkit.util.translate.Baidu;
import com.iroselle.TranslateTool.bukkit.util.translate.Google;
import com.iroselle.TranslateTool.bukkit.util.translate.Tencent;
import com.iroselle.TranslateTool.bukkit.util.translate.Youdao;
import org.apache.commons.lang3.StringUtils;

public class TranslateUtils {

    public static int tries = 0;

    public static String colorCodes = "0123456789abcdefklmnor";

    public static String translateAndColor(String string) {
        return translateAndColor("§",string.replace("&","§"));
    }


    public static String translateAndColor(String regex,String string) {
        Debug.msg("传入字符串 "+string);
        if (!string.contains(regex)) {
            Debug.msg("字符串 &a"+string+" &7不存在 &b"+regex+" &7直接翻译!");
            return translate(string);
        }
        String[] args = string.split(regex);
        String[] adds = new String[args.length];

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            String head = StringUtils.substring(arg,0,1);
            if (!colorCodes.contains(head)) {
                adds[i] = "";
                continue;
            }
            String text = StringUtils.substring(arg,1);

            if (text.length() == 0) {
                if (head.length() == 0)
                    adds[i] = "";
                else
                    adds[i] = head;
            } else {
                adds[i] = head + translate(text);
            }

        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String s : adds) {
            stringBuilder.append(regex).append(translate(s));
        }

        return stringBuilder.toString();
    }

    public static String translate(String string) {
        if (string == null || string.equals("")) return "";
        String translate = string;

        String type = Config.yamlConfig.getString("options.type", "Google");
        int interval = Config.yamlConfig.getInt("options.interval", 500);

        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (type.toLowerCase()) {
            case "google":{
                translate = Google.translate(string);
                break;
            }
            case "baidu":{
                if (Config.yamlConfig.getBoolean("source.baidu.enabled")) {
                    translate = Baidu.translate(string,Config.yamlConfig.getString("source.baidu.app_id"),Config.yamlConfig.getString("source.baidu.security_key"));
                } else {
                    translate = Baidu.translate(string);
                }
                break;
            }
            case "tencent":{
                translate = Tencent.translate(string);
                break;
            }
            case "youdao":{
                translate = Youdao.translate(string);
                break;
            }
            default:{
                translate = string;
                break;
            }
        }

        for (String s : Config.jsonConfig.keySet()) {
            if (s.equals("config_version")) continue;
            if (translate == null) continue;

            if (translate.contains(s)) {
                Debug.msg("&3查找词典中的 "+s+" 包含在该字符串中, 进行替换!");
                translate = StringUtils.replace(translate,s,Config.jsonConfig.getString(s));
            }

        }

        Debug.msg("当前正在使用 &e"+type.toUpperCase() +" &7进行翻译");
        Debug.msg("原文 &6"+string);
        Debug.msg("译文 &a"+translate);
        return translate;
    }
}
