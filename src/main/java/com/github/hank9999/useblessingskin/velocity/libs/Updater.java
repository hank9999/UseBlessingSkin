package com.github.hank9999.useblessingskin.velocity.libs;

import com.github.hank9999.useblessingskin.shared.HttpMethods;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Updater {
    private boolean isFirst = true;
    private final PluginContainer plugin;
    private final ComponentLogger componentLogger;

    public Updater(ProxyServer server, PluginContainer plugin, ComponentLogger componentLogger) {
        this.plugin = plugin;
        this.componentLogger = componentLogger;

        server.getScheduler().buildTask(plugin, this::checkUpdate).repeat(60, java.util.concurrent.TimeUnit.MINUTES)
                .delay(0, TimeUnit.MILLISECONDS).schedule();
    }
    private void checkUpdate() {
        CompletableFuture.runAsync(() -> {
            try {
                String Response = HttpMethods.getString("https://hank9999.github.io/UseBlessingSkin/version.txt");
                if (Response == null) {
                    throw new Exception("Response Empty");
                }
                if (("v" + plugin.getDescription().getVersion().orElse("Unknown")).equalsIgnoreCase(Response)) {
                    if (isFirst) {
                        componentLogger.info(Component.text("No new update available.", NamedTextColor.AQUA));
                        isFirst = false;
                    }
                } else {
                    componentLogger.info(Component.text("A new update " + Response + " available!", NamedTextColor.AQUA));
                    componentLogger.info(Component.text("See it in https://github.com/hank9999/UseBlessingSkin/releases", NamedTextColor.AQUA));
                }
            } catch (Exception e) {
                componentLogger.error(Component.text("Cannot look for updates: " + e.getMessage(), NamedTextColor.RED));
                componentLogger.error(e.getLocalizedMessage());
                componentLogger.error(Arrays.toString(e.getStackTrace()));
            }
        });
    }

}
