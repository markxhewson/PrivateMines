package tech.markxhewson.mines.menu;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import tech.markxhewson.mines.PrivateMines;

@Getter
public class SelectMineMenu extends Menu {

    private final PrivateMines plugin;
    private final Player player;

    public SelectMineMenu(PrivateMines plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public String getMenuName() {
        return "Select your personal mine theme";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void updateItems() {

    }

    @Override
    public void handleClick(InventoryClickEvent event) {

    }
}
