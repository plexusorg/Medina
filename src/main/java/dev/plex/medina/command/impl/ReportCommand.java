package dev.plex.medina.command.impl;

import dev.plex.medina.command.MedinaCommand;
import dev.plex.medina.command.annotation.CommandParameters;
import dev.plex.medina.command.source.RequiredCommandSource;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "report", usage = "/<command> <player> <message>", description = "Reports a player", permission = "medina.report", source = RequiredCommandSource.IN_GAME)
public class ReportCommand extends MedinaCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, @NotNull String[] args)
    {
        return mmString("<green>Report command registered.");
    }

    @Override
    public @NotNull List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        return List.of();
    }
}
