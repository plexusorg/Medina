package dev.plex.medina.util;

import dev.plex.medina.MedinaBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class MedinaUtils implements MedinaBase
{
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component mmDeserialize(String input)
    {
        return MINI_MESSAGE.deserialize(input);
    }

    public static String mmSerialize(Component input)
    {
        return MINI_MESSAGE.serialize(input);
    }

    public static Component mmCustomDeserialize(String input, TagResolver... resolvers)
    {
        return MiniMessage.builder().tags(TagResolver.builder().resolvers(resolvers).build()).build().deserialize(input);
    }

    public static Component messageComponent(String entry, Object... objects)
    {
        return MINI_MESSAGE.deserialize(messageString(entry, objects));
    }

    public static Component messageComponent(String entry, Component... objects)
    {
        Component component = MINI_MESSAGE.deserialize(messageString(entry));
        for (int i = 0; i < objects.length; i++)
        {
            int finalI = i;
            component = component.replaceText(builder -> builder.matchLiteral("{" + finalI + "}").replacement(objects[finalI]).build());
        }
        return component;
    }

    public static String messageString(String entry, Object... objects)
    {
        String f = plugin.messages.getString(entry);
        if (f == null)
        {
            throw new NullPointerException();
        }
        for (int i = 0; i < objects.length; i++)
        {
            f = f.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return f;
    }
}