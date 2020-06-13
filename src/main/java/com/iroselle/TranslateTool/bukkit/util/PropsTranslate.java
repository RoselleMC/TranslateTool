package com.iroselle.TranslateTool.bukkit.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropsTranslate {

    public static Properties translateConfig(File file) {
        if (!file.exists()) return null;
        try {
            Properties cache = new Properties();

            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fileInputStream,StandardCharsets.UTF_8);

            properties.load(reader);

            properties.keySet().forEach(o -> {
                Object object = properties.get(o);
                if (object instanceof String) {
                    String string = (String) object;
                    cache.put(o,TranslateUtils.translate(string));
                }

            });
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
