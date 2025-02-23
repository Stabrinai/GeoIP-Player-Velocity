package com.github.stabrinai.geoipplayer.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.github.stabrinai.geoipplayer.GeoIPPlayer;

public class ReloadCommand {
    public static LiteralArgumentBuilder<CommandSource> createCommand(GeoIPPlayer plugin) {
        return BrigadierCommand.literalArgumentBuilder("reload")
                .requires(ctx -> ctx.hasPermission("geoip.reload"))
                .executes(ctx -> {
                    plugin.reloadConfig();
                    ctx.getSource().sendRichMessage(plugin.getConfigData().node("messages","reload_config").getString(""));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
