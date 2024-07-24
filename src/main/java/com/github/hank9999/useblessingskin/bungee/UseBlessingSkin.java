package com.github.hank9999.useblessingskin.bungee;

import com.github.hank9999.useblessingskin.bungee.libs.GetConfig;
import com.github.hank9999.useblessingskin.bungee.libs.Updater;
import com.github.hank9999.useblessingskin.bungee.libs.MetricsLite;
import com.github.hank9999.useblessingskin.bungee.listeners.ServerConnectListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import com.github.hank9999.useblessingskin.bungee.commands.BungeeCommand;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.MojangAPI;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;

import java.io.*;
import java.nio.file.Files;


public final class UseBlessingSkin extends Plugin {

    public static UseBlessingSkin instance;
    public static Configuration configuration;
    public static SkinsRestorer skinsRestorerAPI;
    public static SkinStorage skinStorage;
    public static PlayerStorage playerStorage;
    public static MojangAPI mojangAPI;

    @Override
    public void onLoad() {
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin Load");
    }

    @Override
    public void onEnable() {
        instance = this;

        skinsRestorerAPI = SkinsRestorerProvider.get();
        skinStorage = skinsRestorerAPI.getSkinStorage();
        playerStorage = skinsRestorerAPI.getPlayerStorage();
        mojangAPI = skinsRestorerAPI.getMojangAPI();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException("Can't Load Config", e);
        }

        getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        getProxy().getPluginManager().registerCommand(this, new BungeeCommand("bskin"));

        if (!getDescription().getVersion().contains("dev")) {
            try {
                MetricsLite metrics = new MetricsLite(this, 7959);
                getLogger().info(ChatColor.GOLD + "bStats Metrics Enable");
            } catch (Exception exception) {
                getLogger().warning("An error occurred while enabling bStats Metrics!");
            }

            if (!GetConfig.checkPath("update") || GetConfig.bool("update")) {
                new Updater();
            }
        }

        getLogger().info("UseBlessingSkin Enable");
        getLogger().info(ChatColor.GOLD + "Version v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        instance = null;
        skinsRestorerAPI = null;
        skinStorage = null;
        playerStorage = null;
        mojangAPI = null;
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin Disable");
    }

}
