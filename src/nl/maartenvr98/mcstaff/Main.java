package nl.maartenvr98.mcstaff;

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

/**
 * @author maartenvr98
 * @version 1
 */
public class Main extends JavaPlugin implements Listener {

    private Connection connection;
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
        config.addDefault("database.host", "localhost");
        config.addDefault("database.port", 3306);
        config.addDefault("database.username", "MySecretUsername");
        config.addDefault("database.password", "MySecretPassword");
        config.addDefault("database.database", "players");
        config.options().copyDefaults(true);
        saveConfig();

        System.out.println("Mcstaff plugin enabled");
        this.getServer().getPluginManager().registerEvents(this, this);

        this.host = config.getString("database.host");
        this.port = config.getInt("database.port");
        this.database = config.getString("database.database");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");

        try {
            this.openConnection();
            this.connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Plugin disabled
     */
    @Override
    public void onDisable() {
        System.out.println("Mcstaff plugin disabled");
        try {
            Connection connection = this.connection;
            if(connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                        "ON DUPLICATE KEY UPDATE lastlogin = values(lastlogin), lastip = values(lastip)");
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
}