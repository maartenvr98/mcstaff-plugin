package nl.maartenvr98.mcstaff_spigot;

import nl.maartenvr98.mcstaff_spigot.commands.Commands;
import nl.maartenvr98.mcstaff_spigot.listeners.Join;
import nl.maartenvr98.mcstaff_spigot.listeners.Leave;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author maartenvr98
 * @version 1
 */
public class Main extends JavaPlugin {

    private FileConfiguration config;
    private Connect connection;

    /**
     * Plugin enabled
     * Create config with defaults
     * Register events
     */
    @Override
    public void onEnable() {
        config.addDefault("enabled", true);
        config.addDefault("url", "http://www.example.com");
        config.addDefault("key" , "api_key");
        config.options().copyDefaults(true);
        saveConfig();

        connection = new Connect();

        this.getServer().getPluginManager().registerEvents(new Join(), this);
        this.getServer().getPluginManager().registerEvents(new Leave(), this);
        this.getCommand("kit").setExecutor(new Commands());
    }

    /**
     * Plugin disabled
     */
    @Override
    public void onDisable() {
        System.out.println("Mcstaff plugin disabled");
    }
}