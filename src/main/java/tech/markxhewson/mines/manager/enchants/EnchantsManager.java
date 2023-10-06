package tech.markxhewson.mines.manager.enchants;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.enchants.impl.JackhammerEnchant;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EnchantsManager {

    private final PrivateMines plugin;

    private final Map<String, Enchantment> customEnchantments = new ConcurrentHashMap<>();

    public EnchantsManager(PrivateMines plugin) {
        this.plugin = plugin;

        loadEnchantments();
    }

    public Enchantment getEnchantment(String enchantmentName) {
        return customEnchantments.get(enchantmentName);
    }

    public void loadEnchantments() {
        customEnchantments.put("Jackhammer", new JackhammerEnchant(100));

        registerEnchantments();
    }

    public void registerEnchantments() {
        customEnchantments.forEach((enchantmentName, enchantment) -> {
            try {
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.set(null, true);

                Enchantment.registerEnchantment(enchantment);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
