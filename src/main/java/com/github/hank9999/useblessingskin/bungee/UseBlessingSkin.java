package com.github.hank9999.useblessingskin.bungee;

import com.github.hank9999.useblessingskin.bungee.Update.Updater;
import com.github.hank9999.useblessingskin.bungee.bStats.MetricsLite;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import com.github.hank9999.useblessingskin.bungee.Commands.BungeeCommand;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.skinsrestorer.api.SkinsRestorerAPI;

import java.io.*;
import java.nio.file.Files;


public final class UseBlessingSkin extends Plugin {

    public static UseBlessingSkin instance;
    public static Configuration configuration;
    public static SkinsRestorerAPI skinsRestorerAPI;

    @Override
    public void onLoad() {
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin Load");
    }

    @Override
    public void onEnable() {
        instance = this;
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
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

        getProxy().getPluginManager().registerCommand(this, new BungeeCommand("bskin"));

        if (!getDescription().getVersion().contains("dev")) {
            try {
                MetricsLite metrics = new MetricsLite(this, 7959);
                getLogger().info(ChatColor.GOLD + "bStats Metrics Enable");
            } catch (Exception exception) {
                getLogger().warning("An error occurred while enabling bStats Metrics!");
            }

            new Updater();
        }

        getLogger().info("UseBlessingSkin Enable");
        getLogger().info(ChatColor.GOLD + "Version v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        instance = null;
        skinsRestorerAPI = null;
        getLogger().info(ChatColor.BLUE + "UseBlessingSkin Disable");
    }

}
