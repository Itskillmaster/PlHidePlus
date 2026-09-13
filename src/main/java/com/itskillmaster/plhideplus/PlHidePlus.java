package com.itskillmaster.plhideplus;

import com.itskillmaster.plhideplus.config.ConfigManager;
import com.itskillmaster.plhideplus.gui.GuiListener;
import com.itskillmaster.plhideplus.gui.GuiManager;
import com.itskillmaster.plhideplus.listener.CommandListener;
import com.itskillmaster.plhideplus.listener.CommandSendListener;
import com.itskillmaster.plhideplus.listener.JoinListener;
import com.itskillmaster.plhideplus.listener.TabCompleteListener;
import com.itskillmaster.plhideplus.update.UpdateChecker;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PlHidePlus extends JavaPlugin {

    private static PlHidePlus instance;
    private ConfigManager configManager;
    private GuiManager guiManager;
    private UpdateChecker updateChecker;
    private MiniMessage miniMessage;

    @Override
    public void onEnable() {
        instance = this;
        miniMessage = MiniMessage.miniMessage();

        saveDefaultConfig();
        reloadConfig();

        configManager = new ConfigManager(this);
        configManager.load();

        guiManager = new GuiManager(this);

        getCommand("plhideplus").setExecutor(new PlHidePlusCommand());

        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandSendListener(this), this);
        
        // ثبت Listener مربوط به رابط کاربری (جلوگیری از برداشتن آیتم‌ها)
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        if (configManager.isUpdateCheckerEnabled()) {
            updateChecker = new UpdateChecker(this);
            updateChecker.checkForUpdates();
            if (configManager.getUpdateCheckInterval() > 0) {
                startUpdateTask();
            }
        }

        log("&aPlHidePlus has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        log("&cPlHidePlus has been disabled!");
        instance = null;
    }

    private void startUpdateTask() {
        long interval = configManager.getUpdateCheckInterval() * 60 * 20L;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (updateChecker != null) {
                updateChecker.checkForUpdates();
            }
        }, interval, interval);
    }

    public static PlHidePlus getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public void log(String message) {
        getLogger().info(colorize(message));
    }

    public void log(Level level, String message) {
        getLogger().log(level, colorize(message));
    }

    private String colorize(String message) {
        return miniMessage.serialize(miniMessage.deserialize(message));
    }

    private class PlHidePlusCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            
            if (!sender.isOp() && !sender.hasPermission("plhideplus.use")) {
                sender.sendMessage(miniMessage.deserialize("<red><bold>⛔ Access Denied</bold></red> <dark_gray>»</dark_gray> <gray>You do not have permission to use this command!</gray>"));
                return true;
            }

            // اگر فقط دستور /plhideplus بدون آرگومان زده شود، منو باز می‌شود
            if (args.length == 0) {
                if (sender instanceof Player player) {
                    guiManager.openMainGui(player);
                } else {
                    sender.sendMessage(miniMessage.deserialize("<red>Console must use subcommands (add, remove, reload, info).</red>"));
                }
                return true;
            }

            // مدیریت ساب‌کامندها
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "add":
                    if (args.length < 2) {
                        sender.sendMessage(miniMessage.deserialize("<red>Usage: /plhideplus add <command></red>"));
                        return true;
                    }
                    String cmdToAdd = args[1].toLowerCase().replace("/", "");
                    if (configManager.isCommandBlocked(cmdToAdd)) {
                        sender.sendMessage(miniMessage.deserialize("<red>Command <white>/" + cmdToAdd + "</white> is already blocked!</red>"));
                    } else {
                        configManager.addBlockedCommand(cmdToAdd);
                        sender.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Added command: <white>/" + cmdToAdd + "</white></gray>"));
                    }
                    break;
                    
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage(miniMessage.deserialize("<red>Usage: /plhideplus remove <command></red>"));
                        return true;
                    }
                    String cmdToRemove = args[1].toLowerCase().replace("/", "");
                    if (!configManager.isCommandBlocked(cmdToRemove)) {
                        sender.sendMessage(miniMessage.deserialize("<red>Command <white>/" + cmdToRemove + "</white> is not blocked!</red>"));
                    } else {
                        configManager.removeBlockedCommand(cmdToRemove);
                        sender.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Removed command: <white>/" + cmdToRemove + "</white></gray>"));
                    }
                    break;
                    
                case "reload":
                    configManager.reload();
                    sender.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Configuration reloaded successfully!</gray>"));
                    break;
                    
                case "info":
                    sender.sendMessage(miniMessage.deserialize("<gradient:#6BCB77:#4ECDC4><bold>PlHidePlus Information</bold></gradient>"));
                    sender.sendMessage(miniMessage.deserialize("<gray>Version: <white>" + getDescription().getVersion() + "</white></gray>"));
                    sender.sendMessage(miniMessage.deserialize("<gray>Blocked commands: <white>" + configManager.getBlockedCommands().size() + "</white></gray>"));
                    break;
                    
                default:
                    sender.sendMessage(miniMessage.deserialize("<red>Unknown subcommand. Available: add, remove, reload, info</red>"));
                    break;
            }
            return true;
        }
    }
}