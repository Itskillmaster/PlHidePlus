package com.itskillmaster.plhideplus.config;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private FileConfiguration config;
    private File configFile;

    private List<String> blockedCommands;
    private String denyMessage;
    private int guiSize;
    private String guiTitle;
    private boolean updateCheckerEnabled;
    private String updateRepository;
    private boolean notifyOnJoin;
    private int updateCheckInterval;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void load() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        parseConfig();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        parseConfig();
    }

    public void save() {
        config.set("blocked-commands", blockedCommands);
        config.set("deny-message", denyMessage);
        config.set("gui.size", guiSize);
        config.set("gui.title", guiTitle);
        config.set("update-checker.enabled", updateCheckerEnabled);
        config.set("update-checker.repository", updateRepository);
        config.set("update-checker.notify-on-join", notifyOnJoin);
        config.set("update-checker.check-interval", updateCheckInterval);

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config.yml: " + e.getMessage());
        }
    }

    private void parseConfig() {
        List<String> rawCommands = config.getStringList("blocked-commands");
        blockedCommands = rawCommands.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        denyMessage = config.getString("deny-message", "<red>Command blocked!</red>");
        guiSize = config.getInt("gui.size", 54);
        guiTitle = config.getString("gui.title", "<gradient:#FF6B6B:#4ECDC4><bold>PlHidePlus Control Panel</bold></gradient>");

        updateCheckerEnabled = config.getBoolean("update-checker.enabled", true);
        updateRepository = config.getString("update-checker.repository", "Itskillmaster/plhideplus");
        notifyOnJoin = config.getBoolean("update-checker.notify-on-join", true);
        updateCheckInterval = config.getInt("update-checker.check-interval", 60);
    }

    public List<String> getBlockedCommands() {
        return new ArrayList<>(blockedCommands);
    }

    public void setBlockedCommands(List<String> commands) {
        this.blockedCommands = commands.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        save();
    }

    public void addBlockedCommand(String command) {
        String lower = command.toLowerCase();
        if (!blockedCommands.contains(lower)) {
            blockedCommands.add(lower);
            save();
        }
    }

    public void removeBlockedCommand(String command) {
        blockedCommands.remove(command.toLowerCase());
        save();
    }

    public boolean isCommandBlocked(String command) {
        // فیکس باگ: فقط دستورات دقیق مسدود می‌شوند تا دستوراتی مثل /plhideplus دچار مشکل نشوند
        return blockedCommands.contains(command.toLowerCase());
    }

    public String getDenyMessage() {
        return denyMessage;
    }

    public void setDenyMessage(String message) {
        this.denyMessage = message;
        save();
    }

    public String getFormattedDenyMessage(String command) {
        return denyMessage.replace("{command}", command);
    }

    public int getGuiSize() {
        return guiSize;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public boolean isUpdateCheckerEnabled() {
        return updateCheckerEnabled;
    }

    public String getUpdateRepository() {
        return updateRepository;
    }

    public boolean isNotifyOnJoin() {
        return notifyOnJoin;
    }

    public int getUpdateCheckInterval() {
        return updateCheckInterval;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}