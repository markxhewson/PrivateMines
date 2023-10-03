package tech.markxhewson.mines.manager.builder;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Location;
import org.bukkit.Material;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class MineBuilder {

    private final PrivateMines plugin;
    private final PlayerMine mine;
    private final String schematicName;

    public MineBuilder(PrivateMines plugin, PlayerMine mine, String schematicName) {
        this.plugin = plugin;
        this.mine = mine;
        this.schematicName = schematicName;
    }

    public void build() {
        plugin.getMineWorldManager().getLastMineLocation().thenAccept(location -> {
            int x, y, z;

            if (location == null) {
                x = 0;
                y = 100;
                z = 0;
            } else {
                x = location.getBlockX();
                y = location.getBlockY();
                z = location.getBlockZ();
            }

            int minesCount = (int) plugin.getMongoManager().getMinesCollection().countDocuments() + 1;

            int borderGap = plugin.getConfig().getInt("mines.borderGap");
            int index = minesCount / 100;
            int lastIndex = (minesCount - 1) / 100;

            if (index % 2 == 0) { // even rows
                if (index != lastIndex) {
                    x -= borderGap;
                } else {
                    z += borderGap;
                }
            } else { // odd rows
                if (index != lastIndex) {
                    x -= borderGap;
                } else {
                    z -= borderGap;
                }
            }

            int radius = mine.getRadius() * 2;

            mine.setMineCenter(new Location(plugin.getMineWorldManager().getMinesWorld(), x, y, z));
            mine.setMineCornerOne(new Location(mine.getMineCenter().getWorld(), mine.getMineCenter().getX() - radius, mine.getMineCenter().getY() + radius, mine.getMineCenter().getZ() + radius));
            mine.setMineCornerTwo(new Location(mine.getMineCenter().getWorld(), mine.getMineCenter().getX() + radius, mine.getMineCenter().getY() - radius, mine.getMineCenter().getZ() - radius));

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                mine.getMineCenter().getBlock().setType(Material.BEDROCK);
                loadSchematic(mine);
            });
        });
    }

    public void loadSchematic(PlayerMine mine) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/schematics/" + this.schematicName + ".schematic");

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        if (format == null) {
            System.out.println("[ERROR] Schematic file could not be found: " + this.schematicName);
            return;
        }

        try {
            EditSession session = format.load(file).paste(FaweAPI.getWorld(mine.getMineCenter().getWorld().getName()), BukkitUtil.toVector(mine.getMineCenter()), false, false, null);
            session.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mine.updateMiningArea();
    }

}
