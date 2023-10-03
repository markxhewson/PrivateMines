package tech.markxhewson.mines.util.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final PrivateMines plugin;

    public PlaceholderAPI(PrivateMines plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mines";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Lotho | markxhewson";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        PlayerMine mine = plugin.getMineManager().getMine(player.getUniqueId());

        if (mine == null) {
            return "";
        }

        if (params.equalsIgnoreCase("mine_level")) {
            return String.valueOf(mine.getLevel());
        }
        else if (params.equalsIgnoreCase("mine_nextLevelPercentage")) {
            return mine.nextLevelPercentage() + "%";
        }

        return null;
    }
}
