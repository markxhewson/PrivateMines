package tech.markxhewson.mines;

import co.aikar.commands.BukkitCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import tech.markxhewson.mines.command.MineCommand;
import tech.markxhewson.mines.command.AdminCommand;
import tech.markxhewson.mines.listener.BlockInteractListener;
import tech.markxhewson.mines.listener.ConnectionListener;
import tech.markxhewson.mines.listener.InventoryInteractListener;
import tech.markxhewson.mines.manager.mine.MineManager;
import tech.markxhewson.mines.manager.world.MineWorldManager;
import tech.markxhewson.mines.storage.MongoManager;
import tech.markxhewson.mines.util.expansion.PlaceholderAPI;

@Getter
public final class PrivateMines extends JavaPlugin {

    private static PrivateMines instance;

    private BukkitCommandManager commandManager;

    private MongoManager mongoManager;
    private MineWorldManager mineWorldManager;
    private MineManager mineManager;
    private Gson gson;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;

        commandManager = new BukkitCommandManager(this);

        mongoManager = new MongoManager(this);
        mineWorldManager = new MineWorldManager(this);
        mineManager = new MineManager(this);
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI(this).register();
        }

        commandManager.registerCommand(new MineCommand(this));
        commandManager.registerCommand(new AdminCommand(this));

        getServer().getPluginManager().registerEvents(new InventoryInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockInteractListener(this), this);
    }

    @Override
    public void onDisable() {
        getMongoManager().disconnect();
    }

    public static PrivateMines getInstance() {
        return instance;
    }
}
