package com.github.hank9999.useblessingskin.bukkit.Update;

import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;
import com.github.hank9999.useblessingskin.shared.httpMethods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.Timer;
import java.util.TimerTask;


final public class Updater {
    private boolean is_first = true;
    private Plugin plugin;

    public Updater() {
        this.plugin = UseBlessingSkin.plugin;
        final Timer timer = new Timer(true); // We use a timer cause the Bukkit scheduler is affected by server lags
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) { // Plugin was disabled
                    timer.cancel();
                    return;
                }
                Bukkit.getScheduler().runTask(UseBlessingSkin.plugin, () -> checkUpdate());
            }
        }, 0, 1000 * 60 * 60);
    }

    private void checkUpdate() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
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
