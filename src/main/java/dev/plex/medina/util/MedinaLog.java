package dev.plex.medina.util;

import dev.plex.medina.Medina;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class MedinaLog
{
    private static final ComponentLogger logger = ComponentLogger.logger("");

    public static void log(String message, Object... strings)
    {
        for (int i = 0; i < strings.length; i++)
        {
            if (strings[i] == null) continue;
            if (message.contains("{" + i + "}"))
            {
                message = message.replace("{" + i + "}", strings[i].toString());
            }
        }
        logger.info(MedinaUtils.mmDeserialize("<yellow>[Medina] <gray>" + message));
    }

    public static void log(Component component)
    {
        logger.info(Component.text("[Medina] ").color(NamedTextColor.YELLOW).append(component).colorIfAbsent(NamedTextColor.GRAY));
    }

    public static void error(String message, Object... strings)
    {
        for (int i = 0; i < strings.length; i++)
        {
            if (strings[i] == null) continue;
            if (message.contains("{" + i + "}"))
            {
                message = message.replace("{" + i + "}", strings[i].toString());
            }
        }
        logger.error(MedinaUtils.mmDeserialize("<red>[Medina Error] <gold>" + message));
    }

    public static void warn(String message, Object... strings)
    {
        for (int i = 0; i < strings.length; i++)
        {
            if (strings[i] == null) continue;
            if (message.contains("{" + i + "}"))
            {
                message = message.replace("{" + i + "}", strings[i].toString());
            }
        }
        logger.warn(MedinaUtils.mmDeserialize("<#eb7c0e>[Medina Warning] <gold>" + message));
    }

    public static void debug(String message, Object... strings)
    {
        if (Medina.getPlugin().config.getBoolean("debug"))
        {
            for (int i = 0; i < strings.length; i++)
            {
                if (strings[i] == null) continue;
                if (message.contains("{" + i + "}"))
                {
                    message = message.replace("{" + i + "}", strings[i].toString());
                }
            }
            logger.info(MedinaUtils.mmDeserialize("<dark_purple>[Medina Debug] <gold>" + message));
        }
    }
}