package tech.markxhewson.mines.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

@CommandAlias("admin")
@CommandPermission("mines.admin")
public class AdminCommand extends BaseCommand {

    private final PrivateMines plugin;

    public AdminCommand(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @Subcommand("setminelevel|sml")
    @Description("Manually set your mine level for debugging.")
    public void onCommand(Player player, int level) {
        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());

        if (mine == null) return;

        mine.setLevel(level);
        player.sendMessage("Set mine level to " + level + ".");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, mine::save);
    }

}
