package tech.markxhewson.mines.manager.builder;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

public class MiningAreaBuilder {

    private final PlayerMine mine;

    public MiningAreaBuilder(PlayerMine mine) {
        this.mine = mine;
    }

    public void build() {
        Location centerMineArea = mine.getMiningAreaCenter() != null ? mine.getMiningAreaCenter() : getMineCenter();

        if (mine.getLevel() % 20 == 0 && mine.getLastMineIncreaseLevel() != mine.getLevel()) {
            mine.setMiningAreaRadius(mine.getMiningAreaRadius() + 1);
            mine.setLastMineIncreaseLevel(mine.getLevel());
        }

        clearMineArea(centerMineArea);
        loadMineBlocks(centerMineArea);
        createMineWalls(centerMineArea);
        createMineFloor(centerMineArea);
        setMineAreaLocations(centerMineArea);
    }

    private void clearMineArea(Location centerMineArea) {
        int mineAreaRadius = mine.getMiningAreaRadius() + 1;

        Location corner1 = new Location(centerMineArea.getWorld(), centerMineArea.getX() - mineAreaRadius, centerMineArea.getY(), centerMineArea.getZ() - mineAreaRadius);
        Location corner2 = new Location(centerMineArea.getWorld(), centerMineArea.getX() + mineAreaRadius, centerMineArea.getY() - (mineAreaRadius + 1), centerMineArea.getZ() + mineAreaRadius);

        CuboidRegion region = new CuboidRegion(BukkitUtil.toVector(corner1), BukkitUtil.toVector(corner2));

        EditSession session = new EditSession(new BukkitWorld(PrivateMines.getInstance().getMineWorldManager().getMinesWorld()), -1);
        session.setBlocks(region, new BaseBlock(BlockID.AIR));
        session.commit();
        session.flushQueue();
    }

    private void loadMineBlocks(Location centerMineArea) {
        int mineBlocksRadius = mine.getMiningAreaRadius() - 1;

        Location corner1 = new Location(centerMineArea.getWorld(), centerMineArea.getX() - mineBlocksRadius, centerMineArea.getY(), centerMineArea.getZ() - mineBlocksRadius);
        Location corner2 = new Location(centerMineArea.getWorld(), centerMineArea.getX() + mineBlocksRadius, centerMineArea.getY() - mineBlocksRadius, centerMineArea.getZ() + mineBlocksRadius);

        CuboidRegion mineBlocksRegion = new CuboidRegion(BukkitUtil.toVector(corner1), BukkitUtil.toVector(corner2));

        RandomPattern pattern = new RandomPattern();

        if (mine.getMineBlockManager().getActiveBlocks().isEmpty()) {
            pattern.add(new BlockPattern(new BaseBlock(Material.valueOf(mine.getMineBlockManager().getDefaultBlock()).ordinal())), 1);
        } else {
            mine.getMineBlockManager().getActiveBlocks().forEach(block -> {
                pattern.add(new BlockPattern(new BaseBlock(block.getMaterial().ordinal())), 1);
            });
        }

        EditSession session = new EditSession(new BukkitWorld(PrivateMines.getInstance().getMineWorldManager().getMinesWorld()), -1);
        session.setBlocks(mineBlocksRegion, pattern);
        session.commit();
        session.flushQueue();
    }

    private void createMineWalls(Location centerMineArea) {
        int wallsRadius = mine.getMiningAreaRadius() + 1;

        Location corner1 = new Location(centerMineArea.getWorld(), centerMineArea.getX() - wallsRadius, centerMineArea.getY(), centerMineArea.getZ() - wallsRadius);
        Location corner2 = new Location(centerMineArea.getWorld(), centerMineArea.getX() + wallsRadius, centerMineArea.getY() - (wallsRadius + 1), centerMineArea.getZ() + wallsRadius);

        CuboidRegion wallsRegion = new CuboidRegion(BukkitUtil.toVector(corner1), BukkitUtil.toVector(corner2));

        EditSession session = new EditSession(new BukkitWorld(PrivateMines.getInstance().getMineWorldManager().getMinesWorld()), -1);
        session.makeCuboidWalls(wallsRegion, new BaseBlock(BlockID.BEDROCK));
        session.commit();
        session.flushQueue();
    }

    private void createMineFloor(Location centerMineArea) {
        int wallsRadius = mine.getMiningAreaRadius() + 1;

        Location corner1 = new Location(centerMineArea.getWorld(), centerMineArea.getX() - wallsRadius, centerMineArea.getY() - (wallsRadius + 1), centerMineArea.getZ() - wallsRadius);
        Location corner2 = new Location(centerMineArea.getWorld(), centerMineArea.getX() + wallsRadius, centerMineArea.getY() - (wallsRadius + 1), centerMineArea.getZ() + wallsRadius);

        CuboidRegion floorRegion = new CuboidRegion(BukkitUtil.toVector(corner1), BukkitUtil.toVector(corner2));

        EditSession session = new EditSession(new BukkitWorld(PrivateMines.getInstance().getMineWorldManager().getMinesWorld()), -1);
        session.setBlocks(floorRegion, new BaseBlock(BlockID.BEDROCK));
        session.commit();
        session.flushQueue();
    }

    private void setMineAreaLocations(Location centerMineArea) {
        int wallsRadius = mine.getMiningAreaRadius();

        mine.setMiningAreaCenter(centerMineArea);

        Location corner1 = new Location(centerMineArea.getWorld(), centerMineArea.getX() - wallsRadius, centerMineArea.getY(), centerMineArea.getZ() - wallsRadius);
        Location corner2 = new Location(centerMineArea.getWorld(), centerMineArea.getX() + wallsRadius, centerMineArea.getY() - (wallsRadius + 1), centerMineArea.getZ() + wallsRadius);

        mine.setMiningAreaCornerOne(corner1);
        mine.setMiningAreaCornerTwo(corner2);
    }


    // function will only work on mine's first creation due to sponge being replaced once mine is created
    public Location getMineCenter() {
        CuboidRegion region = new CuboidRegion(BukkitUtil.toVector(mine.getMineCornerOne()), BukkitUtil.toVector(mine.getMineCornerTwo()));

        for (BlockVector blockVector : region) {
            Location location = BukkitUtil.toLocation(PrivateMines.getInstance().getMineWorldManager().getMinesWorld(), blockVector);

            if (location.getBlock().getType() == Material.SPONGE) {
                return location;
            }
        }

        return null;
    }

}
