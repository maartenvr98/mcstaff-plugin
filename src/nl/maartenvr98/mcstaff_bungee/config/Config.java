package nl.maartenvr98.mcstaff_bungee.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class Config {

    private Configuration configuration;
    private File file;
    private String name;

    /**
     *
     * @param plugin
     * @param name
     */
    public Config(Plugin plugin, String name) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream is = plugin.getResourceAsStream(name + ".yml");
                     OutputStream os = new FileOutputStream(file)) {
                    ByteStreams.copy(is, os);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        configuration.set("enabled", true);
                        configuration.set("url", "http://www.example.com");
                        configuration.set("key", "api_key");
                        saveConfig();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return the config file
     */
    public Configuration getConfig() {
        return configuration;
    }

    /**
     * Save the config
     */
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * reload the config
     */
    public void reloadConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}