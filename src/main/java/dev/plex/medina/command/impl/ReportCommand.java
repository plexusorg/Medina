package dev.plex.medina.command.impl;

import dev.plex.medina.command.MedinaCommand;
import dev.plex.medina.command.annotation.CommandParameters;
import dev.plex.medina.command.source.RequiredCommandSource;
import dev.plex.medina.data.Report;
import dev.plex.medina.util.MedinaUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@CommandParameters(name = "report", usage = "/<command> <player> <message>", description = "Reports a player", permission = "medina.report", source = RequiredCommandSource.IN_GAME)
public class ReportCommand extends MedinaCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, @NotNull String[] args)
    {
        if (args.length < 2)
        {
            return usage();
        }

        Player player = getNonNullPlayer(args[0]);

        String reason = StringUtils.join(args, " ", 1, args.length);

        Report report = new Report(
                0,
                Bukkit.getPlayer(sender.getName()).getUniqueId(),
                sender.getName(),
                player.getUniqueId(),
                player.getName(),
                ZonedDateTime.now(),
                reason,
                false);

        plugin.getSqlReports().addReport(report);
        return messageComponent("reportSubmitted", player.getName());
    }

    @Override
    public @NotNull List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        if (args.length == 1 && sender.hasPermission("medina.report"))
        {
            return MedinaUtils.getPlayerNameList();
        }
        return Collections.emptyList();
    }
}
