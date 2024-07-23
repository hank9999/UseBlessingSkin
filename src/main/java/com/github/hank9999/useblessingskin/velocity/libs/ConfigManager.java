package com.github.hank9999.useblessingskin.velocity.libs;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private final Path configFile;
    private final ComponentLogger componentLogger;
    private CommentedConfigurationNode configuration;

    @Inject
    public ConfigManager(ComponentLogger componentLogger, @DataDirectory Path dataFolder) {
        this.componentLogger = componentLogger;
        this.configFile = dataFolder.resolve("config.yml");

        if (!Files.exists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (IOException e) {
                componentLogger.error("An error occurred while creating the data folder!");
            }
        }

        loadConfig();
    }

    public boolean loadConfig() {
        try {
            if (!Files.exists(configFile)) {
                saveDefaultConfig();
            }
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configFile).build();
            configuration = loader.load();
            return true;
        } catch (IOException e) {
            componentLogger.error("An error occurred while loading configuration file!");
            componentLogger.error(e.getLocalizedMessage());
            componentLogger.error(Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    public void saveDefaultConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(("config.yml"))) {
            if (in != null) {
                Files.copy(in, configFile);
            }
        } catch (IOException e) {
            componentLogger.error("An error occurred while copying the default configuration file!");
            componentLogger.error(e.getLocalizedMessage());
            componentLogger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    public String str(String path) {
        return configuration.node((Object[]) path.split("\\.")).getString();
    }

    public boolean bool(String path) {
        return configuration.node((Object[]) path.split("\\.")).getBoolean();
    }

    public boolean checkPath(String path) {
        return configuration.node((Object[]) path.split("\\.")).isNull();
    }

    public boolean reloadConfig() {
        return loadConfig();
    }
}
