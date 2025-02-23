package com.github.stabrinai.geoipplayer.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.github.stabrinai.geoipplayer.GeoIPPlayer;

import java.util.HashMap;

public class LookupCommand {
    public static LiteralArgumentBuilder<CommandSource> createCommand(GeoIPPlayer plugin) {
        final HashMap<String, HashMap<String, String>> playerData = plugin.getPlayerData();
        return BrigadierCommand.literalArgumentBuilder("lookup")
                .requires(ctx -> ctx.hasPermission("geoip.lookup"))
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((ctx, sug) -> {
                            String input = ctx.getInput().split("lookup")[1].strip();
                            for (String name : playerData.keySet().stream().filter(name -> name.startsWith(input)).toList()) {
                                sug.suggest(name);
                            }
                            return sug.buildFuture();
                        })
                        .executes(ctx -> {
                                    String playerName = ctx.getArgument("player", String.class);
                                    StringBuilder result = new StringBuilder("<#0288d1>" + playerName + " Lookup Result: </#0288d1>");
                                    if (playerData.get(playerName) != null) {
                                        result.append("\n<GRAY> - </GRAY><WHITE>");
                                        for (String key : playerData.get(playerName).keySet()) {
                                            result.append("<#91c6f3>").append(key).append(": </#91c6f3>").append(playerData.get(playerName).get(key)).append("\n<GRAY> - </GRAY>");
                                        }
                                        result.delete(result.length() - 17, result.length());
                                        ctx.getSource().sendRichMessage(result.toString());
                                    } else {
                                        ctx.getSource().sendRichMessage(result.toString());
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }
                        )
                );
    }
}
