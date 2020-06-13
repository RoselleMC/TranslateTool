package com.iroselle.TranslateTool.bukkit.Config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.iroselle.TranslateTool.bukkit.TranslateTool;
import com.iroselle.TranslateTool.bukkit.util.FileUtils;
import com.iroselle.TranslateTool.bukkit.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class  Config {

    public String OPTIONS_TYPE = "Google";
    public int OPTIONS_INTERVAL = 2000;
    public boolean OPTIONS_USE_DICTIONARY = true;
    public boolean OPTIONS_USE_FAST_TRANSLATE = true;
    public int CONFIG_VERSION = 1;

    public static File jsonDictionaryFile = new File(TranslateTool.instance.getDataFolder(),"dictionary.json");
    public static File yamlConfigFile = new File(TranslateTool.instance.getDataFolder(),"config.yml");

    public static JSONObject jsonConfig;
    public static YamlLoader yamlConfig;

    public static void loadConfig() {
        if (!TranslateTool.instance.getDataFolder().exists()) {
            TranslateTool.instance.getDataFolder().mkdirs();
        }

        if (!yamlConfigFile.exists()) {
            TranslateTool.instance.saveResource("config.yml",true);
        }

        if (!jsonDictionaryFile.exists()) {
            TranslateTool.instance.saveResource("dictionary.json",true);
        }

        yamlConfig = YamlLoader.loadConfiguration(yamlConfigFile);

        jsonConfig = JSONObject.parseObject(FileUtils.ReadFile(jsonDictionaryFile));

        JSONObject jsonObject = JSONObject.parseObject(new BufferedReader(
                new InputStreamReader(TranslateTool.instance.getResource("dictionary.json")))
                .lines().collect(Collectors.joining(System.lineSeparator())));

        if (!jsonObject.getInteger("config_version").equals(jsonConfig.getInteger("config_version"))) {
            for (String s : jsonObject.keySet()) {
                if (s.equals("config_version")) continue;

                if (!jsonConfig.containsKey(s)) {
                    jsonConfig.put(s,jsonObject.getString(s));
                }

            }

            FileUtils.writeText(
                    jsonDictionaryFile,JSON.toJSONString(jsonConfig, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat),false);
        }

        /*if (migrate()) {
            try {
                yamlConfig.save(yamlConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

    }

    private static boolean migrate() {
        InputStream inputStream = TranslateTool.instance.getResource("config,yml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        YamlConfiguration yaml = YamlLoader.loadConfiguration(bufferedReader);
        if (!yamlConfig.contains("config-version")) {
            yaml.set("options.type", yamlConfig.getString("type"));
            yaml.set("options.interval", yamlConfig.getString("interval"));
            yaml.set("options.source", yamlConfig.getConfigurationSection("options.source"));
            return true;
        }
        return false;
    }

}
