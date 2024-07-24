package com.github.hank9999.useblessingskin.bukkit.libs;

import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;
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
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RequestError"));
                return false;
            } else if (textureId.equals("Role does not exist")) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleNotExist"));
                return false;
            } else if (textureId.equals("Role response is empty")) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleResponseEmpty"));
                return false;
            } else if (textureId.equals("Role does not have skin")) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleSkinNotExist"));
                if (GetConfig.bool("cdn")) {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.IfCdnMakeRoleSkinNotExist"));
                }
                return false;
            }

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.TextureIdGetSuccess") + " " + textureId);

            String picName;

            if (GetConfig.bool("cache")) {
                picName = textureId + ".png";
                if (!checkCache(basePath + File.separator + "Cache" + File.separator + picName)) {
                    if (!savePic(
                            GetConfig.str("texture").replaceAll("%textureId%", textureId),
                            basePath + File.separator + "Cache" + File.separator,
                            picName)
                    ) {
                        player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError"));
                        return false;
                    }

                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess"));
                }

            } else {
                picName = UUID.randomUUID() + ".png";
                if (!savePic(
                        GetConfig.str("texture").replaceAll("%textureId%", textureId),
                        basePath + File.separator + "Cache" + File.separator,
                        picName)
                ) {
                    player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError"));
                    return false;
                }

                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess"));
            }

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + GetConfig.str("message.UploadingTexture"));

            String[] MineSkinApi = MineSkinApi(
                    GetConfig.str("mineskinapi"),
                    picName,
                    basePath + File.separator + "Cache" + File.separator + picName,
                    isSlim
            );

            if (MineSkinApi == null) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError"));
                return false;
            }
            if (!(MineSkinApi[0].equals("OK"))) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError"));
                return false;
            }

            String value = MineSkinApi[1];
            String signature = MineSkinApi[2];

            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.UploadTextureSuccess"));


            UseBlessingSkin.skinStorage.setCustomSkinData(" " + player.getName(), SkinProperty.of(value, signature));

            Optional<InputDataResult> result = UseBlessingSkin.skinStorage.findOrCreateSkinData(" " + player.getName());

            if (!result.isPresent()) {
                player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
                return false;
            }

            // 防止应用时玩家不在线
            Player player2 = UseBlessingSkin.plugin.getServer().getPlayer(player.getName());

            if (player2 == null) {
                return false;
            }

            UseBlessingSkin.playerStorage.setSkinIdOfPlayer(player2.getUniqueId(), result.get().getIdentifier());

            skinsRestorerAPI.getSkinApplier(Player.class).applySkin(player2);

            player2.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SetSkinSuccess"));
            return true;

        } catch (Exception e) {
            player.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
            e.printStackTrace();
        }
        return false;
    }
}
