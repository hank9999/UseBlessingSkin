package com.github.hank9999.useblessingskin.velocity.Commands;

import com.github.hank9999.useblessingskin.velocity.ConfigManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.hank9999.useblessingskin.shared.utils.*;

public final class VelocityCommand implements SimpleCommand {
    private final ProxyServer server;
    private final PluginContainer plugin;
    private final ConfigManager configManager;
    private final ComponentLogger componentLogger;
    private final Path dataFolder;
    private final SkinsRestorer skinsRestorerAPI;
    private final SkinStorage skinStorage;
    private final PlayerStorage playerStorage;


    public VelocityCommand(ProxyServer server, PluginContainer plugin, ConfigManager configManager, ComponentLogger componentLogger, Path dataFolder, SkinsRestorer skinsRestorerAPI) {
        this.server = server;
        this.plugin = plugin;
        this.configManager = configManager;
        this.componentLogger = componentLogger;
        this.dataFolder = dataFolder;
        this.skinsRestorerAPI = skinsRestorerAPI;
        this.skinStorage = skinsRestorerAPI.getSkinStorage();
        this.playerStorage = skinsRestorerAPI.getPlayerStorage();
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text("Version: v" + plugin.getDescription().getVersion().orElse("Unknown"), NamedTextColor.DARK_BLUE)));
            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text("Author: hank9999", NamedTextColor.LIGHT_PURPLE)));
            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text("/bskin help", NamedTextColor.DARK_PURPLE)));
            return;
        }
        if (args[0].equalsIgnoreCase("help")) {
            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text("/bskin set <ID> " + configManager.str("message.SetSkin"), NamedTextColor.WHITE))
            );

            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.AboutIdInfo"), NamedTextColor.WHITE))
            );

            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.Support"), NamedTextColor.DARK_AQUA))
            );

            Component skinWebSiteLink = Component.text(
                    configManager.str("name") + " " + configManager.str("url"),
                    NamedTextColor.DARK_AQUA
            ).clickEvent(ClickEvent.openUrl(configManager.str("url")));

            source.sendMessage(skinWebSiteLink);

            if (source.hasPermission("UseBlessingSkin.admin")) {
                source.sendMessage(Component.empty());
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text("/bskin reload  Reload Config", NamedTextColor.WHITE)));
            }
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!source.hasPermission("UseBlessingSkin.admin")) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.DoNotHavePermission"), NamedTextColor.RED)));
                return;
            }
            if (configManager.reloadConfig()) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.ReloadSuccess"), NamedTextColor.BLUE)));
            } else {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text("Can't Load Config", NamedTextColor.RED)));
            }
            return;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (source instanceof ConsoleCommandSource) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text("This command only can be used as a player", NamedTextColor.RED)));
                return;
            }

            if (args.length < 2) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text("Version: v" + plugin.getDescription().getVersion().orElse("Unknown"), NamedTextColor.DARK_BLUE)));
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text("Author: hank9999", NamedTextColor.LIGHT_PURPLE)));
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text("/bskin help", NamedTextColor.DARK_PURPLE)));
                return;
            }
            String[] textureIdData;
            try {
                textureIdData = getTextureId(
                        configManager.str("csl").replaceAll("%name%", URLEncoder.encode(args[1], "UTF-8"))
                );
            } catch (UnsupportedEncodingException e) {
                componentLogger.error("An error occurred while encoding url!");
                componentLogger.error(e.getLocalizedMessage());
                componentLogger.error(Arrays.toString(e.getStackTrace()));
                return;
            }
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
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RequestError"), NamedTextColor.RED)));
                return;
            } else if (textureId.equals("Role does not exist")) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RoleNotExist"), NamedTextColor.RED)));
                return;
            } else if (textureId.equals("Role response is empty")) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RoleResponseEmpty"), NamedTextColor.RED)));
                return;
            } else if (textureId.equals("Role does not have skin")) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.RoleSkinNotExist"), NamedTextColor.RED)));
                if (configManager.bool("cdn")) {
                    source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                            .append(Component.text(configManager.str("message.IfCdnMakeRoleSkinNotExist"), NamedTextColor.RED)));
                }
                return;
            }

            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.TextureIdGetSuccess") + " " + textureId, NamedTextColor.BLUE))
            );

            String picName;

            if (configManager.bool("cache")) {
                picName = textureId + ".png";
                if (!checkCache(dataFolder + File.separator + "Cache" + File.separator + picName)) {
                    if (!savePic(
                            configManager.str("texture").replaceAll("%textureId%", textureId),
                            dataFolder + File.separator + "Cache" + File.separator,
                            picName)
                    ) {
                        source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                                .append(Component.text(configManager.str("message.SaveTextureError"), NamedTextColor.RED))
                        );
                        return;
                    }

                    source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                            .append(Component.text(configManager.str("message.SaveTextureSuccess"), NamedTextColor.BLUE))
                    );
                }
            } else {
                picName = UUID.randomUUID() + ".png";
                if (!savePic(
                        configManager.str("texture").replaceAll("%textureId%", textureId),
                        dataFolder + File.separator + "Cache" + File.separator,
                        picName)
                ) {
                    source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                            .append(Component.text(configManager.str("message.SaveTextureError"), NamedTextColor.RED))
                    );
                    return;
                }

                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.SaveTextureSuccess"), NamedTextColor.BLUE))
                );
            }

            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.UploadingTexture"), NamedTextColor.DARK_PURPLE))
            );

            String[] MineSkinApi = MineSkinApi(
                    configManager.str("mineskinapi"),
                    picName,
                    dataFolder + File.separator + "Cache" + File.separator + picName,
                    isSlim
            );
            if (MineSkinApi == null) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.UploadTextureError"), NamedTextColor.RED))
                );
                return;
            }
            if (!(MineSkinApi[0].equals("OK"))) {
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.UploadTextureError"), NamedTextColor.RED))
                );
                return;
            }

            String value = MineSkinApi[1];
            String signature = MineSkinApi[2];

            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.UploadTextureSuccess"), NamedTextColor.BLUE)));

            Player player = (Player) source;

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
                source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                        .append(Component.text(configManager.str("message.UnknownError"), NamedTextColor.RED)));
                return;
            }

            playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());

            try {
                skinsRestorerAPI.getSkinApplier(Player.class).applySkin(player);
            } catch (DataRequestException e) {
                componentLogger.error("An error occurred while applying skin!");
                componentLogger.error(e.getLocalizedMessage());
                componentLogger.error(Arrays.toString(e.getStackTrace()));
            }

            source.sendMessage(Component.text("[UBS] ", NamedTextColor.AQUA)
                    .append(Component.text(configManager.str("message.SetSkinSuccess"), NamedTextColor.BLUE))
            );

            return;
        }

    }

    private final String[] Commands = {"help", "set", "reload"};

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length > 1) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        if (args.length == 0) {
            return CompletableFuture.completedFuture(Arrays.asList(Commands));
        }

        return CompletableFuture.completedFuture(Arrays.stream(Commands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList()));
    }
}
