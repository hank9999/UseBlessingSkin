package com.github.hank9999.useblessingskin.bungee.libs;

import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import com.github.hank9999.useblessingskin.shared.ErrorCode;
import com.github.hank9999.useblessingskin.shared.model.MineSkinData;
import com.github.hank9999.useblessingskin.shared.model.Result;
import com.github.hank9999.useblessingskin.shared.model.SkinCSLData;
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

            Result<SkinCSLData> textureIdData = getTextureId(
                    GetConfig.str("csl").replaceAll("%name%", URLEncoder.encode(username, "UTF-8"))
            );

            if (!textureIdData.isSuccess()) {
                if (textureIdData.getErrorCode() == ErrorCode.UNKNOWN_ERROR) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RequestError")));
                    return false;
                } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_NOT_EXIST) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleNotExist")));
                    return false;
                } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_RESPONSE_EMPTY) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleResponseEmpty")));
                    return false;
                } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_SKIN_NOT_EXIST) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleSkinNotExist")));
                    if (GetConfig.bool("cdn")) {
                        player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.IfCdnMakeRoleSkinNotExist")));
                    }
                    return false;
                } else {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RequestError")));
                    return false;
                }
            }

            String textureId = textureIdData.getData().getTextureId();
            boolean isSlim = textureIdData.getData().isSlim();

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.TextureIdGetSuccess") + " " + textureId));

            String picName;
            boolean needDownload;

            if (GetConfig.bool("cache")) {
                picName = textureId + ".png";
                needDownload = !checkCache(basePath + File.separator + "Cache" + File.separator + picName);
            } else {
                picName = UUID.randomUUID() + ".png";
                needDownload = true;
            }

            if (needDownload) {
                Result<Boolean> savePicResult = savePic(
                        GetConfig.str("texture").replaceAll("%textureId%", textureId),
                        basePath + File.separator + "Cache" + File.separator,
                        picName
                );
                boolean savePicSuccess = savePicResult.isSuccess() && savePicResult.getData();
                if (!savePicSuccess) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError")));
                    return false;
                }

                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess")));
            }

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + GetConfig.str("message.UploadingTexture")));

            Result<MineSkinData> mineSkinData = MineSkinApi(
                    GetConfig.str("mineskinapi"),
                    picName,
                    basePath + File.separator + "Cache" + File.separator + picName,
                    isSlim
            );

            if (!mineSkinData.isSuccess()) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError")));
                return false;
            }

            String value = mineSkinData.getData().getValue();
            String signature = mineSkinData.getData().getSignature();

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.UploadTextureSuccess")));

            skinStorage.setCustomSkinData(" " + player.getName(), SkinProperty.of(value, signature));

            Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(" " + player.getName());

            if (!result.isPresent()) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + GetConfig.str("message.UnknownError")));
                return false;
            }

            // 确保玩家在线
            if (instance.getProxy().getPlayer(player.getName()) == null) {
                return false;
            }

            playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());

            skinsRestorerAPI.getSkinApplier(ProxiedPlayer.class).applySkin(player);

            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SetSkinSuccess")));
            return true;

        } catch (Exception e) {
            player.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + GetConfig.str("message.UnknownError")));
            e.printStackTrace();
        }
        return false;
    }
}
