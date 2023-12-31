package tech.markxhewson.mines.manager.mine;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.builder.MineBorderBuilder;
import tech.markxhewson.mines.manager.mine.builder.MineBuilder;
import tech.markxhewson.mines.manager.mine.builder.MiningAreaBuilder;
import tech.markxhewson.mines.manager.mine.util.MineBlockManager;
import tech.markxhewson.mines.util.LocationUtil;
import tech.markxhewson.mines.util.PlayerUtil;

import java.text.DecimalFormat;
import java.util.UUID;

@Getter @Setter
public class PlayerMine {

    @Expose
    private final UUID ownerUuid;

    @Expose
    private int id = 0;

    @Expose
    private int level = 1;

    @Expose
    private int experience = 0;

    @Expose
    private int blocksMined = 0;

    @Expose(serialize = false, deserialize = false)
    private int lastMineIncreaseLevel = 1;

    @Expose
    private int radius = 50;

    @Expose
    private int miningAreaRadius = 0;

    @Expose(serialize = false, deserialize = false)
    private Location mineCenter, mineCornerOne, mineCornerTwo;

    @Expose(serialize = false, deserialize = false)
    private Location miningAreaCenter, miningAreaCornerOne, miningAreaCornerTwo;

    @Expose(serialize = false, deserialize = false)
    private MineBlockManager mineBlockManager = new MineBlockManager(this);

    @Expose
    private long createdAt = System.currentTimeMillis();

    @Expose(serialize = false, deserialize = false)
    private long lastMineReset = 0;

    public PlayerMine(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public Player getOwner() {
        return PrivateMines.getInstance().getServer().getPlayer(ownerUuid);
    }

    public void teleport(Player player) {
        player.teleport(getMineCenter());
        updateWorldBorder(player);
    }

    public void loadSchematic(String themeSchematic) {
        MineBuilder builder = new MineBuilder(PrivateMines.getInstance(), this, themeSchematic);
        builder.build();
    }

    public void updateWorldBorder(Player player) {
        MineBorderBuilder builder = new MineBorderBuilder(this, player);
        builder.build();
    }

    public void updateMiningArea() {
        MiningAreaBuilder builder = new MiningAreaBuilder(this);
        builder.build();

        this.lastMineReset = System.currentTimeMillis();
    }

    public boolean isMineUnderCapacity() {
        Location centerMineArea = getMiningAreaCenter();
        int mineBlocksRadius = getMiningAreaRadius() - 1;

        Location corner1 = new Location(centerMineArea.getWorld(), centerMineArea.getX() - mineBlocksRadius, centerMineArea.getY(), centerMineArea.getZ() - mineBlocksRadius);
        Location corner2 = new Location(centerMineArea.getWorld(), centerMineArea.getX() + mineBlocksRadius, centerMineArea.getY() - mineBlocksRadius, centerMineArea.getZ() + mineBlocksRadius);

        CuboidRegion region = new CuboidRegion(BukkitUtil.toVector(corner1), BukkitUtil.toVector(corner2));

        int maxBlocks = 0;
        int currentBlocks = 0;

        for (BlockVector vector : region) {
            Block block = centerMineArea.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());

            if (block.getType() != Material.AIR) {
                currentBlocks += 1;
            }

            maxBlocks += 1;
        }

        return currentBlocks <= (maxBlocks * 0.2);
    }

    public void handleBlockMine() {
        experience += PrivateMines.getInstance().getConfig().getInt("mines.defaultBlockExperience");
        blocksMined += 1;

        if (experience >= getExperienceForNextLevel()) {
            level += 1;
            experience = 0;

            Player player = getOwner();
            if (player != null) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                PlayerUtil.sendTitle(player, "&4&lLEVEL UP");
                PlayerUtil.sendSubTitle(player, "&eYour mine is now level &f" + level);
            }
        }

        if (isMineUnderCapacity()) {
            updateMiningArea();
        }
    }

    public double nextLevelPercentage() {
        int experienceNeeded = getExperienceForNextLevel();

        if (experienceNeeded == 0) {
            return 100.0; // Avoid division by zero if already at max level
        }

        double percentage = (experience * 100.0) / experienceNeeded;
        return Double.parseDouble(new DecimalFormat("#.##").format(percentage));
    }

    public int getExperienceForNextLevel() {
        return (int) Math.pow(200 * level, 0.85);
    }

    public long getMineCooldown() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastMineReset;

        if (elapsedTime >= PrivateMines.getInstance().getConfig().getInt("mines.resetCooldown")) {
            return 0;
        } else {
            return PrivateMines.getInstance().getConfig().getInt("mines.resetCooldown") - elapsedTime;
        }
    }

    public boolean isWithinMiningArea(Location location) {
        CuboidRegion mineRegion = new CuboidRegion(BukkitUtil.toVector(getMiningAreaCornerOne()), BukkitUtil.toVector(getMiningAreaCornerTwo()));
        return mineRegion.contains(BukkitUtil.toVector(location));
    }

    public void save() {
        JsonObject object = (JsonObject) new JsonParser().parse(PrivateMines.getInstance().getGson().toJson(this));

        Document locations = new Document()
                .append("mineCenter", LocationUtil.serializeLocation(mineCenter))
                .append("mineCornerOne", LocationUtil.serializeLocation(mineCornerOne))
                .append("mineCornerTwo", LocationUtil.serializeLocation(mineCornerTwo))
                .append("miningAreaCenter", LocationUtil.serializeLocation(miningAreaCenter))
                .append("miningAreaCornerOne", LocationUtil.serializeLocation(miningAreaCornerOne))
                .append("miningAreaCornerTwo", LocationUtil.serializeLocation(miningAreaCornerTwo));

        object.add("locations", new JsonParser().parse(locations.toJson()));

        if (!mineBlockManager.getActiveBlocks().isEmpty()) {
            JSONArray blocksArray = new JSONArray();
            mineBlockManager.getActiveBlocks().forEach(block -> blocksArray.add(block.getMaterial().name()));
            object.add("activeBlocks", new JsonParser().parse(blocksArray.toJSONString()));
        }

        PrivateMines.getInstance().getMongoManager().getMinesCollection().findOneAndReplace(
                new Document("ownerUuid", ownerUuid.toString()),
                Document.parse(object.toString()),
                new FindOneAndReplaceOptions().upsert(true)
        );

    }

}
