package dev.plex.medina;

import dev.plex.medina.config.Config;
import dev.plex.medina.registration.CommandRegistration;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;


public class Medina extends JavaPlugin
{
    private static Medina plugin;

    public Config config;
    public Config messages;

    @Override
    public void onLoad()
    {
        plugin = this;
        config = new Config(this, "config.yml");
        messages = new Config(this, "messages.yml");
    }

    @Override
    public void onEnable()
    {
        config.load();
        messages.load();

        // Metrics @ https://bstats.org/plugin/bukkit/Medina/22026
        Metrics metrics = new Metrics(this, 22026);

        new CommandRegistration();
    }

    public static Medina getPlugin()
    {
        return plugin;
    }

}
