package tech.markxhewson.mines.manager.mine;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitTask;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.util.CC;
import tech.markxhewson.mines.util.LocationUtil;

import javax.xml.stream.Location;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MineManager {

    private final PrivateMines plugin;
    private final BukkitTask saveMinesTask;
    private final Map<UUID, PlayerMine> playerMines = new ConcurrentHashMap<>();

    public MineManager(PrivateMines plugin) {
        this.plugin = plugin;

        this.saveMinesTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().broadcastMessage(CC.translate("&7&o[Saving mines...]"));
            playerMines.values().forEach(PlayerMine::save);
            plugin.getServer().broadcastMessage(CC.translate("&7&o[Finished saving mines]"));
        }, 0L, 20L * 60 * 60); // every hour
    }

    public boolean hasMine(UUID uuid) {
        return playerMines.containsKey(uuid);
    }

    public PlayerMine getMine(UUID uuid) {
        return playerMines.get(uuid);
    }

    public CompletableFuture<Boolean> loadMine(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document document = plugin.getMongoManager().getMinesCollection().find(new Document("ownerUuid", uuid.toString())).first();

                if (document == null) {
                    return false;
                }

                PlayerMine mine = new PlayerMine(uuid);

                mine.setId(document.getInteger("id"));
                mine.setLevel(document.getInteger("level"));
                mine.setRadius(document.getInteger("radius"));
                mine.setMiningAreaRadius(document.getInteger("miningAreaRadius"));

                mine.setMineCenter(LocationUtil.deserializeLocation(document.getString("mineCenter")));
                mine.setMineCornerOne(LocationUtil.deserializeLocation(document.getString("mineCornerOne")));
                mine.setMineCornerTwo(LocationUtil.deserializeLocation(document.getString("mineCornerTwo")));

                mine.setMiningAreaCenter(LocationUtil.deserializeLocation(document.getString("miningAreaCenter")));
                mine.setMiningAreaCornerOne(LocationUtil.deserializeLocation(document.getString("miningAreaCornerOne")));
                mine.setMiningAreaCornerTwo(LocationUtil.deserializeLocation(document.getString("miningAreaCornerTwo")));

                if (document.containsKey("activeBlocks")) {
                    document.get("activeBlocks", new ArrayList<String>()).forEach((block) -> mine.getMineBlockManager().addBlock(block));
                }

                mine.setCreatedAt(document.getLong("createdAt"));
                mine.updateMiningArea();

                playerMines.put(uuid, mine);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        });
    }


    public CompletableFuture<Boolean> createMine(UUID uuid, String themeSchematic) {
        PlayerMine mine = new PlayerMine(uuid);

        return CompletableFuture.supplyAsync(() -> {
            try {
                int id = (int) plugin.getMongoManager().getMinesCollection().countDocuments() + 1;

                mine.setId(id);
                mine.setCreatedAt(System.currentTimeMillis());
                mine.setMiningAreaRadius(plugin.getConfig().getInt("mines.miningAreaRadius"));
                mine.loadSchematic(themeSchematic);

                playerMines.put(uuid, mine);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        });
    }
}
