package com.github.hank9999.useblessingskin.velocity.libs;

import com.github.hank9999.useblessingskin.shared.ErrorCode;
import com.github.hank9999.useblessingskin.shared.model.MineSkinData;
import com.github.hank9999.useblessingskin.shared.model.Result;
import com.github.hank9999.useblessingskin.shared.model.SkinCSLData;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static com.github.hank9999.useblessingskin.shared.Utils.*;

public class SkinSetter {
    public static boolean setSkin(String username, ProxyServer server, Player player, ConfigManager configManager,
                                  ComponentLogger componentLogger, Path dataFolder, SkinsRestorer skinsRestorerAPI,
                                  SkinStorage skinStorage, PlayerStorage playerStorage) {
        Result<SkinCSLData> textureIdData;
        try {
            textureIdData = getTextureId(
                    configManager.str("csl").replaceAll("%name%", URLEncoder.encode(username, "UTF-8"))
            );
        } catch (UnsupportedEncodingException e) {
            componentLogger.error("An error occurred while encoding url!");
            componentLogger.error(e.getLocalizedMessage());
            componentLogger.error(Arrays.toString(e.getStackTrace()));
            return false;
        }

        if (!textureIdData.isSuccess()) {
            if (textureIdData.getErrorCode() == ErrorCode.UNKNOWN_ERROR) {
                player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RequestError"), NamedTextColor.RED)));
                return false;
            } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_NOT_EXIST) {
                player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RoleNotExist"), NamedTextColor.RED)));
                return false;
            } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_RESPONSE_EMPTY) {
                player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RoleResponseEmpty"), NamedTextColor.RED)));
                return false;
            } else if (textureIdData.getErrorCode() == ErrorCode.ROLE_SKIN_NOT_EXIST) {
                player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RoleSkinNotExist"), NamedTextColor.RED)));
                if (configManager.bool("cdn")) {
                    player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                            .append(Component.text(configManager.str("message.IfCdnMakeRoleSkinNotExist"), NamedTextColor.RED)));
                }
                return false;
            } else {
                player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RequestError"), NamedTextColor.RED)));
                return false;
            }
        }

        String textureId = textureIdData.getData().getTextureId();
        boolean isSlim = textureIdData.getData().isSlim();

        player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                .append(Component.text(configManager.str("message.TextureIdGetSuccess") + " " + textureId, NamedTextColor.BLUE))
        );

        String picName;
        boolean needDownload;

        if (configManager.bool("cache")) {
            picName = textureId + ".png";
            needDownload = !checkCache(dataFolder + File.separator + "Cache" + File.separator + picName);
        } else {
            picName = UUID.randomUUID() + ".png";
            needDownload = true;
        }

        if (needDownload) {
            Result<Boolean> savePicResult = savePic(
                    configManager.str("texture").replaceAll("%textureId%", textureId),
                    dataFolder + File.separator + "Cache" + File.separator,
                    picName
            );
            boolean savePicSuccess = savePicResult.isSuccess() && savePicResult.getData();
            if (!savePicSuccess) {
                player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.SaveTextureError"), NamedTextColor.RED))
                );
                return false;
            }

            player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.SaveTextureSuccess"), NamedTextColor.BLUE))
            );
        }

        player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                .append(Component.text(configManager.str("message.UploadingTexture"), NamedTextColor.DARK_PURPLE))
        );

        Result<MineSkinData> mineSkinData = MineSkinApi(
                configManager.str("mineskinapi"),
                picName,
                dataFolder + File.separator + "Cache" + File.separator + picName,
                isSlim
        );
        if (!mineSkinData.isSuccess()) {
            player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.UploadTextureError"), NamedTextColor.RED))
            );
            return false;
        }

        String value = mineSkinData.getData().getValue();
        String signature = mineSkinData.getData().getSignature();

        player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                .append(Component.text(configManager.str("message.UploadTextureSuccess"), NamedTextColor.BLUE)));



        skinStorage.setCustomSkinData(" " + player.getUsername(), SkinProperty.of(value, signature));

        Optional<InputDataResult> result = Optional.empty();
        try {
            result = skinStorage.findOrCreateSkinData(" " + player.getUsername());
        } catch (DataRequestException | MineSkinException e) {
            componentLogger.error("An error occurred while dealing with skin data with SkinsRestorer!");
            componentLogger.error(e.getLocalizedMessage());
            componentLogger.error(Arrays.toString(e.getStackTrace()));
        }

        if (!result.isPresent()) {
            player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.UnknownError"), NamedTextColor.RED)));
            return false;
        }

        if (!server.getPlayer(player.getUniqueId()).isPresent()) {
            return false;
        }

        playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());

        try {
            skinsRestorerAPI.getSkinApplier(Player.class).applySkin(player);
        } catch (DataRequestException e) {
            componentLogger.error("An error occurred while applying skin!");
            componentLogger.error(e.getLocalizedMessage());
            componentLogger.error(Arrays.toString(e.getStackTrace()));
        }

        player.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                .append(Component.text(configManager.str("message.SetSkinSuccess"), NamedTextColor.BLUE))
        );
        return true;
    }
}
