package dev.plex.medina;

import dev.plex.medina.config.Config;
import dev.plex.medina.registration.CommandRegistration;
import dev.plex.medina.storage.SQLConnection;
import dev.plex.medina.storage.SQLReports;
import dev.plex.medina.util.MedinaUtils;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;


public class Medina extends JavaPlugin
{
    @Getter
    private static Medina plugin;

    public Config config;
    public Config messages;

    @Getter
    private SQLConnection sqlConnection;

    @Getter
    private SQLReports sqlReports;

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

        sqlConnection = new SQLConnection();
        MedinaUtils.testConnection();

        sqlReports = new SQLReports();

        new CommandRegistration();
    }
}
