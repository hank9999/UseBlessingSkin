package com.github.hank9999.useblessingskin.bukkit.Commands;

import com.github.hank9999.useblessingskin.bukkit.Libs.getConfig;
import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;

import static com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin.skinsRestorerAPI;
import static com.github.hank9999.useblessingskin.shared.utils.*;

import net.skinsrestorer.api.property.IProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.skinsrestorer.api.PlayerWrapper;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

final public class BukkitCommand implements TabExecutor {

    private final String[] Commands = {"help", "set", "reload"};
    private final String basePath = UseBlessingSkin.plugin.getDataFolder().toString();

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
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + getConfig.str("message.SetSkin"));
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + getConfig.str("message.AboutIdInfo"));
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + getConfig.str("message.Support"));
                commandSender.sendMessage(ChatColor.DARK_AQUA + getConfig.str("name") + " " + getConfig.str("url"));
                if (commandSender.hasPermission("UseBlessingSkin.admin")) {
                    commandSender.sendMessage("");
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin reload  Reload Config");
                }
                return true;
            }
            if (strings[0].equalsIgnoreCase("reload")) {
                if (!commandSender.hasPermission("UseBlessingSkin.admin")) {
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.DoNotHavePermission"));
                    return false;
                }
                UseBlessingSkin.plugin.reloadConfig();
                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.ReloadSuccess"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length == 1) {
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + getConfig.str("message.SetSkin"));
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + getConfig.str("message.AboutIdInfo"));
                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + getConfig.str("message.Support"));
                    commandSender.sendMessage(ChatColor.DARK_AQUA + getConfig.str("name") + " " + getConfig.str("url"));
                    return true;
                } else {
                    (new BukkitRunnable() {
                        public void run() {
                            try {

                                String[] textureIdData = getTextureId(
                                        getConfig.str("csl").replaceAll("%name%", URLEncoder.encode(strings[1], "UTF-8"))
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
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RequestError"));
                                    return;
                                } else if (textureId.equals("Role does not exist")) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RoleNotExist"));
                                    return;
                                } else if (textureId.equals("Role response is empty")) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RoleResponseEmpty"));
                                    return;
                                } else if (textureId.equals("Role does not have skin")) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RoleSkinNotExist"));
                                    if (getConfig.bool("cdn")) {
                                        commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.IfCdnMakeRoleSkinNotExist"));
                                    }
                                    return;
                                }

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.TextureIdGetSuccess") + " " + textureId);

                                String picName;

                                if (getConfig.bool("cache")) {
                                    picName = textureId + ".png";
                                    if (!checkCache(basePath + "\\Cache\\" + picName)) {
                                        if (!savePic(
                                                getConfig.str("texture").replaceAll("%textureId%", textureId),
                                                basePath + "\\Cache\\",
                                                picName)
                                        ) {
                                            commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.SaveTextureError"));
                                            return;
                                        }

                                        commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.SaveTextureSuccess"));
                                    }

                                } else {
                                    picName = UUID.randomUUID() + ".png";
                                    if (!savePic(
                                            getConfig.str("texture").replaceAll("%textureId%", textureId),
                                            basePath + "\\Cache\\",
                                            picName)
                                    ) {
                                        commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.SaveTextureError"));
                                        return;
                                    }

                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.SaveTextureSuccess"));
                                }

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + getConfig.str("message.UploadingTexture"));

                                String[] MineSkinApi = MineSkinApi(
                                        getConfig.str("mineskinapi"),
                                        picName,
                                        basePath + "\\Cache\\" + picName,
                                        isSlim
                                );

                                if (MineSkinApi == null) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.UploadTextureError"));
                                    return;
                                }
                                if (!(MineSkinApi[0].equals("OK"))) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.UploadTextureError"));
                                    return;
                                }

                                String value = MineSkinApi[1];
                                String signature = MineSkinApi[2];

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.UploadTextureSuccess"));

                                IProperty iProperty = skinsRestorerAPI.createPlatformProperty("textures", value, signature);
                                skinsRestorerAPI.setSkinData(" " + commandSender.getName(), iProperty, 9223243187835955807L);

                                skinsRestorerAPI.setSkin(commandSender.getName(), " " + commandSender.getName());

                                skinsRestorerAPI.applySkin(new PlayerWrapper(commandSender));

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.SetSkinSuccess"));

                            } catch (Exception e) {
                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.UnknownError"));
                                e.printStackTrace();
                            }
                        }
                    }).runTaskAsynchronously(UseBlessingSkin.plugin);
                }
                return true;
            }
            commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.NoCommand"));
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