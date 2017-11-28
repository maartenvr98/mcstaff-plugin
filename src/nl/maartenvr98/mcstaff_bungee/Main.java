package nl.maartenvr98.mcstaff_bungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import nl.maartenvr98.mcstaff_bungee.commands.Commands;
import nl.maartenvr98.mcstaff_bungee.commands.SetCommand;
import nl.maartenvr98.mcstaff_bungee.config.Config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends Plugin implements Listener {

    private boolean enabled;
    private String url;
    private String key;
    private Config config;

    /**
     * Plugin enabled
     * Create config with defaults
     * Register events
     * Check if key is valid
     */
    @Override
    public void onEnable() {
        config = new Config(this, "config");

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new Commands(config));


        this.enabled = config.getConfig().getBoolean("enabled");
        this.url = config.getConfig().getString("url");
        this.key = config.getConfig().getString("key");

        if(this.enabled) {
            try {
                String result = executePost("connect", "key="+this.key);

                Gson gson = new Gson();
                JsonObject jsonResult = gson.fromJson(result, JsonObject.class);

                String status = jsonResult.get("status").getAsString();
                if(!status.equals("granted")) {
                    this.enabled = false;
                    SendMessage("Plugin disabled due incorrect key");
                }else {
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
        if(this.enabled) {
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
        if(this.enabled) {
            ProxiedPlayer p = event.getPlayer();

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
    }

    /**
     * Player quit event
     * Send request to webserver with leave event
     *
     * @param event
     */
    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        if(this.enabled) {
            ProxiedPlayer p = event.getPlayer();

            try {
                executePost("addevent", "key="+this.key+"" +
                        "&uuid="+p.getUniqueId().toString().replaceAll("-", "")+"" +
                        "&type=leave");

                System.out.println("Leave action saved for " + p.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send post http post request to webserver
     *
     * @param targetURL
     * @param urlParameters
     * @return
     * @throws IOException
     */
    private String executePost(String targetURL, String urlParameters) throws IOException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(this.url + "/api/v1/" + targetURL);
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
