package com.github.hank9999.useblessingskin.bukkit;

import com.github.hank9999.useblessingskin.bukkit.commands.BukkitCommand;
import com.github.hank9999.useblessingskin.bukkit.libs.GetConfig;

import com.github.hank9999.useblessingskin.bukkit.libs.MetricsLite;
import com.github.hank9999.useblessingskin.bukkit.libs.Updater;
import com.github.hank9999.useblessingskin.bukkit.listeners.PlayerJoinListener;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.MojangAPI;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class UseBlessingSkin extends JavaPlugin {

    public static UseBlessingSkin plugin;
    public static SkinsRestorer skinsRestorerAPI;
    public static SkinStorage skinStorage;
    public static PlayerStorage playerStorage;
    public static MojangAPI mojangAPI;

    @Override
    public void onLoad() {
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin插件已加载");
    }

    @Override
    public void onEnable() {
        plugin = this;
        skinsRestorerAPI = SkinsRestorerProvider.get();
        skinStorage = skinsRestorerAPI.getSkinStorage();
        playerStorage = skinsRestorerAPI.getPlayerStorage();
        mojangAPI = skinsRestorerAPI.getMojangAPI();
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

            if (!GetConfig.checkPath("update") || GetConfig.bool("update")) {
                new Updater();
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        Objects.requireNonNull(getServer().getPluginCommand("bskin")).setExecutor(new BukkitCommand());
        Objects.requireNonNull(getServer().getPluginCommand("bskin")).setTabCompleter(new BukkitCommand());

        getLogger().info(ChatColor.BLUE + "UseBlessingSkin插件已启用");
        getLogger().info(ChatColor.GOLD + "版本 v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        plugin = null;
        skinsRestorerAPI = null;
        skinStorage = null;
        playerStorage = null;
        mojangAPI = null;
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin插件已禁用");
    }
}
