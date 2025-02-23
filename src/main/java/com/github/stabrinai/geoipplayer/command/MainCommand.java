package com.github.stabrinai.geoipplayer.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.github.stabrinai.geoipplayer.GeoIPPlayer;
import com.velocitypowered.api.command.CommandSource;

public class MainCommand {
    public static BrigadierCommand registerAllCommand(GeoIPPlayer plugin) {
        LiteralCommandNode<CommandSource> geoip = BrigadierCommand.literalArgumentBuilder("geoip")
                .requires(ctx -> ctx.hasPermission("geoip.command"))
                .then(ListCommand.createCommand(plugin))
                .then(ReloadCommand.createCommand(plugin))
                .then(LookupCommand.createCommand(plugin))
                .build();
        return new BrigadierCommand(geoip);
    }
}
