package com.github.hank9999.useblessingskin.velocity.listeners;

import com.github.hank9999.useblessingskin.velocity.libs.ConfigManager;
import com.github.hank9999.useblessingskin.velocity.libs.SkinSetter;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.property.SkinIdentifier;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;

import java.nio.file.Path;
import java.util.Optional;

public class PlayerJoinListener {
    private final ProxyServer server;
    private final PluginContainer plugin;
    private final ConfigManager configManager;
    private final ComponentLogger componentLogger;
    private final Path dataFolder;
    private final SkinsRestorer skinsRestorerAPI;
    private final SkinStorage skinStorage;
    private final PlayerStorage playerStorage;

    public PlayerJoinListener(ProxyServer server, PluginContainer plugin, ConfigManager configManager, ComponentLogger componentLogger, Path dataFolder, SkinsRestorer skinsRestorerAPI) {
        this.server = server;
        this.plugin = plugin;
        this.configManager = configManager;
        this.componentLogger = componentLogger;
        this.dataFolder = dataFolder;
        this.skinsRestorerAPI = skinsRestorerAPI;
        this.skinStorage = skinsRestorerAPI.getSkinStorage();
        this.playerStorage = skinsRestorerAPI.getPlayerStorage();
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        if (!configManager.checkPath("loginAutoSetSkin") || !configManager.bool("loginAutoSetSkin")) {
            return;
        }
        Player p = event.getPlayer();
        Optional<SkinIdentifier> skinId = playerStorage.getSkinIdOfPlayer(p.getUniqueId());

        if (skinId.isPresent()) {
            return;
        }

        try {
            if (!configManager.checkPath("loginAutoSetSkinMojangFirst") || configManager.bool("loginAutoSetSkinMojangFirst")) {

                if (skinsRestorerAPI.getMojangAPI().getSkin(p.getUsername()).isPresent()) {
                    return;
                }
                SkinSetter.setSkin(p.getUsername(), server, p, configManager, componentLogger, dataFolder,
                        skinsRestorerAPI, skinStorage, playerStorage);

            } else {
                SkinSetter.setSkin(p.getUsername(), server, p, configManager, componentLogger, dataFolder,
                        skinsRestorerAPI, skinStorage, playerStorage);
            }
        } catch (Exception ignored) {
        }

    }
}
