package nl.maartenvr98.mcstaff_spigot;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

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

        this.getServer().getPluginManager().registerEvents(this, this);

        this.key = config.getString("key");
        this.enabled = config.getBoolean("enabled");

        if(this.enabled) {
            try {
                String result = executePost("connect", "key="+this.key);

                Gson gson = new Gson();
                JsonObject jsonResult = gson.fromJson(result, JsonObject.class);

                String status = jsonResult.get("status").getAsString();
                if(!status.equals("granted")) {
                    this.enabled = false;
                    SendMessage("Plugin disabled due incorrect key");
                } else {
                    System.out.println("Mcstaff plugin enabled");
                }
            } catch (IOException e) {
                this.enabled = false;
                SendMessage("Could not connect to REST service");
            }
        }
    }

    /**
     * Plugin disabled
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
        p.getDisplayName();

        try {
            executePost("addplayer", "key="+this.key+"" +
                    "&name="+p.getName()+"" +
                    "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                    "&lastip="+p.getAddress().getHostString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            executePost("addevent", "key="+this.key+"" +
                    "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                    "&type=join");

            System.out.println("Join action saved for" + p.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        try {
            executePost("addevent", "key="+this.key+"" +
                    "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                    "&type=leave");

            System.out.println("Leave action saved for " + p.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send post http post request to webserver
     *
     * @param targetURL
     * @param urlParameters
     * @return
     */
    private String executePost(String targetURL, String urlParameters) throws IOException {
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
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void SendMessage(String message) {
        System.out.println("----------------Mcstaff-----------------");
        System.out.println(" ");
        System.out.println(message);
        System.out.println(" ");
        System.out.println("----------------------------------------");
    }
}