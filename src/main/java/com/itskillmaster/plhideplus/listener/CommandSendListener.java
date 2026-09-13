package com.itskillmaster.plhideplus.listener;

import com.itskillmaster.plhideplus.PlHidePlus;
import com.itskillmaster.plhideplus.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandSendListener implements Listener {

    private final PlHidePlus plugin;
    private final ConfigManager configManager;

    public CommandSendListener(PlHidePlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("plhideplus.use")) {
            return;
        }

        event.getCommands().removeIf(command -> configManager.isCommandBlocked(command));
    }
}