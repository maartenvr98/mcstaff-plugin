package nl.maartenvr98.mcstaff_spigot.commands;

import nl.maartenvr98.mcstaff_spigot.Connect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands extends JavaPlugin implements CommandExecutor {

    private Connect connection;
    private FileConfiguration config;

    public Commands() {
        connection = new Connect();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("mcstsff.admin") || p.isOp()) {
                if(label.equalsIgnoreCase("mcstaff")) {
                    switch (args[0]) {
                        case "help":
                        case "?":
                            sendHelp(p);
                            break;
                        case "enable":
                            config.set("enabled", true);
                            saveConfig();
                            connection.createConnection(true, false);
                            successMessage(p, "Mcstaff enabled");
                            break;
                        case "disable":
                            config.set("enabled", false);
                            saveConfig();
                            successMessage(p, "Mcstaff disabled");
                            break;
                        case "reload":
                            reloadConfig();
                            successMessage(p, "Config reloaded");
                            break;
                        case "set":
                            if(args.length == 1) {
                                errorMessage(p, "Invalid arguments. Use 'url' or 'key'");
                            }
                            else if(args.length == 2) {
                                errorMessage(p, "Missing value");
                            }
                            else if(args.length > 2) {
                                if(args[1].equals("url") || args[1].equals("key")) {
                                    setConfig(args[1], args[2]);
                                    successMessage(p, "Item set");
                                }
                                else {
                                    errorMessage(p, "Invalid arguments. Use 'url' or 'key'");
                                }
                            }
                            else {
                                errorMessage(p, "Invalid arguments");
                            }
                            break;
                        default:
                            sendHelp(p);
                    }
                }
            }

        }

        return false;
    }

    /**
     * Reload config
     */
    public void reloadConfig() { reloadConfig(); }

    /**
     * Send error message to player
     *
     * @param player
     * @param text
     */
    private void errorMessage(Player player, String text) {
        player.sendMessage("§c"+text);
    }

    /**
     * Send success message to player
     *
     * @param player
     * @param text
     */
    private void successMessage(Player player, String text) {
        player.sendMessage("§a"+text);
    }

    /**
     * Send line in chat player
     *
     * @param player
     * @param text
     */
    private void sendLine(Player player, String text) {
        player.sendMessage(text);
    }

    /**
     * Set value to config
     *
     * @param param
     * @param value
     */
    private void setConfig(String param, String value) {
        config.set(param,value);
        saveConfig();
    }

    /**
     * Send help message
     *
     * @param player
     */
    private void sendHelp(Player player) {
        sendLine(player, "§8----------§e§lMcstaff§8----------");
        sendLine(player, "§8- §e§l/mcstaff reload §8reload config");
        sendLine(player, "§8- §e§l/mcstaff set <param> <value> §8set net config value");
        sendLine(player, "§8- §e§l/mcstaff help/? §8view help page");
        sendLine(player, "§8- §e§l/mcstaff enable/disable §8enable/disable plugin");
        sendLine(player, "§8---------------------------");
    }

}
