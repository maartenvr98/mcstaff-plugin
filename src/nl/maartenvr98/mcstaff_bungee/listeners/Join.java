package nl.maartenvr98.mcstaff_bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.maartenvr98.mcstaff_bungee.Connect;
import nl.maartenvr98.mcstaff_bungee.config.Config;

import java.io.IOException;

public class Join implements Listener {

    private final Config config;
    private final Connect connection;
    private String key;

    public Join(Config config) {
        this.config = config;
        this.connection = new Connect(config);

        this.key = config.getConfig().getString("key");
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

}
