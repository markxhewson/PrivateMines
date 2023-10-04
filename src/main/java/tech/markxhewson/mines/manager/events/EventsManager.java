package tech.markxhewson.mines.manager.events;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.events.listener.BlockInteractListener;
import tech.markxhewson.mines.manager.events.listener.ConnectionListener;
import tech.markxhewson.mines.manager.events.listener.InventoryInteractListener;

import java.util.LinkedList;
import java.util.List;

@Getter
public class EventsManager {

    private final PrivateMines plugin;

    private final List<Listener> listeners = new LinkedList<>();

    public EventsManager(PrivateMines plugin) {
        this.plugin = plugin;

        addListener(new ConnectionListener(plugin));
        addListener(new BlockInteractListener(plugin));
        addListener(new InventoryInteractListener(plugin));

        loadListeners();
    }

    public void addListener(Listener listener) {
        getListeners().add(listener);
    }

    public void removeListener(Listener listener) {
        getListeners().remove(listener);
    }

    public void reloadListeners() {
        getListeners().forEach(HandlerList::unregisterAll);
        getListeners().forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    public void loadListeners() {
        getListeners().forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    public void unloadListeners() {
        getListeners().forEach(HandlerList::unregisterAll);
    }

}
