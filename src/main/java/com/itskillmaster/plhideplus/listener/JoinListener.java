package com.itskillmaster.plhideplus.listener;

import com.itskillmaster.plhideplus.PlHidePlus;
import com.itskillmaster.plhideplus.update.UpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PlHidePlus plugin;
    private final UpdateChecker updateChecker;
    private final MiniMessage miniMessage;

    public JoinListener(PlHidePlus plugin) {
        this.plugin = plugin;
        this.updateChecker = plugin.getUpdateChecker();
        this.miniMessage = plugin.getMiniMessage();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("plhideplus.use")) return;
        if (!plugin.getConfigManager().isNotifyOnJoin()) return;
        if (updateChecker == null) return;

        if (updateChecker.isUpdateAvailable()) {
            sendUpdateNotification(player);
        }
    }

    private void sendUpdateNotification(Player player) {
        String latestVersion = updateChecker.getLatestVersion();
        String currentVersion = plugin.getDescription().getVersion();
        String url = "https://github.com/Itskillmaster/plhideplus/releases";

        Component message = miniMessage.deserialize(
                "<gradient:#FF6B6B:#4ECDC4><bold>PlHidePlus Update Available!</bold></gradient>"
        );

        Component details = miniMessage.deserialize(
                "<gray>A new version is available: <green>" + latestVersion + "</green> (current: <red>" + currentVersion + "</red>)</gray>"
        );

        Component clickable = miniMessage.deserialize(
                "<gradient:#4ECDC4:#44A08D><bold>[Click to Download]</bold></gradient>"
        ).clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<yellow>Click to open the GitHub releases page</yellow>")));

        player.sendMessage(message);
        player.sendMessage(details);
        player.sendMessage(clickable);
        player.sendMessage(Component.text(""));
    }
}