package com.itskillmaster.plhideplus.listener;

import com.itskillmaster.plhideplus.PlHidePlus;
import com.itskillmaster.plhideplus.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteListener implements Listener {

    private final PlHidePlus plugin;
    private final ConfigManager configManager;

    public TabCompleteListener(PlHidePlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player player)) return;

        // فیکس باگ: ادمین‌ها می‌توانند به راحتی از تب کامپلیت استفاده کنند
        if (player.isOp() || player.hasPermission("plhideplus.use")) {
            return;
        }

        String buffer = event.getBuffer().toLowerCase();
        List<String> completions = event.getCompletions();

        List<String> filtered = completions.stream()
                .filter(completion -> !isBlockedCompletion(buffer, completion))
                .collect(Collectors.toList());

        event.setCompletions(filtered);
    }

    private boolean isBlockedCompletion(String buffer, String completion) {
        String fullCommand = buffer + completion;
        fullCommand = fullCommand.toLowerCase();

        if (fullCommand.startsWith("/")) {
            fullCommand = fullCommand.substring(1);
        }

        String[] parts = fullCommand.split(" ", 2);
        String command = parts[0];

        return configManager.isCommandBlocked(command);
    }
}