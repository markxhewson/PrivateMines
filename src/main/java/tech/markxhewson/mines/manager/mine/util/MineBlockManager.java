package tech.markxhewson.mines.manager.mine.util;

import lombok.Getter;
import org.bukkit.Material;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MineBlockManager {

    private final PlayerMine mine;
    private List<MineBlock> activeBlocks = new LinkedList<>();

    public MineBlockManager(PlayerMine mine) {
        this.mine = mine;
    }

    public MineBlock getBlock(String material) {
        return activeBlocks.stream().filter(block -> block.getMaterial().name().equals(material)).findFirst().orElse(null);
    }

    public boolean isActive(String material) {
        return activeBlocks.stream().anyMatch(block -> block.getMaterial().name().equals(material));
    }

    public void addBlock(String material) {
        activeBlocks.add(new MineBlock(Material.valueOf(material)));
    }

    public void removeBlock(String material) {
        activeBlocks.removeIf(block -> block.getMaterial().name().equals(material));
    }

    public String getDefaultBlock() {
        for (String block : PrivateMines.getInstance().getConfig().getConfigurationSection("blocks").getKeys(false)) {
            if (PrivateMines.getInstance().getConfig().getBoolean("blocks." + block + ".default")) {
                return PrivateMines.getInstance().getConfig().getString("blocks." + block + ".id");
            }
        }

        return "COBBLESTONE";
    }

}
