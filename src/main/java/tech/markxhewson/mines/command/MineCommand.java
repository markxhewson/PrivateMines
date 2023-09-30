package tech.markxhewson.mines.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.menu.MineMenu;

@CommandAlias("mine|mines")
public class MineCommand extends BaseCommand {

    private final PrivateMines plugin;

    public MineCommand(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onCommand(Player player) {
        new MineMenu(plugin, player).open(player);
    }

}
