package com.github.hank9999.useblessingskin.bungee.libs;

import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;

import java.io.File;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

import static com.github.hank9999.useblessingskin.bungee.UseBlessingSkin.*;
import static com.github.hank9999.useblessingskin.bungee.UseBlessingSkin.skinsRestorerAPI;
import static com.github.hank9999.useblessingskin.shared.Utils.*;

public class SkinSetter {
    private final static String basePath = UseBlessingSkin.instance.getDataFolder().toString();
    public static boolean setSkin(String username, ProxiedPlayer player) {
        try {

            String[] textureIdData = getTextureId(
                    GetConfig.str("csl").replaceAll("%name%", URLEncoder.encode(username, "UTF-8"))
            );

            String isSlim;
            String textureId;

            if (textureIdData == null) {
                isSlim = "false";
                textureId = null;
            } else {
                isSlim = textureIdData[0];
                textureId = textureIdData[1];
            }

            if (textureId == null) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RequestError")));
                return false;
            } else if (textureId.equals("Role does not exist")) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleNotExist")));
                return false;
            } else if (textureId.equals("Role response is empty")) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleResponseEmpty")));
                return false;
            } else if (textureId.equals("Role does not have skin")) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleSkinNotExist")));
                if (GetConfig.bool("cdn")) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.IfCdnMakeRoleSkinNotExist")));
                }
                return false;
            }

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.TextureIdGetSuccess") + " " + textureId));

            String picName;

            if (GetConfig.bool("cache")) {
                picName = textureId + ".png";
                if (!checkCache(basePath + File.separator + "Cache" + File.separator + picName)) {
                    if (!savePic(
                            GetConfig.str("texture").replaceAll("%textureId%", textureId),
                            basePath + File.separator + "Cache" + File.separator,
                            picName)
                    ) {
                        player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError")));
                        return false;
                    }

                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess")));
                }
            } else {
                picName = UUID.randomUUID() + ".png";
                if (!savePic(
                        GetConfig.str("texture").replaceAll("%textureId%", textureId),
                        basePath + File.separator + "Cache" + File.separator,
                        picName)
                ) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError")));
                    return false;
                }

                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess")));
            }

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + GetConfig.str("message.UploadingTexture")));

            String[] MineSkinApi = MineSkinApi(
                    GetConfig.str("mineskinapi"),
                    picName,
                    basePath + File.separator + "Cache" + File.separator + picName,
                    isSlim
            );

            if (MineSkinApi == null) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError")));
                return false;
            }
            if (!(MineSkinApi[0].equals("OK"))) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError")));
                return false;
            }

            String value = MineSkinApi[1];
            String signature = MineSkinApi[2];

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.UploadTextureSuccess")));


            skinStorage.setCustomSkinData(" " + player.getName(), SkinProperty.of(value, signature));

            Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(" " + player.getName());

            if (!result.isPresent()) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + GetConfig.str("message.UnknownError")));
                return false;
            }

            // 确保玩家在线
            ProxiedPlayer player2 = instance.getProxy().getPlayer(player.getName());
            playerStorage.setSkinIdOfPlayer(player2.getUniqueId(), result.get().getIdentifier());

            skinsRestorerAPI.getSkinApplier(ProxiedPlayer.class).applySkin(player2);

            player2.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SetSkinSuccess")));
            return true;

        } catch (Exception e) {
            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + GetConfig.str("message.UnknownError")));
            e.printStackTrace();
        }
        return false;
    }
}
