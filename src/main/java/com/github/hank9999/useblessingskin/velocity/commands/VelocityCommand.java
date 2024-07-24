package com.github.hank9999.useblessingskin.velocity.commands;

import com.github.hank9999.useblessingskin.velocity.libs.ConfigManager;
import com.github.hank9999.useblessingskin.velocity.libs.SkinSetter;
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
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

            SkinSetter.setSkin(args[1], server, (Player) source, configManager, componentLogger, dataFolder,
                    skinsRestorerAPI, skinStorage, playerStorage);

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
