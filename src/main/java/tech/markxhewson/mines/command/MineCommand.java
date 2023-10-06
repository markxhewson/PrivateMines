package tech.markxhewson.mines.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.menu.MineMenu;
import tech.markxhewson.mines.menu.SelectMineMenu;

@CommandAlias("mine|mines")
public class MineCommand extends BaseCommand {

    private final PrivateMines plugin;

    public MineCommand(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Open your mine menu.")
    public void onMineMenuOpen(Player player) {
        if (plugin.getMineManager().hasMine(player.getUniqueId())) {
            new MineMenu(plugin, player).open(player);
        } else {
            new SelectMineMenu(plugin, player).open(player);
        }
    }

    @Subcommand("reset")
    @Description("Reset your mine.")
    public void onMineReset(Player player) {
        if (!plugin.getMineManager().hasMine(player.getUniqueId())) {
            player.sendMessage("You do not have a mine.");
            return;
        }

        plugin.getMineManager().getMine(player.getUniqueId()).updateMiningArea();
    }

}
