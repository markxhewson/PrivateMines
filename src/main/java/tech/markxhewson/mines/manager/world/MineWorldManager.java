package tech.markxhewson.mines.manager.world;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.util.LocationUtil;

import java.util.concurrent.CompletableFuture;

@Getter
public class MineWorldManager {

    private final PrivateMines plugin;
    private final World minesWorld;

    private final int mineGap = 1000;

    private final Location defaultLocation;

    public MineWorldManager(PrivateMines plugin) {
        this.plugin = plugin;
        this.minesWorld = plugin.getServer().createWorld(
                new WorldCreator("mines")
                        .type(WorldType.FLAT)
                        .generator(new EmptyWorldGenerator()));

        this.defaultLocation = new Location(minesWorld, 0, 200, 0);
    }

    public CompletableFuture<Location> getLastMineLocation() {
        return CompletableFuture.supplyAsync(() -> {
            Document document = plugin.getMongoManager().getMinesCollection().find().sort(new Document("id", -1)).first();

            if (document == null) {
                return null;
            }

            return LocationUtil.deserializeLocation(document.getString("mineCenter"));
        });
    }
}
