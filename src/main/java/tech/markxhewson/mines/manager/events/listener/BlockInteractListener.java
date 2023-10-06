package tech.markxhewson.mines.manager.events.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.enchants.impl.JackhammerEnchant;
import tech.markxhewson.mines.manager.mine.PlayerMine;
import tech.markxhewson.mines.util.CC;

import java.util.Objects;

public class BlockInteractListener implements Listener {

    private final PrivateMines plugin;

    public BlockInteractListener(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());
        Location location = event.getBlock().getLocation();

        ItemStack item = player.getItemInHand();

        if (mine == null) return;
        if (!Objects.equals(player.getWorld().getName(), mine.getMineCenter().getWorld().getName())) return;

        if (!mine.isWithinMiningArea(location)) {
            player.sendMessage(CC.translate("&cYou cannot break blocks outside your mining area."));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);

        item.getEnchantments().forEach((enchantment, level) -> {
            Enchantment enchant = plugin.getEnchantsManager().getEnchantment(enchantment.getName());
            if (enchant == null) return;

            if (enchant.getName().equalsIgnoreCase("Jackhammer")) {
                JackhammerEnchant jackhammer = (JackhammerEnchant) enchant;
                jackhammer.canActivate(event.getBlock(), level);
            }
        });

        mine.handleBlockMine();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());
        Location location = event.getBlock().getLocation();

        if (mine == null) return;
        if (!Objects.equals(player.getWorld().getName(), mine.getMineCenter().getWorld().getName())) return;

        if (!mine.isWithinMiningArea(location)) {
            player.sendMessage(CC.translate("&cYou cannot place blocks outside your mining area."));
            event.setCancelled(true);
        }
    }

}
