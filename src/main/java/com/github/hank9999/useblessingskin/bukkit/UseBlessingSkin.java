package com.github.hank9999.useblessingskin.bukkit;

import com.github.hank9999.useblessingskin.bukkit.Commands.BukkitCommand;
import com.github.hank9999.useblessingskin.bukkit.bStats.MetricsLite;

import com.github.hank9999.useblessingskin.bukkit.Update.Updater;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class UseBlessingSkin extends JavaPlugin {

    public static UseBlessingSkin plugin;

    @Override
    public void onLoad() {
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin插件已加载");
    }

    @Override
    public void onEnable() {
        plugin = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        reloadConfig();

        if (!getDescription().getVersion().contains("dev")) {
            try {
                MetricsLite metrics = new MetricsLite(this, 7957);
                getLogger().info(ChatColor.GOLD + "bStats Metrics Enable");
            } catch (Exception exception) {
                getLogger().warning("An error occurred while enabling bStats Metrics!");
            }

            new Updater();
        }

        Objects.requireNonNull(getServer().getPluginCommand("bskin")).setExecutor(new BukkitCommand());
        Objects.requireNonNull(getServer().getPluginCommand("bskin")).setTabCompleter(new BukkitCommand());

        getLogger().info(ChatColor.BLUE + "UseBlessingSkin插件已启用");
        getLogger().info(ChatColor.GOLD + "版本 v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin插件已禁用");
    }
}
