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

@CommandParameters(name = "consoleonly", usage = "/<command>", description = "Tests the command system with a console only command", permission = "medina.consoleonly", source = RequiredCommandSource.CONSOLE)
public class ConsoleOnlyCommand extends MedinaCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, @NotNull String[] args)
    {
        return mmString("<green>ConsoleOnlyCommand registered.");
    }

    @Override
    public @NotNull List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        return List.of();
    }
}
