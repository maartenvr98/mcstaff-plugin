package nl.maartenvr98.mcstaff_bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import nl.maartenvr98.mcstaff_bungee.config.Config;

public class Commands extends Command {

    private final Config config;

    public Commands(Config config) {
        super("mcstaff", "mcstaff.reload");
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)sender;
        if(args.length == 0) {
            p.sendMessage(new TextComponent("Invalid arguments"));
        }
        else {
            if(args[0].equals("reload")) {
                reload();
            }
            else if(args[0].equals("set")) {
                if(args.length == 3) {
                    config.getConfig().set(args[1], args[2]);
                    config.saveConfig();
                    p.sendMessage(new TextComponent("Item set"));
                }
                else {
                    p.sendMessage(new TextComponent("Invalid arguments"));
                }
            }
        }
    }

    private void reload() {
        config.reloadConfig();
    }

    private void errorMessage(ProxiedPlayer player, String text) {
        TextComponent message = new TextComponent(text);
        message.setColor(ChatColor.RED);
        player.sendMessage(message);
    }
}
