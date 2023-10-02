package tech.markxhewson.mines.menu;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.util.CC;
import tech.markxhewson.mines.util.ItemBuilder;

import java.util.Objects;

@Getter
public class SelectMineMenu extends Menu {

    private final PrivateMines plugin;
    private final Player player;

    public SelectMineMenu(PrivateMines plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public String getMenuName() {
        return "Select your personal mine theme";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void updateItems() {
        int[] slots = {10, 12, 14, 16};
        int currentIndex = 0;

        for (String theme : plugin.getConfig().getConfigurationSection("themes").getKeys(false)) {
            ItemBuilder themeItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("themes." + theme + ".icon")))
                    .setDisplayName(plugin.getConfig().getString("themes." + theme + ".name"))
                    .setLore(plugin.getConfig().getStringList("themes." + theme + ".description"));

            getInventory().setItem(slots[currentIndex++], themeItem.build());
        }


        super.fillRemainingSlots();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR || item.getType() == Material.STAINED_GLASS_PANE) {
            return;
        }

        String theme = item.getItemMeta().getDisplayName();
        String themeSchematic = findThemeSchematic(theme);

        if (themeSchematic == null) {
            System.out.println("[ERROR] Theme schematic not found for theme " + theme);
            return;
        }

        plugin.getMineManager().createMine(player.getUniqueId(), themeSchematic).thenAccept((success) -> {
            if (success) {
                player.sendMessage(CC.translate("&aYour personal mine has been created!"));
            } else {
                player.sendMessage(CC.translate("&cYour personal mine could not be created. Please contact an administrator."));
            }
        });
    }

    // function to find theme in config file
    public String findThemeSchematic(String themeDisplayName) {
        for (String theme : plugin.getConfig().getConfigurationSection("themes").getKeys(false)) {
            if (Objects.equals(plugin.getConfig().getString("themes." + theme + ".name"), themeDisplayName)) {
                return plugin.getConfig().getString("themes." + theme + ".schematic");
            }
        }

        return null;
    }
}
