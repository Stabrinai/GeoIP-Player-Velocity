package com.github.stabrinai.geoipplayer;

import com.github.stabrinai.geoipplayer.command.MainCommand;
import com.github.stabrinai.geoipplayer.listener.PlayerListener;
import com.github.stabrinai.geoipplayer.service.GeoIPService;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@Plugin(id = "geoip-player", name = "GeoIP-Player", version = "1.0-SNAPSHOT")
public class GeoIPPlayer {

    @Getter
    final HashMap<String, HashMap<String, String>> playerData = new HashMap<>();

    @Getter
    @Inject
    private Logger logger;

    @Getter
    private final Path dataDirectory;
    @Getter
    private final ProxyServer server;

    @Getter
    private ConfigurationNode configData;

    @Inject
    public GeoIPPlayer(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reloadConfig();

        server.getEventManager().register(this, new PlayerListener(this, new GeoIPService(this)));

        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("geoip")
                .plugin(this)
                .build();
        commandManager.register(commandMeta, MainCommand.registerAllCommand(this));

    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        playerData.clear();
    }


    public void reloadConfig() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            Path configPath = dataDirectory.resolve("config.yml");
            if (!configPath.toFile().exists()) {
                try (InputStream defaultConfigStream = this.getClass().getResourceAsStream("/config.yml")) {
                    if (defaultConfigStream != null) {
                        Files.copy(defaultConfigStream, configPath);
                    } else {
                        logger.warn("No default config found!");
                    }
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configPath).build();
            configData = loader.load();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
