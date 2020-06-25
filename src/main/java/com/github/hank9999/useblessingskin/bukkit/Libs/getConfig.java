package com.github.hank9999.useblessingskin.bukkit.Libs;

import com.github.hank9999.useblessingskin.bukkit.UseBlessingSkin;

import java.util.Objects;

final public class getConfig {
    public static String str(String path) {
        return Objects.requireNonNull(UseBlessingSkin.plugin.getConfig().getString(path));
    }
    public static Boolean bool(String path) {
        return UseBlessingSkin.plugin.getConfig().getBoolean(path);
    }
}
