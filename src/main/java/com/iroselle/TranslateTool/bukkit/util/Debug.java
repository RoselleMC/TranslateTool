package com.iroselle.TranslateTool.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Debug {

    private static boolean debug;

    public static void setDebug() {
        if (debug) {
            debug = false;
            msg("调试模式已关闭");
        } else {
            debug = true;
            msg("调试模式已开启");
        }
    }

    public static void msg(String string) {
        if (debug) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&a[TranslateTool]&b[DEBUG] &7"+string));
    }

}
