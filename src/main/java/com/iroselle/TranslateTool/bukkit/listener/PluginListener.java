package com.iroselle.TranslateTool.bukkit.listener;

import com.iroselle.TranslateTool.bukkit.TranslateTool;
import com.iroselle.TranslateTool.bukkit.hook.PAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PluginListener implements Listener {

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        Plugin plugin = e.getPlugin();

        if (plugin.getName().equals("PlaceholderAPI")) {
            new PAPIHook().register();
            Bukkit.getConsoleSender().sendMessage("§eSuccessful hooked PlaceholderAPI!");
        }


    }

}
