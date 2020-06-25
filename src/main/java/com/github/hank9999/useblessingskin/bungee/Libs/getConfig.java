package com.github.hank9999.useblessingskin.bungee.Libs;

import com.github.hank9999.useblessingskin.bungee.UseBlessingSkin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

final public class getConfig {
    public static String str(String path) {
        return Objects.requireNonNull(UseBlessingSkin.configuration.getString(path));
    }

    public static Boolean bool(String path) {
        return UseBlessingSkin.configuration.getBoolean(path);
    }

    public static Boolean reload() {
        try {
            UseBlessingSkin.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(UseBlessingSkin.instance.getDataFolder(), "config.yml"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
