package com.itskillmaster.plhideplus.gui;

import com.itskillmaster.plhideplus.PlHidePlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {

    private final PlHidePlus plugin;

    public GuiListener(PlHidePlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // بررسی می‌کند که آیا منوی باز شده متعلق به پلاگین ماست یا خیر
        if (event.getInventory().getHolder() instanceof GuiManager.GuiHolder) {
            event.setCancelled(true); // قفل کردن کامل برداشتن آیتم‌ها
            plugin.getGuiManager().handleClick(event); // ارجاع کلیک به GuiManager برای انجام عملیات
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof GuiManager.GuiHolder) {
            plugin.getGuiManager().handleClose(event);
        }
    }
}