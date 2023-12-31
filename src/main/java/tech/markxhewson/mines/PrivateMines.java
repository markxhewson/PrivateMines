package tech.markxhewson.mines;

import co.aikar.commands.BukkitCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import tech.markxhewson.mines.command.MineCommand;
import tech.markxhewson.mines.command.AdminCommand;
import tech.markxhewson.mines.manager.enchants.EnchantsManager;
import tech.markxhewson.mines.manager.events.EventsManager;
import tech.markxhewson.mines.manager.events.listener.BlockInteractListener;
import tech.markxhewson.mines.manager.events.listener.ConnectionListener;
import tech.markxhewson.mines.manager.events.listener.InventoryInteractListener;
import tech.markxhewson.mines.manager.mine.MineManager;
import tech.markxhewson.mines.manager.world.MineWorldManager;
import tech.markxhewson.mines.storage.MongoManager;
import tech.markxhewson.mines.util.expansion.PlaceholderAPI;

@Getter
public final class PrivateMines extends JavaPlugin {

    private static PrivateMines instance;

    private BukkitCommandManager commandManager;
    private EventsManager eventsManager;

    private MongoManager mongoManager;
    private MineWorldManager mineWorldManager;
    private MineManager mineManager;

    private EnchantsManager enchantsManager;

    private Gson gson;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;

        commandManager = new BukkitCommandManager(this);
        eventsManager = new EventsManager(this);
        initCommands();

        mongoManager = new MongoManager(this);
        mineWorldManager = new MineWorldManager(this);
        mineManager = new MineManager(this);

        enchantsManager = new EnchantsManager(this);

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI(this).register();
        }
    }

    @Override
    public void onDisable() {
        getMongoManager().disconnect();
    }

    public void initCommands() {
        commandManager.registerCommand(new MineCommand(this));
        commandManager.registerCommand(new AdminCommand(this));
    }

    public static PrivateMines getInstance() {
        return instance;
    }
}
