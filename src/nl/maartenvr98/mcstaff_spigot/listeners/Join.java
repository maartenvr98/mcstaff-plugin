package nl.maartenvr98.mcstaff_spigot.listeners;

import nl.maartenvr98.mcstaff_spigot.Connect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class Join implements Listener {

    private Connect connection;
    private FileConfiguration config;
    private String key;

    public Join() {
        connection = new Connect();
        this.key = config.getString("key");
    }

    /**
     * Player join event
     * Add player in database. Checking if not exsist will handled in the api
     * Send request to webserver with join event
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(connection.connected()) {
            return;
        }
        Player p = event.getPlayer();
        p.getDisplayName();

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

            System.out.println("Join action saved for" + p.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
