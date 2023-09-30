package tech.markxhewson.mines.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.menu.Menu;

public class InventoryInteractListener implements Listener {

    private final PrivateMines plugin;

    public InventoryInteractListener(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof Menu && event.getCurrentItem() != null) {
            event.setCancelled(true);
            ((Menu) holder).handleClick(event);
        }

    }

}
