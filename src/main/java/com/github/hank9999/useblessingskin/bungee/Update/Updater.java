package com.github.hank9999.useblessingskin.bungee.Update;

import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import com.github.hank9999.useblessingskin.shared.httpMethods;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;


final public class Updater {
    private boolean is_first = true;
    private Plugin plugin;

    public Updater() {
        this.plugin = UseBlessingSkin.instance;
        this.plugin.getProxy().getScheduler().schedule(this.plugin, this::checkUpdate, 0, 60, TimeUnit.MINUTES);
    }

    private void checkUpdate() {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            try {
                String Response = httpMethods.getUrl("https://hank9999.github.io/UseBlessingSkin/version.txt");
                if (Response == null) {
                    throw new Exception("Response Empty");
                }
                if (("v" + this.plugin.getDescription().getVersion()).equalsIgnoreCase(Response)) {
                    if (is_first) {
                        this.plugin.getLogger().info(ChatColor.AQUA + "No new update available.");
                        is_first = false;
                    } else {
                        this.plugin.getLogger().info(ChatColor.AQUA + "A new update " + Response + " available!");
                        this.plugin.getLogger().info(ChatColor.AQUA + "See it in https://github.com/hank9999/UseBlessingSkin/releases");
                    }
                }
            } catch (Exception e) {
                this.plugin.getLogger().info(ChatColor.AQUA + "Cannot look for updates: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
