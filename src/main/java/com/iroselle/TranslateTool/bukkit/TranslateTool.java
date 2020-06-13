package com.iroselle.TranslateTool.bukkit;

import com.iroselle.TranslateTool.bukkit.Config.Config;
import com.iroselle.TranslateTool.bukkit.hook.PAPIHook;
import com.iroselle.TranslateTool.bukkit.util.Debug;
import com.iroselle.TranslateTool.bukkit.util.TranslateUtils;
import com.iroselle.TranslateTool.bukkit.util.YamlTranslate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TranslateTool extends JavaPlugin {

    public static TranslateTool instance;

    private final String[] subCommands = {"config", "translate", "debug", "reload"};

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§3  _______                  _       _    _______          _ ");
        Bukkit.getConsoleSender().sendMessage("§3 |__   __|                | |     | |  |__   __|        | |");
        Bukkit.getConsoleSender().sendMessage("§3    | |_ __ __ _ _ __  ___| | __ _| |_ ___| | ___   ___ | |");
        Bukkit.getConsoleSender().sendMessage("§3    | | '__/ _` | '_ \\/ __| |/ _` | __/ _ \\ |/ _ \\ / _ \\| |");
        Bukkit.getConsoleSender().sendMessage("§3    | | | | (_| | | | \\__ \\ | (_| | ||  __/ | (_) | (_) | |");
        Bukkit.getConsoleSender().sendMessage("§3    |_|_|  \\__,_|_| |_|___/_|\\__,_|\\__\\___|_|\\___/ \\___/|_|");
        Bukkit.getConsoleSender().sendMessage("§3                                                           ");
        Bukkit.getConsoleSender().sendMessage(" §eRoselle Translate Tool §aV"+getDescription().getVersion()+"");
        Bukkit.getConsoleSender().sendMessage(" §fby RoselleTeams");
        Bukkit.getConsoleSender().sendMessage("");
        instance = this;
        Config.loadConfig();
        Bukkit.getConsoleSender().sendMessage(" §6Config loaded!");
        getCommand("translateTool").setExecutor(this);
        Bukkit.getConsoleSender().sendMessage(" §6Successful register commands and tabComplete!");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIHook().register();
            Bukkit.getConsoleSender().sendMessage(" §eSuccessful hooked PlaceholderAPI!");
        }

        Bukkit.getConsoleSender().sendMessage(" §e加载完成! 输入 /tt help 获取更多帮助!");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {

        for (TaskQueue tq : TaskQueue.taskQueues) {
            if (!tq.getTask().isCancelled()) tq.getTask().cancel();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("translatetool.admin")) {
            sender.sendMessage("§c你没有权限");
            return true;
        }

        if (args.length < 1) {
            this.helpMessage(s, sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "config":{
                if (args.length < 2) {
                    sender.sendMessage("§c请输入文件路径!");
                    break;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }
                String append = stringBuilder.toString();

                if (TaskQueue.contains(append)) {
                    sender.sendMessage("§a"+append+" 的翻译已经在队列中了!");
                    break;
                }

                TaskQueue.taskQueues.add(new TaskQueue(append, new File(append), sender));

                break;
            }
            case "translate":{
                if (args.length < 2) {
                    sender.sendMessage("§c请输入翻译内容!");
                    break;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }
                sender.sendMessage("§a正在翻译..."+stringBuilder.toString());
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                    String s1 = TranslateUtils.translate(stringBuilder.toString());
                    sender.sendMessage("§b原文: §e"+stringBuilder.toString());
                    sender.sendMessage("§b翻译: §e"+s1);
                    sender.sendMessage("§a翻译完成!"+stringBuilder.toString());
                });
                break;
            }
            /*case "task":{
                if (args.length < 2) {
                    this.helpMessage(s, sender);
                    break;
                }

                switch (args[1].toLowerCase()) {
                    case "cancel":{
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            stringBuilder.append(args[i]).append(" ");
                        }
                        String append = stringBuilder.toString();

                        TaskQueue taskQueue = TaskQueue.getTaskQueue(append);
                        if (taskQueue == null) {
                            sender.sendMessage("§a"+append+" 没有在队列中!");
                            break;
                        }

                        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                            if (taskQueue.saveFile()) {
                                sender.sendMessage("§b成功保存未完成翻译的 " + taskQueue.getName() + "!");
                                if (!taskQueue.getTask().isCancelled()){ {
                                    taskQueue.getTask().cancel();
                                    sender.sendMessage("§b成功取消队列 " + taskQueue.getName() + "!");
                                }}
                                taskQueue.remove();

                            } else {
                                sender.sendMessage("§c保存 " + taskQueue.getName() + " 失败! 文件不存在或无法读取!");
                            }
                        });

                        break;
                    }
                    case "save":{
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            stringBuilder.append(args[i]).append(" ");
                        }
                        String append = stringBuilder.toString();

                        TaskQueue taskQueue = TaskQueue.getTaskQueue(append);
                        if (taskQueue == null) {
                            sender.sendMessage("§a"+append+" 没有在队列中!");
                            break;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

                            if (taskQueue.saveFile()) {
                                sender.sendMessage("§b成功保存正在翻译的 " + taskQueue.getName() + "!");
                            } else {
                                sender.sendMessage("§c保存 " + taskQueue.getName() + " 失败! 文件不存在或无法读取!");
                            }
                        });

                        break;
                    }
                }


                break;
            }*/
            case "debug":{
                Debug.setDebug();
                break;
            }
            case "reload":{
                Config.loadConfig();
                sender.sendMessage("§a配置文件重新载入完成!");
                break;
            }
            default:{
                this.helpMessage(s, sender);
                break;
            }
        }

        return true;
    }

    private void helpMessage(String s,CommandSender sender) {
        sender.sendMessage("§a使用命令");
        sender.sendMessage("§2/"+s.toUpperCase()+" §bCONFIG <配置文件地址>");
//        sender.sendMessage("§2/"+s.toUpperCase()+" §bTASK CANCEL §7- 取消当前正在运行的任务并保存文件!");
        sender.sendMessage("§2/"+s.toUpperCase()+" §bTRANSLATE <翻译的内容>");
        sender.sendMessage("§2/"+s.toUpperCase()+" §bDEBUG §7- 开启调试模式");
        sender.sendMessage("§2/"+s.toUpperCase()+" §bRELOAD §7- 重新载入配置文件");
        sender.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length > 1) return new ArrayList<>();

        if (args.length < 1) return Arrays.asList(subCommands);

        return Arrays.stream(subCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }


}
