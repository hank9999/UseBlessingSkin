package com.github.hank9999.useblessingskin.bukkit.libs;

import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;
import com.github.hank9999.useblessingskin.shared.ErrorCode;
import com.github.hank9999.useblessingskin.shared.model.MineSkinData;
import com.github.hank9999.useblessingskin.shared.model.Result;
import com.github.hank9999.useblessingskin.shared.model.SkinCSLData;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

import static com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin.skinsRestorerAPI;
import static com.github.hank9999.useblessingskin.shared.Utils.*;

public class SkinSetter {
    private static final String basePath = UseBlessingSkin.plugin.getDataFolder().toString();

    public static boolean setSkin(String username, Player player) {
        try {

            Result<SkinCSLData> textureIdData = getTextureId(
                    GetConfig.str("csl").replaceAll("%name%", URLEncoder.encode(username, "UTF-8"))
            );

            if (!textureIdData.isSuccess()) {
                if (textureIdData.getErrorCode() == ErrorCode.UNKNOWN_ERROR) {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RequestError"));
                    return false;
                } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_NOT_EXIST) {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleNotExist"));
                    return false;
                } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_RESPONSE_EMPTY) {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleResponseEmpty"));
                    return false;
                } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_SKIN_NOT_EXIST) {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleSkinNotExist"));
                    if (GetConfig.bool("cdn")) {
                        player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.IfCdnMakeRoleSkinNotExist"));
                    }
                    return false;
                } else {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
                    return false;
                }
            }

            String textureId = textureIdData.getData().getTextureId();
            boolean isSlim = textureIdData.getData().isSlim();

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.TextureIdGetSuccess") + " " + textureId);

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
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError"));
                    return false;
                }

                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess"));
            }

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + GetConfig.str("message.UploadingTexture"));

            Result<MineSkinData> mineSkinData = MineSkinApi(
                    GetConfig.str("mineskinapi"),
                    picName,
                    basePath + File.separator + "Cache" + File.separator + picName,
                    isSlim
            );

            if (!mineSkinData.isSuccess()) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError"));
                return false;
            }

            String value = mineSkinData.getData().getValue();
            String signature = mineSkinData.getData().getSignature();

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.UploadTextureSuccess"));


            UseBlessingSkin.skinStorage.setCustomSkinData(" " + player.getName(), SkinProperty.of(value, signature));

            Optional<InputDataResult> result = UseBlessingSkin.skinStorage.findOrCreateSkinData(" " + player.getName());

            if (!result.isPresent()) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
                return false;
            }

            // 防止应用时玩家不在线
            if (UseBlessingSkin.plugin.getServer().getPlayer(player.getName()) == null) {
                return false;
            }

            UseBlessingSkin.playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());

            skinsRestorerAPI.getSkinApplier(Player.class).applySkin(player);

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SetSkinSuccess"));
            return true;

        } catch (Exception e) {
            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
            e.printStackTrace();
        }
        return false;
    }
}
