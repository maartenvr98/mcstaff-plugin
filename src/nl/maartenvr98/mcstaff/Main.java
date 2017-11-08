package nl.maartenvr98.mcstaff;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

//TODO: Post request to api instead of database

/**
 * @author maartenvr98
 * @version 1
 */
public class Main extends JavaPlugin implements Listener {

    private Connection connection;
    private String url;
    private String key;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    private FileConfiguration config = getConfig();

    /**
     * Plugin enabled
     */
    @Override
    public void onEnable() {
        config.addDefault("enabled", true);
        config.addDefault("url", "https://www.example.com");
        config.addDefault("key" , "api_key");
        config.addDefault("database.host", "localhost");
        config.addDefault("database.port", 3306);
        config.addDefault("database.username", "MySecretUsername");
        config.addDefault("database.password", "MySecretPassword");
        config.addDefault("database.database", "players");
        config.options().copyDefaults(true);
        saveConfig();

        System.out.println("Mcstaff plugin enabled");
        this.getServer().getPluginManager().registerEvents(this, this);

        this.url = config.getString("url");
        this.key = config.getString("key");
        this.host = config.getString("database.host");
        this.port = config.getInt("database.port");
        this.database = config.getString("database.database");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");
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
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!config.getBoolean("enabled")) {
            return;
        }
        Player p = event.getPlayer();

        try {
            this.openConnection();
            Statement statement = this.connection.createStatement();

            DateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();

            try {
                statement.executeUpdate("INSERT INTO players (name, uuid, lastlogin, lastip) " +
                        "VALUES ('" + p.getName() + "', '" + p.getUniqueId().toString().replaceAll("-", "") + "', '" + fullFormat.format(date) + "', '" + p.getAddress().getHostString() + "') " +
                        "ON DUPLICATE KEY UPDATE name = values(name), lastlogin = values(lastlogin), lastip = values(lastip)");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                statement.executeUpdate("INSERT INTO players_events (uuid, type, date, time) " +
                        "VALUES ('" + p.getUniqueId().toString().replaceAll("-", "") + "', 'join', '" + dateFormat.format(date) + "', '" + timeFormat.format(date) + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Join action saved for" + p.getName());
            this.connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Player quit event
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!config.getBoolean("enabled")) {
            return;
        }
        Player p = event.getPlayer();

        try {
            this.openConnection();
            Statement statement = this.connection.createStatement();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();

            try {
                statement.executeUpdate("INSERT INTO players_events (uuid, type, date, time) " +
                        "VALUES ('" + p.getUniqueId().toString().replaceAll("-", "") + "', 'leave', '" + dateFormat.format(date) + "', '" + timeFormat.format(date) + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Leave action saved for " + p.getName());
            this.connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to MySQL database
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void openConnection() throws SQLException, ClassNotFoundException {
        if (this.connection == null || this.connection.isClosed()) {
            synchronized(this) {
                if (this.connection == null || this.connection.isClosed()) {
                    Class.forName("com.mysql.jdbc.Driver");
                    this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
                }
            }
        }
    }

    /**
     * Send post request
     *
     * @param targetURL
     * @param urlParameters
     * @return
     */
    private String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(this.url + targetURL);
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
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
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