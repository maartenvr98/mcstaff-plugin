package nl.maartenvr98.mcstaff_bungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends Plugin {

    private boolean enabled;
    private String url;
    private String key;
    private BungeeConfig config;

    /**
     * Plugin enabled
     * Create config with defaults
     * Register events
     * Check if key is valid
     */
    @Override
    public void onEnable() {
        config = new BungeeConfig(this, "config");

        config.getConfig().set("enabled", true);
        config.getConfig().set("url", "http://www.example.com");
        config.getConfig().set("key", "api_key");
        config.saveConfig();

        System.out.println("Mcstaff plugin enabled");

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

                    System.out.println("----------------Mcstaff-----------------\n");
                    System.out.println("Plugin disabled due incorrect key\n");
                    System.out.println("----------------------------------------");
                }
            } catch (IOException e) {
                this.enabled = false;
                System.out.println("----------------Mcstaff-----------------\n");
                System.out.println("Could not connect to REST service\n");
                System.out.println("----------------------------------------");
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
    public void onLogin(LoginEvent event) {
        if(this.enabled) {
            // TODO: Execute posts to REST service
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
            // TODO: Execute posts to REST service
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

}
