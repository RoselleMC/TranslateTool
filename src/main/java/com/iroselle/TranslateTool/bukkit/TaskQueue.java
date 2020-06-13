package com.iroselle.TranslateTool.bukkit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iroselle.TranslateTool.bukkit.util.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskQueue {

    public static List<TaskQueue> taskQueues = new ArrayList<>();

    private final String name;
    private final BukkitTask task;
    private YamlLoader yamlConfig;
    private JSONObject jsonConfig;
    private Properties propertiesConfig;
    private final File file;

    public TaskQueue(String name, File file, CommandSender sender) {
        this.name = name;
        this.file = file;
        this.task = Bukkit.getScheduler().runTaskAsynchronously(TranslateTool.instance, () -> {
            if (!file.exists()) {
                sender.sendMessage("§c翻译 " + name + " 失败! 文件不存在!");
                return;
            }

            String suffix = StringUtils.substring(file.getName(), file.getName().lastIndexOf(".") + 1,file.getName().length() - 1);
            sender.sendMessage("§a开始对 "+name+" 进行异步翻译");
            if (suffix.equals("yml") || suffix.equals("yaml")) {
                sender.sendMessage("§b该文件为 §eYAML §b格式文件");
                yamlConfig = YamlTranslate.translateConfig(file);
                if (this.saveYamlFile()) {
                    sender.sendMessage("§a成功翻译 " + name + "!");
                    this.remove();
                } else {
                    sender.sendMessage("§c翻译 " + name + " 失败! 文件不存在或无法读取!");
                }
            } else if (suffix.equals("json")) {
                sender.sendMessage("§b该文件为 §cJSON §b格式文件");
                jsonConfig = JsonTranslate.translateConfig(file);
                if (this.saveJsonFile()) {
                    sender.sendMessage("§a成功翻译 " + name + "!");
                    this.remove();
                } else {
                    sender.sendMessage("§c翻译 " + name + " 失败! 文件不存在或无法读取!");
                }
            } else if (suffix.equals("properties")) {
                sender.sendMessage("§b该文件为 §bPROPERTIES §b格式文件");
                propertiesConfig = PropsTranslate.translateConfig(file);
                if (this.savePropertiesFile()) {
                    sender.sendMessage("§a成功翻译 " + name + "!");
                    this.remove();
                } else {
                    sender.sendMessage("§c翻译 " + name + " 失败! 文件不存在或无法读取!");
                }

            } else {
                sender.sendMessage("§c暂不支持 §7"+suffix+" §c类型文件的翻译");
            }

        });

    }

    public void remove() {
        taskQueues.remove(this);
    }

    public boolean saveYamlFile() {
        try {
            yamlConfig.save(new File(name+"-translatetool.yml"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
    public boolean saveJsonFile() {
        FileUtils.writeText(new File(name+"-translatetool.json"),
                JSON.toJSONString(jsonConfig, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat),
                false);
        return true;
    }

    public boolean savePropertiesFile() {
        try {
            File copyFile = new File(file.getName()+"-translatetool.properties");
            if (!FileUtil.copy(file,copyFile)) {
                return false;
            }
            List<String> lisLines = new LinkedList<>();
            // 找到符合属性=属性值的行
            final String strPattern = "(^\\s*([^#\\s=]+)\\s*=\\s*)([^#\\s]*)(.*$)";
            final Pattern pattern = Pattern.compile(strPattern);
            // 逐行读取文件
            try(BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(copyFile), StandardCharsets.UTF_8))) {
                String line;
                while((line = bf.readLine())!=null) {
                    Matcher m = pattern.matcher(line);
                    // 将读取出的文件，如果key正好是在proUpdated里面，则更新value的值
                    if(m.find()) {
                        String key = m.group(2);
                        String valueUpdated = propertiesConfig.getProperty(key);
                        if(valueUpdated != null) {
                            line = line.replaceAll(strPattern, "$1" + valueUpdated + "$4");
                        }
                    }
                    lisLines.add(line);
                }
            }

            // 将结果写回到文件
            try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(copyFile), StandardCharsets.UTF_8))) {
                for(String line : lisLines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            return true;
        } catch (Exception x) {
            x.printStackTrace();
            return false;
        }
    }

    public BukkitTask getTask() {
        return task;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public YamlLoader getYamlConfig() {
        return yamlConfig;
    }

    public Properties getPropertiesConfig() {
        return propertiesConfig;
    }

    public static boolean contains(String name) {
        for (TaskQueue tq : taskQueues) {
            if (tq.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static TaskQueue getTaskQueue(String name) {
        for (TaskQueue tq : taskQueues) {
            if (tq.getName().equals(name)) {
                return tq;
            }
        }
        return null;
    }
}
