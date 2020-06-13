package com.iroselle.TranslateTool.bukkit.util.translate;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.iroselle.TranslateTool.bukkit.util.UrlUtils;

public class Youdao {

    public static String translate(String string) {
        String s = UrlUtils.readUrlString("http://fanyi.youdao.com/translate?&doctype=json&type=EN2ZH_CN&i="+string);

        if (s == null || s.equals("")) {
            return null;
        }

        JSONObject json;
        try {
            json = JSONObject.parseObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (json == null) {
            return null;
        }

        if (json.getString("options.type") == null) {
            return null;
        }

        String s1 = json.getJSONArray("translateResult").getJSONArray(0).getJSONObject(0).getString("tgt");

        return s1;
    }

}
