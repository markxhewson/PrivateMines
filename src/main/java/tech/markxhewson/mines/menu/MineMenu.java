package tech.markxhewson.mines.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;
import tech.markxhewson.mines.manager.mine.util.MineBlock;
import tech.markxhewson.mines.util.CC;
import tech.markxhewson.mines.util.ItemBuilder;
import tech.markxhewson.mines.util.TimeUtil;

import java.util.LinkedList;
import java.util.List;

public class MineMenu extends Menu {

    private final PrivateMines plugin;
    private final Player player;

    public MineMenu(PrivateMines plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public String getMenuName() {
        return "Your Mine";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void updateItems() {
        List<String> lore = new LinkedList<>();
        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());

        if (mine != null) {
            lore.add("");
            lore.add("&4&l| &7ID: &a" + mine.getId());
            lore.add("&4&l| &7Mine Level: &a" + mine.getLevel());
            lore.add("&4&l| &7Created &a" + TimeUtil.formatMillis(System.currentTimeMillis() - mine.getCreatedAt()) + " &7ago");
            lore.add("");
            lore.add("&7(( &aClick &7to teleport to your personal mine. ))");
        } else {
            lore.add("&7(( &aClick &7to create your personal mine. ))");
        }

        ItemBuilder mineItem = new ItemBuilder(Material.WOOD_DOOR)
                .setDisplayName("&a&lYour Mine")
                .setLore(lore);

        getInventory().setItem(mine == null ? 22 : 10, mineItem.build());

        if (mine == null) {
            return;
        }

        ItemBuilder resetItem = new ItemBuilder(Material.FISHING_ROD)
                .setDisplayName("&c&lReset Mine " + (mine.getTimeRemainingOnMineCooldown() <= 0 ? "" : "&7(&f" + TimeUtil.formatMillis(mine.getTimeRemainingOnMineCooldown()) + " remaining&7)"))
                .setLore("&7(( &cClick &7to reset your mine. ))");

        ItemBuilder blockSelectItem = new ItemBuilder(Material.BEACON)
                .setDisplayName("&6&lCustomise Mine →→")
                .setLore("&7(( Select 3 blocks to spruce up your mine! ))");

        // fill empty slots with default placeholder
        for (int i = 32; i < 35; i++) {
            getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName("&7Empty block slot").build());
        }

        // populate the slots with the unlocked blocks if there are any
        int i = 32;
        for (MineBlock unlockedBlock : mine.getMineBlockManager().getActiveBlocks()) {
            getInventory().setItem(i,
                    new ItemBuilder(unlockedBlock.getMaterial())
                            .setDisplayName("&a" + unlockedBlock.getMaterial().name())
                            .setLore("", "&7(( &cShift + Click &7to remove. ))")
                            .addEnchantment(Enchantment.DURABILITY, 1)
                            .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                            .build()
            );
            i++;
        }

        getInventory().setItem(28, resetItem.build());
        getInventory().setItem(31, blockSelectItem.build());

        super.fillRemainingSlots();
    }


    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        int slot = event.getSlot();

        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (slot == (mine == null ? 22 : 10)) {
            handleMineTeleport(player, mine);
        }

        if (mine == null) {
            return; // Do not handle any more clicks if the mine is null
        }

        if (slot == 28) {
            long cooldownTime = mine.getTimeRemainingOnMineCooldown();
            if (cooldownTime <= 0) {
                handleMineReset(player, mine);
            } else {
                player.sendMessage(CC.translate("&cYou cannot reset your mine for another &f" + TimeUtil.formatMillis(cooldownTime) + "&c!"));
            }
        }

        if (slot == 32 || slot == 33 || slot == 34) {
            if (event.isShiftClick() && item.getType() != Material.STAINED_GLASS_PANE) {
                mine.getMineBlockManager().removeBlock(item.getType().name());
                updateItems();
                player.updateInventory();
                return;
            }
            new MineBlockSelectMenu(plugin, mine).open(player);
        }
    }

    private void handleMineTeleport(Player player, PlayerMine mine) {
        if (mine != null) {
            mine.teleport(player);
        } else {
            plugin.getMineManager().createMine(player.getUniqueId()).thenAccept(isCreated -> {
                if (isCreated) {
                    player.sendMessage(CC.translate("&aYour mine has been created successfully."));
                } else {
                    player.sendMessage(("&cUnable to create your mine."));
                }

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    getInventory().setItem(22, null);
                    updateItems();
                    player.updateInventory();
                }, 5L);
            });
        }
    }

    private void handleMineReset(Player player, PlayerMine mine) {
        mine.updateMiningArea();
        player.sendMessage(CC.translate("&aYour mine has been reset successfully."));
    }

}
