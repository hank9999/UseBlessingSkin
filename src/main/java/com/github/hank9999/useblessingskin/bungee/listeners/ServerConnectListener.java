package com.github.hank9999.useblessingskin.bungee.listeners;

import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import com.github.hank9999.useblessingskin.bungee.libs.GetConfig;
import com.github.hank9999.useblessingskin.bungee.libs.SkinSetter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.skinsrestorer.api.property.SkinIdentifier;

import java.util.Optional;

public class ServerConnectListener implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (!GetConfig.checkPath("loginAutoSetSkin") || !GetConfig.bool("loginAutoSetSkin")) {
            return;
        }
        ProxiedPlayer p = event.getPlayer();
        Optional<SkinIdentifier> skinId = UseBlessingSkin.playerStorage.getSkinIdOfPlayer(p.getUniqueId());

        if (skinId.isPresent()) {
            return;
        }

        UseBlessingSkin.instance.getProxy().getScheduler().runAsync(UseBlessingSkin.instance, (() -> {
            try {
                if (!GetConfig.checkPath("loginAutoSetSkinMojangFirst") || GetConfig.bool("loginAutoSetSkinMojangFirst")) {
                    if (UseBlessingSkin.mojangAPI.getSkin(p.getName()).isPresent()) {
                        return;
                    }
                    SkinSetter.setSkin(p.getName(), p);
                } else {
                    SkinSetter.setSkin(p.getName(), p);
                }
            } catch (Exception ignored) {
            }
        }));
    }
}
