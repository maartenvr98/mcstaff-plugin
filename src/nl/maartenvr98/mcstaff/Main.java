package nl.maartenvr98.mcstaff;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jdk.nashorn.internal.parser.JSONParser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

//TODO: Post request to api instead of database

/**
 * @author maartenvr98
 * @version 1
 */
public class Main extends JavaPlugin implements Listener {

    private String key;
    private boolean enabled;

    private FileConfiguration config = getConfig();

    /**
     * Plugin enabled
     * Create config with defaults
     * Register events
     * Check if key is valid
     */
    @Override
    public void onEnable() {
        config.addDefault("enabled", true);
        config.addDefault("url", "http://www.example.com");
        config.addDefault("key" , "api_key");
        config.options().copyDefaults(true);
        saveConfig();

        System.out.println("Mcstaff plugin enabled");
        this.getServer().getPluginManager().registerEvents(this, this);

        this.key = config.getString("key");
        this.enabled = config.getBoolean("enabled");

        if(this.enabled) {
            String result = executePost("connect", "key="+this.key);
            //TODO: Check if key from config is correct
        }
    }

    /**
     * Plugin disabled6
     */
    @Override
    public void onDisable() {
        System.out.println("Mcstaff plugin disabled");
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
        if(!this.enabled) {
            return;
        }
        Player p = event.getPlayer();

        executePost("addplayer", "key="+this.key+"" +
                "&name="+p.getName()+"" +
                "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                "&lastip="+p.getAddress().getHostString());

        executePost("addevent", "key="+this.key+"" +
                "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                "&type=join");

        System.out.println("Join action saved for" + p.getName());
    }

    /**
     * Player quit event
     * Send request to webserver with leave event
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!this.enabled) {
            return;
        }
        Player p = event.getPlayer();

        executePost("addevent", "key="+this.key+"" +
                "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                "&type=leave");

        System.out.println("Leave action saved for " + p.getName());
    }

    /**
     * Send post http post request to webserver
     *
     * @param targetURL
     * @param urlParameters
     * @return
     */
    private String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(config.getString("url") + "/api/v1/" + targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}