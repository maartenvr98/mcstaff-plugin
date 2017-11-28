package nl.maartenvr98.mcstaff_bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import nl.maartenvr98.mcstaff_bungee.commands.Commands;
import nl.maartenvr98.mcstaff_bungee.config.Config;

import java.io.*;

public class Main extends Plugin implements Listener {

    private String key;
    private Config config;
    private Connect connection;

    @Override
    public void onEnable() {
        config = new Config(this, "config");
        connection = new Connect(config);

        getProxy().getPluginManager().registerListener(this, this);
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

    /**
     * Player join event
     * Add player in database. Checking if not exsist will handled in the api
     * Send request to webserver with join event
     *
     * @param event
     */
    @EventHandler
    public void onLogin(PostLoginEvent event) {
        if(connection.connected()) {
            ProxiedPlayer p = event.getPlayer();

            try {
                connection.executePost("addplayer", "key="+this.key+"" +
                        "&name="+p.getName()+"" +
                        "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                        "&lastip="+p.getAddress().getHostString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.executePost("addevent", "key="+this.key+"" +
                        "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                        "&type=join");

                System.out.println("Join action saved for " + p.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Player quit event
     * Send request to webserver with leave event
     *
     * @param event
     */
    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        if(connection.connected()) {
            ProxiedPlayer p = event.getPlayer();

            try {
                connection.executePost("addevent", "key="+this.key+"" +
                        "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                        "&type=leave");

                System.out.println("Leave action saved for " + p.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
