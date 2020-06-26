package com.github.hank9999.useblessingskin.bungee.Commands;

import com.github.hank9999.useblessingskin.bungee.Libs.getConfig;
import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.storage.SkinStorage;

import java.net.URLEncoder;
import java.util.UUID;

import static com.github.hank9999.useblessingskin.shared.utils.*;


public class BungeeCommand extends Command {

    private final String basePath = UseBlessingSkin.instance.getDataFolder().toString();


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
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + getConfig.str("message.SetSkin")));
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + getConfig.str("message.AboutIdInfo")));
            commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + getConfig.str("message.Support")));

            TextComponent skinWebSiteLink = new TextComponent(ChatColor.DARK_AQUA + getConfig.str("name") + " " + getConfig.str("url"));
            skinWebSiteLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getConfig.str("url")));

            commandSender.sendMessage(skinWebSiteLink);

            if (commandSender.hasPermission("UseBlessingSkin.admin")) {
                commandSender.sendMessage(new TextComponent(""));
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin reload  Reload Config"));
            }
            return;
        }
        if (strings[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("UseBlessingSkin.admin")) {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.DoNotHavePermission")));
                return;
            }
            if (getConfig.reload()) {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.ReloadSuccess")));
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + "Can't Load Config"));
            }
        }
        if (strings[0].equalsIgnoreCase("set")) {
            if (strings.length == 1) {
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + "/bskin set <ID> " + getConfig.str("message.SetSkin")));
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.WHITE + getConfig.str("message.AboutIdInfo")));
                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_AQUA + getConfig.str("message.Support")));

                TextComponent skinWebSiteLink = new TextComponent(ChatColor.DARK_AQUA + getConfig.str("name") + " " + getConfig.str("url"));
                skinWebSiteLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getConfig.str("url")));

                commandSender.sendMessage(skinWebSiteLink);

            } else {
                UseBlessingSkin.instance.getProxy().getScheduler().runAsync(UseBlessingSkin.instance,
                        (() -> {
                            try {

                                String textureId = getTextureId(
                                        getConfig.str("csl").replaceAll("%name%", URLEncoder.encode(strings[1], "UTF-8"))
                                );

                                if (textureId == null) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RequestError")));
                                    return;
                                } else if (textureId.equals("Role does not exist")) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RoleNotExist")));
                                    return;
                                } else if (textureId.equals("Role response is empty")) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RoleResponseEmpty")));
                                    return;
                                } else if (textureId.equals("Role does not have skin")) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.RoleSkinNotExist")));
                                    if (getConfig.bool("cdn")) {
                                        commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.IfCdnMakeRoleSkinNotExist")));
                                    }
                                    return;
                                }

                                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.TextureIdGetSuccess") + " " + textureId));

                                String picName = UUID.randomUUID().toString() + ".png";

                                if (!savePic(
                                        getConfig.str("texture").replaceAll("%textureId%", textureId),
                                        basePath + "\\Cache\\",
                                        picName)
                                ) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.SaveTextureError")));
                                    return;
                                }

                                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.SaveTextureSuccess")));
                                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.DARK_PURPLE + getConfig.str("message.UploadingTexture")));

                                String[] MineSkinApi = MineSkinApi(
                                        getConfig.str("mineskinapi"),
                                        picName,
                                        basePath + "\\Cache\\" + picName
                                );

                                if (MineSkinApi == null) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.UploadTextureError")));
                                    return;
                                }
                                if (!(MineSkinApi[0].equals("OK"))) {
                                    commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.RED + getConfig.str("message.UploadTextureError")));
                                    return;
                                }

                                String value = MineSkinApi[1];
                                String signature = MineSkinApi[2];

                                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.UploadTextureSuccess")));

                                SkinsRestorer skinsRestorer = SkinsRestorer.getInstance();
                                SkinStorage skinStorage = skinsRestorer.getSkinStorage();

                                skinStorage.setSkinData(" " + commandSender.getName(), skinStorage.createProperty("textures", value, signature));
                                skinStorage.setPlayerSkin(commandSender.getName(), " " + commandSender.getName());
                                skinsRestorer.getSkinsRestorerBungeeAPI().applySkin((ProxiedPlayer) commandSender);

                                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + ChatColor.BLUE + getConfig.str("message.SetSkinSuccess")));

                            } catch (Exception e) {
                                commandSender.sendMessage(new TextComponent(ChatColor.AQUA + "[UBS] " + getConfig.str("message.UnknownError")));
                                e.printStackTrace();
                            }
                        }));
            }
        }
    }
}
