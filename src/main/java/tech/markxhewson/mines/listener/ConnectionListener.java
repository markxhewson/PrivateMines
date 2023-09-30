package tech.markxhewson.mines.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;
import tech.markxhewson.mines.util.CC;

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
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getMineManager().hasMine(player.getUniqueId())) return;

        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, mine::save);
    }

}
