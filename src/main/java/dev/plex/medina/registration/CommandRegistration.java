package dev.plex.medina.registration;

import com.google.common.collect.Lists;
import dev.plex.medina.MedinaBase;
import dev.plex.medina.command.MedinaCommand;
import dev.plex.medina.util.MedinaLog;
import dev.plex.medina.util.ReflectionsUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public class CommandRegistration implements MedinaBase
{
    public CommandRegistration()
    {
        Set<Class<? extends MedinaCommand>> commandSet = ReflectionsUtil.getClassesBySubType("dev.plex.medina.command.impl", MedinaCommand.class);
        List<MedinaCommand> commands = Lists.newArrayList();

        commandSet.forEach(clazz ->
        {
            try
            {

                commands.add(clazz.getConstructor().newInstance());
            }
            catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                   NoSuchMethodException ex)
            {
                MedinaLog.error("Failed to register " + clazz.getSimpleName() + " as a command!");
            }
        });
        MedinaLog.log(String.format("Registered %s commands from %s classes!", commands.size(), commandSet.size()));
    }
}
