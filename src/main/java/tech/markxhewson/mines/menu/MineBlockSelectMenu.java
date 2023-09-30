package tech.markxhewson.mines.menu;

import com.sk89q.worldedit.EditSessionFactory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;
import tech.markxhewson.mines.util.CC;
import tech.markxhewson.mines.util.ItemBuilder;

public class MineBlockSelectMenu extends Menu {

    private final PrivateMines plugin;
    private final PlayerMine mine;

    public MineBlockSelectMenu(PrivateMines plugin, PlayerMine mine) {
        this.plugin = plugin;
        this.mine = mine;
    }

    @Override
    public String getMenuName() {
        return "Customise your mine";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void updateItems() {
        getInventory().clear();

        for (int row = 0; row < 5; row++) {
            int index = row * 9;
            for (int slot = index; slot < index + 9; slot++)
                if (row == 0 || row == 4 || ((row > 0 && row < 5) && (index == slot || slot == index + 8)))
                    getInventory().setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE).build());
        }

        plugin.getConfig().getConfigurationSection("blocks").getKeys(false).forEach(block -> {
            String material = plugin.getConfig().getString("blocks." + block + ".id");
            int unlockLevel = plugin.getConfig().getInt("blocks." + block + ".requiredLevel");
            boolean unlocked = mine.getLevel() >= unlockLevel;

            boolean active = mine.getMineBlockManager().isActive(material);

            ItemBuilder builder = new ItemBuilder(Material.valueOf(material))
                    .setDisplayName("&a" + material)
                    .setLore(
                            "&7Status: " + (unlocked ? "&aUnlocked" : "&cLocked"),
                            "",
                            unlocked ? (active ? "&7(( &cClick &7to deactivate block! ))" : "&7(( &aClick &7to activate block! ))") : "&7(( This requires mine level &f" + unlockLevel + " &7to unlock. ))"
                    );

            if (mine.getMineBlockManager().isActive(material)) {
                builder.addEnchantment(Enchantment.DURABILITY, 1);
                builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            }

            getInventory().addItem(builder.build());
        });
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR || item.getType() == Material.STAINED_GLASS_PANE) return;

        String material = item.getType().name();
        int unlockLevel = plugin.getConfig().getInt("blocks." + material.toLowerCase() + ".requiredLevel");
        boolean canUnlock = mine.getLevel() >= unlockLevel;

        if (!canUnlock) {
            player.sendMessage(CC.translate("&cYour mine must be level &f" + unlockLevel + " &cto unlock this."));
            return;
        }

        if (mine.getMineBlockManager().isActive(material)) {
            mine.getMineBlockManager().removeBlock(material);
            player.sendMessage(CC.translate("&cYou have removed &f" + material + " &cfrom your mine."));
        } else {
            if (mine.getMineBlockManager().getActiveBlocks().size() >= 3) {
                player.sendMessage(CC.translate("&cYou can only have a maximum of 3 blocks in your mine."));
                return;
            }

            mine.getMineBlockManager().addBlock(material);
            player.sendMessage(CC.translate("&aYou have added &f" + material + " &ato your mine."));
        }

        updateItems();
        player.updateInventory();
    }
}
