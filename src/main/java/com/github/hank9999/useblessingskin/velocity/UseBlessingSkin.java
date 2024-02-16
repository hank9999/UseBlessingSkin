package com.github.hank9999.useblessingskin.velocity;

import com.github.hank9999.useblessingskin.velocity.Commands.VelocityCommand;
import com.github.hank9999.useblessingskin.velocity.bStats.Metrics;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import java.nio.file.Path;
@Plugin(id = "useblessingskin", name = "UseBlessingSkin", version = "1.2.0",
        url = "https://github.com/hank9999/UseBlessingSkin",
        description = "Let SkinsRestorer use Blessing Skin Server's / CSL API's skins",
        authors = {"hank9999"},
        dependencies = {@Dependency(id = "skinsrestorer", optional = false)})
public class UseBlessingSkin {
    private final ProxyServer server;
    private final PluginContainer plugin;
    private final ComponentLogger componentLogger;
    private final Metrics.Factory metricsFactory;
    private final Path dataFolder;
    private final ConfigManager configManager;

    @Inject
    public UseBlessingSkin(ProxyServer server, PluginContainer plugin, ComponentLogger logger, @DataDirectory Path dataFolder, Metrics.Factory metricsFactory, ConfigManager configManager) {
        this.server = server;
        this.plugin = plugin;
        this.componentLogger = logger;
        this.dataFolder = dataFolder;
        this.metricsFactory = metricsFactory;
        this.configManager = configManager;

        componentLogger.info(Component.text("UseBlessingSkin Load").color(NamedTextColor.BLUE));
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        SkinsRestorer skinsRestorerAPI = SkinsRestorerProvider.get();

        if (!plugin.getDescription().getVersion().orElse("dev").contains("dev")) {
            try {
                metricsFactory.make(this, 20877);
                componentLogger.info(Component.text("bStats Metrics Enable").color(NamedTextColor.GOLD));
            } catch (Exception exception) {
                componentLogger.warn("An error occurred while enabling bStats Metrics!");
            }

            if (configManager.bool("update")) {
                new Updater(server, plugin, componentLogger);
            }
        }

        server.getCommandManager().register("bskin", new VelocityCommand(server, plugin, configManager, componentLogger, dataFolder, skinsRestorerAPI));
        componentLogger.info(Component.text("UseBlessingSkin Enable").color(NamedTextColor.BLUE));
    }
}
