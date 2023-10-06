package tech.markxhewson.mines.manager.enchants.impl;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Getter
public class JackhammerEnchant extends Enchantment {

    private final String name = "Jackhammer";
    private final int maxLevel = 5000;

    public JackhammerEnchant(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return false;
    }

    public void canActivate(Block block, int enchantLevel) {
        double activationChance = calculateActivationChance(enchantLevel);
        double randomValue = Math.random();

        if (randomValue <= activationChance) {
            onActivate(block, new LinkedList<>());
        }
    }

    public void onActivate(Block block, LinkedList<Block> previousBlocks) {
        block.setType(Material.AIR);
        previousBlocks.add(block);

        Block[] adjacentBlocks = {
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.WEST)
        };

        for (Block adjacentBlock : adjacentBlocks) {
            if (previousBlocks.contains(adjacentBlock)) {
                continue;
            }

            if (adjacentBlock.getType() == Material.BEDROCK) {
                continue;
            }

            onActivate(adjacentBlock, previousBlocks);
        }
    }

    public double calculateActivationChance(int level) {
        return 0.00001 * level;
    }
}
