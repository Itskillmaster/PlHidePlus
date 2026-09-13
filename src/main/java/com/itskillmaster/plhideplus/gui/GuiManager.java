package com.itskillmaster.plhideplus.gui;

import com.itskillmaster.plhideplus.PlHidePlus;
import com.itskillmaster.plhideplus.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private final PlHidePlus plugin;
    private final ConfigManager configManager;
    private final MiniMessage miniMessage;

    public GuiManager(PlHidePlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.miniMessage = plugin.getMiniMessage();
    }

    public void openMainGui(Player player) {
        Inventory gui = Bukkit.createInventory(new GuiHolder(GuiType.MAIN), configManager.getGuiSize(),
                miniMessage.deserialize(configManager.getGuiTitle()));

        fillBackground(gui);
        setMainItems(gui, player);
        player.openInventory(gui);
    }

    public void openCommandsGui(Player player) {
        Inventory gui = Bukkit.createInventory(new GuiHolder(GuiType.COMMANDS), 54,
                miniMessage.deserialize("<gradient:#FF6B6B:#EE5A5A><bold>Blocked Commands Manager</bold></gradient>"));

        fillBackground(gui);
        setCommandItems(gui, player);
        player.openInventory(gui);
    }

    public void openDenyMessageGui(Player player) {
        Inventory gui = Bukkit.createInventory(new GuiHolder(GuiType.DENY_MESSAGE), 54,
                miniMessage.deserialize("<gradient:#4ECDC4:#44A08D><bold>Deny Message Editor</bold></gradient>"));

        fillBackground(gui);
        setDenyMessageItems(gui, player);
        player.openInventory(gui);
    }

    private void fillBackground(Inventory gui) {
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", List.of());
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, glass);
            }
        }
    }

    private void setMainItems(Inventory gui, Player player) {
        List<String> blocked = configManager.getBlockedCommands();
        String preview = configManager.getDenyMessage().length() > 40
                ? configManager.getDenyMessage().substring(0, 40) + "..."
                : configManager.getDenyMessage();

        gui.setItem(11, createItem(Material.COMMAND_BLOCK,
                "<gradient:#FF6B6B:#EE5A5A><bold>Blocked Commands</bold></gradient>",
                List.of(
                        "<gray>View and manage blocked commands</gray>",
                        "",
                        "<yellow>Left-click:</yellow> <white>View list</white>",
                        "<yellow>Right-click:</yellow> <white>Add new command</white>",
                        "",
                        "<green>Currently blocking: <white>" + blocked.size() + " commands</white></green>"
                )));

        gui.setItem(13, createItem(Material.PAPER,
                "<gradient:#4ECDC4:#44A08D><bold>Deny Message</bold></gradient>",
                List.of(
                        "<gray>Edit the message shown when</gray>",
                        "<gray>a blocked command is used</gray>",
                        "",
                        "<yellow>Left-click:</yellow> <white>View current message</white>",
                        "<yellow>Right-click:</yellow> <white>Edit message</white>",
                        "",
                        "<gray>Preview: <white>" + miniMessage.serialize(miniMessage.deserialize(preview)) + "</white></gray>"
                )));

        gui.setItem(15, createItem(Material.REDSTONE,
                "<gradient:#FFD93D:#FF6B6B><bold>Reload Configuration</bold></gradient>",
                List.of(
                        "<gray>Reload all settings from config.yml</gray>",
                        "",
                        "<yellow>Click to reload</yellow>"
                )));

        gui.setItem(31, createItem(Material.ENCHANTED_BOOK,
                "<gradient:#6BCB77:#4ECDC4><bold>Plugin Information</bold></gradient>",
                List.of(
                        "<gray>Version: <white>" + plugin.getDescription().getVersion() + "</white></gray>",
                        "<gray>Author: <white>Itskillmaster</white></gray>",
                        "<gray>GitHub: <white>github.com/Itskillmaster/plhideplus</white></gray>",
                        "",
                        "<green>Status: <white>Running</white></green>"
                )));
    }

    private void setCommandItems(Inventory gui, Player player) {
        List<String> blocked = configManager.getBlockedCommands();

        gui.setItem(4, createItem(Material.ARROW,
                "<gradient:#FF6B6B:#EE5A5A><bold>Back to Main Menu</bold></gradient>",
                List.of("<yellow>Click to return</yellow>")));

        gui.setItem(8, createItem(Material.PAPER,
                "<gradient:#4ECDC4:#44A08D><bold>Add New Command</bold></gradient>",
                List.of(
                        "<gray>Click to add a new blocked command</gray>",
                        "<gray>You will be prompted in chat</gray>",
                        "",
                        "<yellow>Click to start</yellow>"
                )));

        int slot = 19;
        for (String cmd : blocked) {
            if (slot > 43) break;
            gui.setItem(slot, createItem(Material.COMMAND_BLOCK,
                    "<white>/" + cmd + "</white>",
                    List.of(
                            "<gray>Click to remove this command</gray>",
                            "<red>Right-click to confirm removal</red>"
                    )));
            slot++;
            if (slot % 9 == 8) slot += 2;
        }
    }

    private void setDenyMessageItems(Inventory gui, Player player) {
        gui.setItem(4, createItem(Material.ARROW,
                "<gradient:#FF6B6B:#EE5A5A><bold>Back to Main Menu</bold></gradient>",
                List.of("<yellow>Click to return</yellow>")));

        gui.setItem(8, createItem(Material.WRITABLE_BOOK,
                "<gradient:#4ECDC4:#44A08D><bold>Edit Deny Message</bold></gradient>",
                List.of(
                        "<gray>Click to edit the deny message</gray>",
                        "<gray>Supports MiniMessage format</gray>",
                        "",
                        "<yellow>Click to start editing</yellow>"
                )));

        String current = configManager.getDenyMessage();
        gui.setItem(22, createItem(Material.PAPER,
                "<gradient:#6BCB77:#4ECDC4><bold>Current Message</bold></gradient>",
                List.of(
                        "<gray>Current deny message:</gray>",
                        "",
                        "<white>" + miniMessage.serialize(miniMessage.deserialize(current)) + "</white>",
                        "",
                        "<gray>Placeholders: <white>{command}</white></gray>"
                )));
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(miniMessage.deserialize(name));
        List<Component> loreComponents = new ArrayList<>();
        for (String line : lore) {
            loreComponents.add(miniMessage.deserialize(line));
        }
        meta.lore(loreComponents);
        item.setItemMeta(meta);
        return item;
    }

    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof GuiHolder holder)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getRawSlot();
        GuiType type = holder.getType();

        switch (type) {
            case MAIN -> handleMainClick(player, slot, event.isRightClick());
            case COMMANDS -> handleCommandsClick(player, slot, event.isRightClick());
            case DENY_MESSAGE -> handleDenyMessageClick(player, slot);
        }
    }

    private void handleMainClick(Player player, int slot, boolean rightClick) {
        switch (slot) {
            case 11 -> openCommandsGui(player);
            case 13 -> {
                if (rightClick) {
                    openDenyMessageGui(player);
                } else {
                    showDenyMessagePreview(player);
                }
            }
            case 15 -> {
                configManager.reload();
                plugin.getGuiManager().openMainGui(player);
                player.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Configuration reloaded successfully!</gray>"));
            }
        }
    }

    private void handleCommandsClick(Player player, int slot, boolean rightClick) {
        if (slot == 4) {
            openMainGui(player);
            return;
        }
        if (slot == 8) {
            promptAddCommand(player);
            return;
        }

        List<String> blocked = configManager.getBlockedCommands();
        int index = 0;
        for (int i = 19; i <= 43; i++) {
            if (i % 9 == 8 || i % 9 == 0) continue;
            if (index >= blocked.size()) break;
            if (slot == i) {
                String cmd = blocked.get(index);
                if (rightClick) {
                    configManager.removeBlockedCommand(cmd);
                    player.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Removed command: <white>/" + cmd + "</white></gray>"));
                    openCommandsGui(player);
                } else {
                    player.sendMessage(miniMessage.deserialize("<yellow>Right-click to remove <white>/" + cmd + "</white></yellow>"));
                }
                return;
            }
            index++;
        }
    }

    private void handleDenyMessageClick(Player player, int slot) {
        if (slot == 4) {
            openMainGui(player);
        } else if (slot == 8) {
            promptEditDenyMessage(player);
        }
    }

    private void showDenyMessagePreview(Player player) {
        String msg = configManager.getFormattedDenyMessage("example");
        player.sendMessage(miniMessage.deserialize("<gradient:#4ECDC4:#44A08D><bold>Current Deny Message:</bold></gradient>"));
        player.sendMessage(miniMessage.deserialize(msg));
        player.sendMessage(miniMessage.deserialize("<gray>Placeholder <white>{command}</white> will be replaced with the blocked command.</gray>"));
    }

    private void promptAddCommand(Player player) {
        player.closeInventory();
        player.sendMessage(miniMessage.deserialize("<gradient:#4ECDC4:#44A08D><bold>Add Blocked Command</bold></gradient>"));
        player.sendMessage(miniMessage.deserialize("<gray>Type the command to block (without /) or <red>cancel</red> to abort.</gray>"));

        new CommandInputHandler(plugin, player, input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage(miniMessage.deserialize("<yellow>Cancelled.</yellow>"));
                openCommandsGui(player);
                return;
            }
            String cmd = input.toLowerCase().replace("/", "");
            if (configManager.isCommandBlocked(cmd)) {
                player.sendMessage(miniMessage.deserialize("<red>Command <white>/" + cmd + "</white> is already blocked!</red>"));
            } else {
                configManager.addBlockedCommand(cmd);
                player.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Added command: <white>/" + cmd + "</white></gray>"));
            }
            openCommandsGui(player);
        });
    }

    private void promptEditDenyMessage(Player player) {
        player.closeInventory();
        player.sendMessage(miniMessage.deserialize("<gradient:#4ECDC4:#44A08D><bold>Edit Deny Message</bold></gradient>"));
        player.sendMessage(miniMessage.deserialize("<gray>Enter new message (supports MiniMessage). Type <red>cancel</red> to abort.</gray>"));
        player.sendMessage(miniMessage.deserialize("<gray>Placeholder: <white>{command}</white></gray>"));
        player.sendMessage(miniMessage.deserialize("<gray>Current: <white>" + configManager.getDenyMessage() + "</white></gray>"));

        new CommandInputHandler(plugin, player, input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage(miniMessage.deserialize("<yellow>Cancelled.</yellow>"));
                openDenyMessageGui(player);
                return;
            }
            configManager.setDenyMessage(input);
            player.sendMessage(miniMessage.deserialize("<green><bold>✔</bold></green> <gray>Deny message updated!</gray>"));
            openDenyMessageGui(player);
        });
    }

    public void handleClose(InventoryCloseEvent event) {
    }

    public enum GuiType {
        MAIN, COMMANDS, DENY_MESSAGE
    }

    public static class GuiHolder implements InventoryHolder {
        private final GuiType type;

        public GuiHolder(GuiType type) {
            this.type = type;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }

        public GuiType getType() {
            return type;
        }
    }

    public static class CommandInputHandler {
        private final PlHidePlus plugin;
        private final Player player;
        private final java.util.function.Consumer<String> callback;

        public CommandInputHandler(PlHidePlus plugin, Player player, java.util.function.Consumer<String> callback) {
            this.plugin = plugin;
            this.player = player;
            this.callback = callback;

            plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
                @org.bukkit.event.EventHandler
                public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent e) {
                    if (e.getPlayer().equals(player)) {
                        e.setCancelled(true);
                        String message = e.getMessage();
                        
                        // باز کردن GUI و عملیات سرور نباید Async انجام شود
                        Bukkit.getScheduler().runTask(plugin, () -> callback.accept(message));
                        
                        org.bukkit.event.HandlerList.unregisterAll(this);
                    }
                }
            }, plugin);
        }
    }
}