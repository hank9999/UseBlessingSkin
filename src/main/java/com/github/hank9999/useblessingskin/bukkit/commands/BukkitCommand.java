package com.github.hank9999.useblessingskin.bukkit.commands;

import com.github.hank9999.useblessingskin.bukkit.libs.GetConfig;
import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;

import com.github.hank9999.useblessingskin.bukkit.libs.SkinSetter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

final public class BukkitCommand implements TabExecutor {

    private final String[] Commands = {"help", "set", "reload"};

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("bskin")) {
            if (strings.length == 0) {
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_BLUE + "Version: v" + UseBlessingSkin.plugin.getDescription().getVersion());
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.LIGHT_PURPLE + "Author: hank9999");
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + "/bskin help");
                return true;
            }
            if (strings[0].equalsIgnoreCase("help")) {
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + GetConfig.str("message.SetSkin"));
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + GetConfig.str("message.AboutIdInfo"));
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + GetConfig.str("message.Support"));
                commandSender.sendMessage(ChatColor.DARK_AQUA + GetConfig.str("name") + " " + GetConfig.str("url"));
                if (commandSender.hasPermission("UseBlessingSkin.admin")) {
                    commandSender.sendMessage("");
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin reload  Reload Config");
                }
                return true;
            }
            if (strings[0].equalsIgnoreCase("reload")) {
                if (!commandSender.hasPermission("UseBlessingSkin.admin")) {
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.DoNotHavePermission"));
                    return false;
                }
                UseBlessingSkin.plugin.reloadConfig();
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.ReloadSuccess"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length == 1) {
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + GetConfig.str("message.SetSkin"));
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + GetConfig.str("message.AboutIdInfo"));
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + GetConfig.str("message.Support"));
                    commandSender.sendMessage(ChatColor.DARK_AQUA + GetConfig.str("name") + " " + GetConfig.str("url"));
                    return true;
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SkinSetter.setSkin(strings[1], (Player) commandSender);
                        }
                    }.runTaskAsynchronously(UseBlessingSkin.plugin);
                }
                return true;
            }
            commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.NoCommand"));
        }
        return true;
    }


    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return Arrays.stream(Commands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}