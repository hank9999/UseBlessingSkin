package com.github.hank9999.useblessingskin.bungee.commands;

import com.github.hank9999.useblessingskin.bungee.libs.GetConfig;
import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import com.github.hank9999.useblessingskin.bungee.libs.SkinSetter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import java.util.*;
import java.util.stream.Collectors;

public class BungeeCommand extends Command implements TabExecutor {

    public BungeeCommand(String name) {
        super(name);
    }

    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_BLUE + "Version: v" + UseBlessingSkin.instance.getDescription().getVersion()));
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.LIGHT_PURPLE + "Author: hank9999"));
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + "/bskin help"));
            return;
        }
        if (strings[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + GetConfig.str("message.SetSkin")));
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + GetConfig.str("message.AboutIdInfo")));
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + GetConfig.str("message.Support")));

            TextComponent skinWebSiteLink = new TextComponent(ChatColor.DARK_AQUA + GetConfig.str("name") + " " + GetConfig.str("url"));
            skinWebSiteLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GetConfig.str("url")));

            commandSender.sendMessage(skinWebSiteLink);

            if (commandSender.hasPermission("UseBlessingSkin.admin")) {
                commandSender.sendMessage(new TextComponent(""));
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin reload  Reload Config"));
            }
            return;
        }
        if (strings[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("UseBlessingSkin.admin")) {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.DoNotHavePermission")));
                return;
            }
            if (GetConfig.reload()) {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.ReloadSuccess")));
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + "Can't Load Config"));
            }
            return;
        }
        if (strings[0].equalsIgnoreCase("set")) {
            if (strings.length == 1) {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + GetConfig.str("message.SetSkin")));
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + GetConfig.str("message.AboutIdInfo")));
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + GetConfig.str("message.Support")));

                TextComponent skinWebSiteLink = new TextComponent(ChatColor.DARK_AQUA + GetConfig.str("name") + " " + GetConfig.str("url"));
                skinWebSiteLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GetConfig.str("url")));

                commandSender.sendMessage(skinWebSiteLink);

                return;

            } else {
                UseBlessingSkin.instance.getProxy().getScheduler().runAsync(UseBlessingSkin.instance, (() -> {
                    if (commandSender instanceof ProxiedPlayer) {
                        SkinSetter.setSkin(strings[1], (ProxiedPlayer) commandSender);
                    }
                }));
                return;
            }
        }
        commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.NoCommand")));
    }


    private final String[] Commands = {"help", "set", "reload"};

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return Arrays.stream(Commands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }

}
