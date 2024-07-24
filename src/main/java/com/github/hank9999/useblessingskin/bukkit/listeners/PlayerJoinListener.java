package com.github.hank9999.useblessingskin.bukkit.listeners;

import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;
import com.github.hank9999.useblessingskin.bukkit.libs.GetConfig;
import com.github.hank9999.useblessingskin.bukkit.libs.SkinSetter;
import net.skinsrestorer.api.property.SkinIdentifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GetConfig.checkPath("loginAutoSetSkin") && GetConfig.bool("loginAutoSetSkin")) {
            Player p = event.getPlayer();
            Optional<SkinIdentifier> skinId = UseBlessingSkin.playerStorage.getSkinIdOfPlayer(p.getUniqueId());

            if (skinId.isPresent()) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
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
                }
            }.runTaskAsynchronously(UseBlessingSkin.plugin);
        }
    }
}
