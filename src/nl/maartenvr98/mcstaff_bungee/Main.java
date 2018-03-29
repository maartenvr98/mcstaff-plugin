package nl.maartenvr98.mcstaff_bungee;

import net.md_5.bungee.api.plugin.Plugin;

import nl.maartenvr98.mcstaff_bungee.commands.Commands;
import nl.maartenvr98.mcstaff_bungee.config.Config;
import nl.maartenvr98.mcstaff_bungee.listeners.Join;
import nl.maartenvr98.mcstaff_bungee.listeners.Leave;

public class Main extends Plugin {

    private String key;
    private Config config;
    private Connect connection;

    @Override
    public void onEnable() {
        config = new Config(this, "config");
        connection = new Connect(config);

        getProxy().getPluginManager().registerListener(this, new Join(config));
        getProxy().getPluginManager().registerListener(this, new Leave(config));
        getProxy().getPluginManager().registerCommand(this, new Commands(config));

        this.key = config.getConfig().getString("key");
    }

    /**
     * Plugin disabled
     */
    @Override
    public void onDisable() {
        if(connection.connected()) {
            System.out.println("Mcstaff plugin disabled");
        }
    }
}
