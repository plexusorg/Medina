package dev.plex.medina.command;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import dev.plex.medina.Medina;
import dev.plex.medina.command.annotation.CommandParameters;
import dev.plex.medina.command.source.RequiredCommandSource;
import dev.plex.medina.util.MedinaUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public abstract class MedinaCommand extends Command implements PluginIdentifiableCommand
{
    /**
     * Returns the instance of the plugin
     */
    protected static Medina plugin = Medina.getPlugin();

    /**
     * The parameters for the command
     */
    private final CommandParameters params;

    /**
     * Required command source fetched from the parameters
     */
    private final RequiredCommandSource commandSource;

    public MedinaCommand(boolean register)
    {
        super("");
        this.params = getClass().getAnnotation(CommandParameters.class);

        setName(this.params.name());
        setLabel(this.params.name());
        setDescription(this.params.description());
        setDescription(this.params.permission());
        setUsage(params.usage().replace("<command>", this.params.name()));
        if (params.aliases().split(",").length > 0)
        {
            setAliases(Arrays.asList(params.aliases().split(",")));
        }
        this.commandSource = this.params.source();

        if (register)
        {
            if (getMap().getKnownCommands().containsKey(this.getName().toLowerCase()))
            {
                getMap().getKnownCommands().remove(this.getName().toLowerCase());
            }
            this.getAliases().forEach(s ->
            {
                if (getMap().getKnownCommands().containsKey(s.toLowerCase()))
                {
                    getMap().getKnownCommands().remove(s.toLowerCase());
                }
            });
            getMap().register("medina", this);
        }
    }

    public MedinaCommand()
    {
        this(true);
    }

    /**
     * The plugin
     *
     * @return The instance of the plugin
     * @see Medina
     */
    @Override
    public @NotNull Medina getPlugin()
    {
        return plugin;
    }

    /**
     * Executes the command
     *
     * @param sender       The sender of the command
     * @param playerSender The player who executed the command (null if CommandSource is console or if CommandSource is any but console executed)
     * @param args         A Kyori Component to send to the sender (can be null)
     */
    protected abstract Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, @NotNull String[] args);

    /**
     * @hidden
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args)
    {
        if (!matches(label))
        {
            return false;
        }

        if (commandSource == RequiredCommandSource.CONSOLE && sender instanceof Player)
        {
            sender.sendMessage(messageComponent("noPermissionInGame"));
            return true;
        }

        if (commandSource == RequiredCommandSource.IN_GAME)
        {
            if (sender instanceof ConsoleCommandSender)
            {
                send(sender, messageComponent("noPermissionConsole"));
                return true;
            }
        }

        if (sender instanceof Player player)
        {

            if (!params.permission().isEmpty() && !player.hasPermission(params.permission()))
            {
                send(sender, messageComponent("noPermissionNode", params.permission()));
                return true;
            }
        }

        if (sender instanceof ConsoleCommandSender && !sender.getName().equalsIgnoreCase("console")) //telnet
        {
            if (!params.permission().isEmpty() && !Bukkit.getPlayer(sender.getName()).hasPermission(params.permission()))
            {
                send(sender, messageComponent("noPermissionNode", params.permission()));
                return true;
            }
        }
        try
        {
            Component component = this.execute(sender, isConsole(sender) ? null : (Player) sender, args);
            if (component != null)
            {
                send(sender, component);
            }
        }
        catch (NumberFormatException ex)
        {
            send(sender, MedinaUtils.mmDeserialize(ex.getMessage()));
        }
        return true;
    }

    /**
     * Sends a message to an Audience
     *
     * @param audience The Audience to send the message to
     * @param s        The message to send
     */
    protected void send(Audience audience, String s)
    {
        audience.sendMessage(componentFromString(s));
    }

    /**
     * Sends a message to an Audience
     *
     * @param audience  The Audience to send the message to
     * @param component The Component to send
     */
    protected void send(Audience audience, Component component)
    {
        audience.sendMessage(component);
    }

    @NotNull
    public abstract List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException;

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        List<String> list = smartTabComplete(sender, alias, args);
        return StringUtil.copyPartialMatches(args[args.length - 1], list, Lists.newArrayList());
    }

    /**
     * Checks if the String given is a matching command
     *
     * @param label The String to check
     * @return true if the string is a command name or alias
     */
    private boolean matches(String label)
    {
        if (params.aliases().split(",").length > 0)
        {
            for (String alias : params.aliases().split(","))
            {
                if (alias.equalsIgnoreCase(label) || getName().equalsIgnoreCase(label))
                {
                    return true;
                }
            }
        }
        else if (params.aliases().split(",").length < 1)
        {
            return getName().equalsIgnoreCase(label);
        }
        return false;
    }

    /**
     * Checks whether a sender is console
     *
     * @param sender A command sender
     * @return true if the sender is console
     */
    protected boolean isConsole(CommandSender sender)
    {
        return !(sender instanceof Player);
    }

    /**
     * Converts a message entry from the "messages.yml" to a Component
     *
     * @param s       The message entry
     * @param objects Any objects to replace in order
     * @return A Kyori Component
     */
    protected Component messageComponent(String s, Object... objects)
    {
        return MedinaUtils.messageComponent(s, objects);
    }

    /**
     * Converts a message entry from the "messages.yml" to a Component
     *
     * @param s       The message entry
     * @param objects Any objects to replace in order
     * @return A Kyori Component
     */
    protected Component messageComponent(String s, Component... objects)
    {
        return MedinaUtils.messageComponent(s, objects);
    }

    /**
     * Converts a message entry from the "messages.yml" to a String
     *
     * @param s       The message entry
     * @param objects Any objects to replace in order
     * @return A String
     */
    protected String messageString(String s, Object... objects)
    {
        return MedinaUtils.messageString(s, objects);
    }

    /**
     * Converts a String to a legacy Kyori Component
     *
     * @param s The String to convert
     * @return A Kyori component
     */
    protected Component componentFromString(String s)
    {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s).colorIfAbsent(NamedTextColor.GRAY);
    }

    /**
     * Converts usage to a Component
     *
     * @return A Kyori Component stating the usage
     */
    protected Component usage()
    {
        return Component.text("Correct Usage: ").color(NamedTextColor.YELLOW).append(componentFromString(this.getUsage()).color(NamedTextColor.GRAY));
    }

    /**
     * Converts usage to a Component
     * <p>
     * s The usage to convert
     *
     * @return A Kyori Component stating the usage
     */
    protected Component usage(String s)
    {
        return Component.text("Correct Usage: ").color(NamedTextColor.YELLOW).append(componentFromString(s).color(NamedTextColor.GRAY));
    }

    /**
     * Converts a String to a MiniMessage Component
     *
     * @param s The String to convert
     * @return A Kyori Component
     */
    protected Component mmString(String s)
    {
        return MedinaUtils.mmDeserialize(s);
    }

    public CommandMap getMap()
    {
        return Medina.getPlugin().getServer().getCommandMap();
    }
}