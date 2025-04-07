package com.github.stabrinai.geoipplayer.listener;

import com.github.stabrinai.geoipplayer.GeoIPPlayer;
import com.github.stabrinai.geoipplayer.service.GeoIPService;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class PlayerListener {
    private final GeoIPPlayer plugin;
    private final HashMap<String, HashMap<String, String>> playerData;
    private final GeoIPService service;

    public PlayerListener(GeoIPPlayer plugin, GeoIPService service) {
        this.plugin = plugin;
        this.service = service;
        this.playerData = plugin.getPlayerData();
    }

    @Subscribe
    public void onPlayerEnter(ServerConnectedEvent event) {
        String lobby = plugin.getConfigData().node("server").getString("none");
        if (lobby.equals(event.getServer().getServerInfo().getName()) || lobby.equals("none")) {
            InetSocketAddress ip = event.getPlayer().getRemoteAddress();
            playerData.remove(event.getPlayer().getUsername());
            if (ip != null) {
                playerData.put(event.getPlayer().getUsername(), service.lookupByIp(ip));
            }
        }
    }

    @Subscribe
    public void onPlayerQuitEvent(DisconnectEvent event) {
        if (plugin.getConfigData().node("toggle", "delete_on_quit").getBoolean(false))
            playerData.remove(event.getPlayer().getUsername());
    }
}
