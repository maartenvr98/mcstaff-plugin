package nl.maartenvr98.mcstaff_bungee;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.event.EventHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends Plugin {

    private Plugin plugin;
    private String key;
    private String url;
    private boolean enabled;

    /**
     * Plugin enabled
     * Create config with defaults
     * Register events
     * Check if key is valid
     */
    @Override
    public void onEnable() {
        System.out.println("Mcstaff plugin enabled");
        createConfig();

        this.url = "http://www.example.com/api";
    }

    /**
     * Plugin disabled
     */
    @Override
    public void onDisable() {
        System.out.println("Mcstaff plugin disabled");
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {

    }

    /**
     * Create config file to save plugin variables
     */
    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                //saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

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
