package tech.markxhewson.mines.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

@CommandAlias("setminelevel|sml")
public class SetMineLevelCommand extends BaseCommand {

    private final PrivateMines plugin;

    public SetMineLevelCommand(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onCommand(Player player, int level) {
        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());

        if (mine == null) return;

        mine.setLevel(level);
        player.sendMessage("Set mine level to " + level + ".");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, mine::save);
    }

}
