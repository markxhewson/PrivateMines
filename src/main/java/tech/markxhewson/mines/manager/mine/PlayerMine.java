package tech.markxhewson.mines.manager.mine;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.builder.MineBorderBuilder;
import tech.markxhewson.mines.manager.builder.MineBuilder;
import tech.markxhewson.mines.manager.builder.MiningAreaBuilder;
import tech.markxhewson.mines.manager.mine.util.MineBlockManager;
import tech.markxhewson.mines.util.CC;
import tech.markxhewson.mines.util.LocationUtil;

import java.util.LinkedList;
import java.util.List;
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

    public void giveExperience() {
        experience += 5;

        if (experience >= getExperienceForNextLevel()) {
            setLevel(level++);
            setExperience(0);

            Player player = PrivateMines.getInstance().getServer().getPlayer(ownerUuid);
            if (player == null) return;

            player.sendMessage(CC.translate("&4&lOMG! &eYour mine has leveled up to level &f" + level + "&e!"));
        }
    }

    public int getExperienceForNextLevel() {
        return (int) Math.pow(25 * level, 1.5);
    }

    public long getTimeRemainingOnMineCooldown() {
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

        // independently handle location data as they can't be serialized with gson
        object.addProperty("mineCenter", LocationUtil.serializeLocation(mineCenter));
        object.addProperty("mineCornerOne", LocationUtil.serializeLocation(mineCornerOne));
        object.addProperty("mineCornerTwo", LocationUtil.serializeLocation(mineCornerTwo));

        object.addProperty("miningAreaCenter", LocationUtil.serializeLocation(miningAreaCenter));
        object.addProperty("miningAreaCornerOne", LocationUtil.serializeLocation(miningAreaCornerOne));
        object.addProperty("miningAreaCornerTwo", LocationUtil.serializeLocation(miningAreaCornerTwo));


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
