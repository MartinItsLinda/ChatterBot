package me.angrypostman.chatterbot;

import me.angrypostman.chatterbot.command.ChatterBotCommand;
import me.angrypostman.chatterbot.listener.AsyncChatListener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ChatterBot extends JavaPlugin {

    private static ChatterBot instance;
    private File botsFile;
    private FileConfiguration bots;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new AsyncChatListener(), this);

        ChatterBotCommand chatterBotCommand = new ChatterBotCommand();

        getCommand("chatterbot").setExecutor(chatterBotCommand);
        getCommand("chatterbot").setTabCompleter(chatterBotCommand);

    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void reloadBotsYML() {
        if (botsFile == null) {
            botsFile = new File(getDataFolder(), "customConfig.yml");
        }
        bots = YamlConfiguration.loadConfiguration(botsFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(this.getResource("bots.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            bots.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCustomConfig() {
        if (botsFile == null) {
            reloadBotsYML();
        }
        return bots;
    }

    public static ChatterBot getInstance() {
        return instance;
    }

}
