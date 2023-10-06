package tech.markxhewson.mines.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EnchantUtil {

    public static ItemStack formatEnchantments(ItemStack item) {
        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        ItemMeta meta = item.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> formattedEnchants = new LinkedList<>();
        formattedEnchants.add("");
        formattedEnchants.add(CC.translate("&a&lENCHANTS"));

        enchantments.forEach((enchantment, level) -> {
            String enchantName;

            try {
                EnchantmentsEnum enchantData = EnchantmentsEnum.valueOf(enchantment.getName());
                enchantName = enchantData.getActualEnchantmentName();
            } catch (IllegalArgumentException e) {
                enchantName = enchantment.getName();
            }

            formattedEnchants.add(CC.translate("&b&l| &e" + enchantName + " &c" + level));
        });

        formattedEnchants.add("");

        meta.setLore(formattedEnchants);
        item.setItemMeta(meta);

        return item;
    }

}
