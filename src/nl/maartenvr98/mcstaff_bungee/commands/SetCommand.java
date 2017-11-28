package nl.maartenvr98.mcstaff_bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.maartenvr98.mcstaff_bungee.config.Config;

public class SetCommand extends net.md_5.bungee.api.plugin.Command {

    private final Config config;

    public SetCommand(Config config) {
        super("mcstaff", "mcstaff.set");
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {

        config.getConfig().set("api_key", "ff");
    }

}
