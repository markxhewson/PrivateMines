package tech.markxhewson.mines.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import tech.markxhewson.mines.PrivateMines;
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

        if (mine == null) return;
        if (!Objects.equals(player.getWorld().getName(), mine.getMineCenter().getWorld().getName())) return;

        if (!mine.isWithinMiningArea(location)) {
            player.sendMessage(CC.translate("&cYou cannot break blocks outside your mining area."));
            event.setCancelled(true);
        }

        mine.giveExperience();
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
