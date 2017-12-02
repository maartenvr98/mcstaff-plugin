package nl.maartenvr98.mcstaff_bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.maartenvr98.mcstaff_bungee.Connect;
import nl.maartenvr98.mcstaff_bungee.config.Config;

import java.io.IOException;

public class Leave implements Listener{

    private Config config;
    private Connect connection;
    private String key;

    public Leave(Config config) {
        this.config = config;
        this.connection = new Connect(config);

        this.key = config.getConfig().getString("key");
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
