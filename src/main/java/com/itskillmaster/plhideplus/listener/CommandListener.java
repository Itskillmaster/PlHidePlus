package com.itskillmaster.plhideplus.listener;

import com.itskillmaster.plhideplus.PlHidePlus;
import com.itskillmaster.plhideplus.config.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {

    private final PlHidePlus plugin;
    private final ConfigManager configManager;
    private final MiniMessage miniMessage;

    public CommandListener(PlHidePlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.miniMessage = plugin.getMiniMessage();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        // فیکس باگ: اگر پلیر OP باشد یا پرمیشن داشته باشد، هیچکدام از دستورات برای او مسدود نمی‌شود
        if (player.isOp() || player.hasPermission("plhideplus.use")) {
            return;
        }

        String message = event.getMessage();
        if (message == null || message.isEmpty()) return;

        String[] parts = message.split(" ", 2);
        String command = parts[0].toLowerCase();

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        if (isBlockedCommand(command)) {
            event.setCancelled(true);
            sendDenyMessage(player, command);
            logBlockAttempt(player, command);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        
        if (sender instanceof Player player) {
            if (player.isOp() || player.hasPermission("plhideplus.use")) return;
            
            String command = event.getCommand().toLowerCase();
            String[] parts = command.split(" ", 2);
            String baseCommand = parts[0];
            
            if (isBlockedCommand(baseCommand)) {
                event.setCancelled(true);
                sendDenyMessage(player, baseCommand);
                logBlockAttempt(player, baseCommand);
            }
        }
    }

    private boolean isBlockedCommand(String command) {
        return configManager.isCommandBlocked(command);
    }

    private void sendDenyMessage(Player player, String command) {
        String formatted = configManager.getFormattedDenyMessage(command);
        player.sendMessage(miniMessage.deserialize(formatted));
    }

    private void logBlockAttempt(Player player, String command) {
        plugin.getLogger().info(String.format("Blocked command '/%s' from player %s", command, player.getName()));
    }
}