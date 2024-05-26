package dev.plex.medina.command.impl;

import dev.plex.medina.command.MedinaCommand;
import dev.plex.medina.command.annotation.CommandParameters;
import dev.plex.medina.command.source.RequiredCommandSource;
import dev.plex.medina.data.Report;
import dev.plex.medina.util.MedinaUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@CommandParameters(name = "reports", usage = "/<command> <player> <list | delete <id>>", description = "View existing reports on a player", permission = "medina.reports", source = RequiredCommandSource.ANY)
public class ReportsCommand extends MedinaCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, @NotNull String[] args)
    {
        if (args.length < 2)
        {
            return usage();
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore())
        {
            return messageComponent("playerNotFound");
        }

        switch (args[1].toLowerCase())
        {
            case "list":
            {
                plugin.getSqlReports().getReports(offlinePlayer.getUniqueId()).whenComplete((reports, ex) ->
                {
                    // we don't want to include deleted reports in the logic
                    long count = reports.stream()
                            .filter(report -> !report.isDeleted())
                            .count();
                    if (count <= 0)
                    {
                        send(sender, messageComponent("noReports", offlinePlayer.getName()));
                        return;
                    }
                    readReports(sender, offlinePlayer, reports);
                });

                return null;
            }
            case "delete":
            {
                if (args.length < 3)
                {
                    return usage();
                }
                int id = parseInt(sender, args[2]);
                plugin.getSqlReports().getReports(id).whenComplete(((report, ex) ->
                {
                    if (report == null)
                    {
                        send(sender, messageComponent("reportDoesntExist"));
                        return;
                    }
                    if (report.isDeleted())
                    {
                        send(sender, messageComponent("reportDoesntExist"));
                        return;
                    }
                    else
                    {
                        plugin.getSqlReports().deleteReport(id, offlinePlayer.getUniqueId()).whenComplete((report1, ex1) ->
                        {
                            send(sender, messageComponent("deletedReport", id));
                        });
                    }
                }));

                return null;
            }
            default:
            {
                return usage();
            }
        }
    }

    private void readReports(@NotNull CommandSender sender, OfflinePlayer player, List<Report> reports)
    {
        AtomicReference<Component> reportList = new AtomicReference<>(messageComponent("reportHeader", player.getName()));
        for (Report report : reports)
        {
            if (report.isDeleted())
            {
                continue;
            }
            Component reportLine = messageComponent("reportPrefix", report.getReportId(), player.getName(), MedinaUtils.useTimezone(report.getTimestamp()));
            reportLine = reportLine.append(messageComponent("reportLine", report.getReason()));
            reportList.set(reportList.get().append(Component.newline()));
            reportList.set(reportList.get().append(reportLine));
        }
        send(sender, reportList.get());
    }

    @Override
    public @NotNull List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        if (args.length == 1 && sender.hasPermission("medina.reports"))
        {
            return MedinaUtils.getPlayerNameList();
        }
        if (args.length == 2 && sender.hasPermission("medina.reports"))
        {
            return List.of("list", "delete");
        }
        return Collections.emptyList();
    }
}
