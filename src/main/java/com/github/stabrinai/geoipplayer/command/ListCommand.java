package com.github.stabrinai.geoipplayer.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.github.stabrinai.geoipplayer.GeoIPPlayer;

import java.util.HashMap;

public class ListCommand {
    public static LiteralArgumentBuilder<CommandSource> createCommand(GeoIPPlayer plugin) {
        final HashMap<String, HashMap<String, String>> playerData = plugin.getPlayerData();
        return BrigadierCommand.literalArgumentBuilder("list")
                .requires(ctx -> ctx.hasPermission("geoip.list"))
                .executes(ctx -> {
                    StringBuilder result = new StringBuilder("<#0288d1>GeoIP Players(" + playerData.size() +"):</#0288d1>");
                    if (!playerData.isEmpty()) {
                        result.append("\n<GRAY> - </GRAY>");
                        for (String playerName : playerData.keySet()) {
                            result.append("<WHITE><hover:show_text:'");
                            HashMap<String, String> playerValue = playerData.get(playerName);
                            for (String key : playerValue.keySet()) {
                                result.append(key).append(": ").append(playerData.get(playerName).get(key)).append("\n");
                            }
                            result.delete(result.length() - 1, result.length());
                            result.append("'><click:run_command:/geoip lookup ").append(playerName).append(">");

                            if (playerValue.containsKey("ERROR")) {
                                result.append("<RED>").append(playerName).append("</RED>, ");
                            } else if (plugin.getServer().getPlayer(playerName).isEmpty()) {
                                result.append("<GRAY>").append(playerName).append("</GRAY>, ");
                            } else {
                                result.append("<GREEN>").append(playerName).append("</GREEN>, ");
                            }

                        }
                    }
                    if (result.toString().endsWith(", "))
                        result.delete(result.length() - 2, result.length());
                    ctx.getSource().sendRichMessage(result.toString());
                    return Command.SINGLE_SUCCESS;
                });
    }
}
