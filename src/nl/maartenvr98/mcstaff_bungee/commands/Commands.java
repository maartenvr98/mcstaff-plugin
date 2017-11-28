package nl.maartenvr98.mcstaff_bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.maartenvr98.mcstaff_bungee.Connect;
import nl.maartenvr98.mcstaff_bungee.config.Config;

public class Commands extends Command {

    private final Config config;
    private final Connect connection;

    public Commands(Config config) {
        super("mcstaff", "mcstaff.reload");
        this.config = config;
        this.connection = new Connect(config);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)sender;
        if(args.length == 0) {
            sendHelp(p);
        }
        else {
            switch (args[0]) {
                case "help":
                case "?":
                    sendHelp(p);
                    break;
                case "enable":
                    config.getConfig().set("enabled", true);
                    config.saveConfig();
                    connection.createConnection(true, false);
                    successMessage(p, "Mcstaff enabled");
                    break;
                case "disable":
                    config.getConfig().set("enabled", false);
                    config.saveConfig();
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

    /**
     * Reload config
     */
    private void reloadConfig() { config.reloadConfig(); }

    /**
     * Send error message to player
     *
     * @param player
     * @param text
     */
    private void errorMessage(ProxiedPlayer player, String text) {
        TextComponent message = new TextComponent(text);
        message.setColor(ChatColor.RED);
        player.sendMessage(message);
    }

    /**
     * Send success message to player
     *
     * @param player
     * @param text
     */
    private void successMessage(ProxiedPlayer player, String text) {
        TextComponent message = new TextComponent(text);
        message.setColor(ChatColor.GREEN);
        player.sendMessage(message);
    }

    /**
     * Send line in chat player
     *
     * @param player
     * @param text
     */
    private void sendLine(ProxiedPlayer player, String text) {
        TextComponent message = new TextComponent(text);
        player.sendMessage(message);
    }

    /**
     * Set value to config
     *
     * @param param
     * @param value
     */
    private void setConfig(String param, String value) {
        config.getConfig().set(param, value);
        config.saveConfig();
    }

    /**
     * Send help message
     *
     * @param player
     */
    private void sendHelp(ProxiedPlayer player) {
        sendLine(player, "§8----------§e§lMcstaff§8----------");
        sendLine(player, "§8- §e§l/mcstaff reload §8reload config");
        sendLine(player, "§8- §e§l/mcstaff set <param> <value> §8set net config value");
        sendLine(player, "§8- §e§l/mcstaff help/? §8view help page");
        sendLine(player, "§8- §e§l/mcstaff enable/disable §8enable/disable plugin");
        sendLine(player, "§8---------------------------");
    }
}
