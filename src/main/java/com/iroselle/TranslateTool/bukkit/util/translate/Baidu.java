package com.iroselle.TranslateTool.bukkit.util.translate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.translate.demo.TransApi;
import com.iroselle.TranslateTool.bukkit.util.Debug;
import com.iroselle.TranslateTool.bukkit.util.TranslateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class Baidu {

    private final static String PreUrl="http://www.baidu.com/s?wd=";
    private final static String TransResultStartFlag="<span class=\"op_dict_text2\">";
    private final static String TransResultEndFlag="</span>";

    public static String translate(String string) {
        try {
            URL url = new URL(PreUrl+string);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String preLine="";
            String line;
            int flag=1;
            StringBuilder content= new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if(preLine.contains(TransResultStartFlag) && !line.contains(TransResultEndFlag)){
                    content.append(line.replaceAll("[　 ]", "")).append("\n");
                    flag=0;
                }
                if(line.contains(TransResultEndFlag)){
                    flag=1;
                }
                if(flag==1){
                    preLine=line;
                }
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String translate(String string, String APP_ID, String SECURITY_KEY) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        JSONObject jsonObject = JSONObject.parseObject(api.getTransResult(string, "en", "zh"));
        JSONArray array = jsonObject.getJSONArray("trans_result");

        if (TranslateUtils.tries > 4) {
            Debug.msg("&c尝试了 4 次都没有成功, 跳过翻译!");
            return string;
        }

        if (array == null) {
            TranslateUtils.tries++;
            translate(string, APP_ID, SECURITY_KEY);
        }

        for (int i = 0; i < array.size(); i++) {
            JSONObject params = JSONObject.parseObject(array.getString(i));
            String str1 = params.getString("dst");
            try {
                str1 = URLDecoder.decode(str1, "utf-8");
                return str1;
            } catch (Exception ignored) {
                ignored.printStackTrace();
                return string;
            }
        }
        return string;
    }

}
