package com.github.hank9999.useblessingskin.bukkit.commands;

import com.github.hank9999.useblessingskin.bukkit.libs.GetConfig;
import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;

import static com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin.skinsRestorerAPI;
import static com.github.hank9999.useblessingskin.shared.Utils.*;

import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
                    (new BukkitRunnable() {
                        public void run() {
                            try {

                                String[] textureIdData = getTextureId(
                                        GetConfig.str("csl").replaceAll("%name%", URLEncoder.encode(strings[1], "UTF-8"))
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
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RequestError"));
                                    return;
                                } else if (textureId.equals("Role does not exist")) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleNotExist"));
                                    return;
                                } else if (textureId.equals("Role response is empty")) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleResponseEmpty"));
                                    return;
                                } else if (textureId.equals("Role does not have skin")) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.RoleSkinNotExist"));
                                    if (GetConfig.bool("cdn")) {
                                        commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.IfCdnMakeRoleSkinNotExist"));
                                    }
                                    return;
                                }

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.TextureIdGetSuccess") + " " + textureId);

                                String picName;

                                if (GetConfig.bool("cache")) {
                                    picName = textureId + ".png";
                                    if (!checkCache(basePath + File.separator + "Cache" + File.separator + picName)) {
                                        if (!savePic(
                                                GetConfig.str("texture").replaceAll("%textureId%", textureId),
                                                basePath + File.separator + "Cache" + File.separator,
                                                picName)
                                        ) {
                                            commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError"));
                                            return;
                                        }

                                        commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess"));
                                    }

                                } else {
                                    picName = UUID.randomUUID() + ".png";
                                    if (!savePic(
                                            GetConfig.str("texture").replaceAll("%textureId%", textureId),
                                            basePath + File.separator + "Cache" + File.separator,
                                            picName)
                                    ) {
                                        commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.SaveTextureError"));
                                        return;
                                    }

                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SaveTextureSuccess"));
                                }

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + GetConfig.str("message.UploadingTexture"));

                                String[] MineSkinApi = MineSkinApi(
                                        GetConfig.str("mineskinapi"),
                                        picName,
                                        basePath + File.separator + "Cache" + File.separator + picName,
                                        isSlim
                                );

                                if (MineSkinApi == null) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError"));
                                    return;
                                }
                                if (!(MineSkinApi[0].equals("OK"))) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UploadTextureError"));
                                    return;
                                }

                                String value = MineSkinApi[1];
                                String signature = MineSkinApi[2];

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.UploadTextureSuccess"));


                                UseBlessingSkin.skinStorage.setCustomSkinData(" " + commandSender.getName(), SkinProperty.of(value, signature));

                                Optional<InputDataResult> result = UseBlessingSkin.skinStorage.findOrCreateSkinData(" " + commandSender.getName());

                                if (!result.isPresent()) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
                                    return;
                                }

                                Player player = UseBlessingSkin.plugin.getServer().getPlayer(commandSender.getName());

                                if (player == null) {
                                    commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
                                    return;
                                }

                                UseBlessingSkin.playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());

                                skinsRestorerAPI.getSkinApplier(Player.class).applySkin(player);

                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + GetConfig.str("message.SetSkinSuccess"));

                            } catch (Exception e) {
                                commandSender.sendMessage(ChatColor.AQUA + "[UBS] " + ChatColor.RED + GetConfig.str("message.UnknownError"));
                                e.printStackTrace();
                            }
                        }
                    }).runTaskAsynchronously(UseBlessingSkin.plugin);
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