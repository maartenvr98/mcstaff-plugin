package nl.maartenvr98.mcstaff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    @Override
    public void onEnable() {
        System.out.println("Plugin enabled");
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        this.host = "80.82.222.241";
        this.port = 3306;
        this.database = "test_java";
        this.username = "java";
        this.password = "bHts0~06";

        try {
            this.openConnection();
            Statement statement = this.connection.createStatement();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();

            statement.executeUpdate("INSERT INTO players_events (uuid, type, date, time) VALUES ('" + p.getUniqueId().toString() + "', 'join', '" + dateFormat.format(date) + "', '" + timeFormat.format(date) + "');");

            System.out.println("Login action saved for" + p.getName());
            this.connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

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