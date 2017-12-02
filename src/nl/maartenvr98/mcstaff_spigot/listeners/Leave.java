package nl.maartenvr98.mcstaff_spigot.listeners;

import nl.maartenvr98.mcstaff_spigot.Connect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class Leave implements Listener{

    private Connect connection;
    private FileConfiguration config;
    private String key;

    public Leave() {
        connection = new Connect();
        this.key = config.getString("key");
    }

    /**
     * Player quit event
     * Send request to webserver with leave event
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(connection.connected()) {
            return;
        }
        Player p = event.getPlayer();

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
