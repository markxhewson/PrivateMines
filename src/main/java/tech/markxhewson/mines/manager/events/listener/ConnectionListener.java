package tech.markxhewson.mines.manager.events.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.enchants.impl.JackhammerEnchant;
import tech.markxhewson.mines.manager.mine.PlayerMine;
import tech.markxhewson.mines.util.CC;
import tech.markxhewson.mines.util.EnchantUtil;
import tech.markxhewson.mines.util.ItemBuilder;

public class ConnectionListener implements Listener {

    private final PrivateMines plugin;

    public ConnectionListener(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getMineManager().getPlayerMines().containsKey(player.getUniqueId())) {
            plugin.getMineManager().loadMine(player.getUniqueId()).thenAccept(mine -> {
                if (mine) player.sendMessage(CC.translate("&aYour mine has been loaded successfully."));
                else player.sendMessage(CC.translate("&cUnable to load your mine as one has not been created yet. &7/mine"));
            });
        }

        ItemStack startingPickaxe = new ItemBuilder(Material.DIAMOND_PICKAXE)
                .setDisplayName("&a" + player.getName() + "'s Pickaxe")
                .addEnchantment(Enchantment.DIG_SPEED, 100)
                .addEnchantment(plugin.getEnchantsManager().getEnchantment("Jackhammer"), 1)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .setUnbreakable(true)
                .build();

        player.getInventory().addItem(EnchantUtil.formatEnchantments(startingPickaxe));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getMineManager().hasMine(player.getUniqueId())) return;

        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, mine::save);
    }

}
