package com.iroselle.TranslateTool.bukkit.util.translate;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Google {

    private static final String PATH="https://translate.googleapis.com/translate_a/single";
    private static final String CLIENT="gtx";

    private static final String USER_AGENT="Mozilla/5.0";

    private static final Map<String,String> LANGUAGES = new HashMap<>();

    static {
        LANGUAGES.put("auto","Automatic");
        LANGUAGES.put("zh_cn","Chinese Simplified");
        LANGUAGES.put("en","English");
    }

    public static String translate(String string) {
        String s;
        try {
            s = translateText(string, "en", "zh_cn");
        } catch (Exception e) {
            e.printStackTrace();
            s = null;
        }

        return s;
    }

    public static String translateText(String string, String source, String target) {
        if (string == null) return "";

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("client", CLIENT));
        pairs.add(new BasicNameValuePair("sl", source));
        pairs.add(new BasicNameValuePair("tl", target));
        pairs.add(new BasicNameValuePair("dt", "t"));
        pairs.add(new BasicNameValuePair("q", string));

        String resp = postHttp(pairs);

        JSONArray jsonObject = JSONArray.parseArray(resp);

        return jsonObject.getJSONArray(0).stream().map(o -> (JSONArray) o).map(a -> a.getString(0)).collect(Collectors.joining());
    }


    private static String postHttp(List<NameValuePair> pairs){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Google.PATH);
        httpPost.setHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse response2 = null;
        String string = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
            response2 = httpClient.execute(httpPost);
            HttpEntity entity2 = response2.getEntity();
            string = EntityUtils.toString(entity2);
            EntityUtils.consume(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response2 != null) {
                try {
                    response2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return string == null ? "" : string;
    }

}
