package tech.markxhewson.mines.manager.mine.builder;

import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import tech.markxhewson.mines.PrivateMines;
import tech.markxhewson.mines.manager.mine.PlayerMine;

public class MineBorderBuilder {

    private final PlayerMine mine;
    private final Player player;

    public MineBorderBuilder(PlayerMine mine, Player player) {
        this.mine = mine;
        this.player = player;
    }

    public void build() {
        WorldBorder worldBorder = new WorldBorder();

        int borderRadius = mine.getRadius() * 3;

        worldBorder.world = ((CraftWorld) PrivateMines.getInstance().getMineWorldManager().getMinesWorld()).getHandle();
        worldBorder.setSize(borderRadius);
        worldBorder.setCenter(mine.getMiningAreaCenter().getX(), mine.getMiningAreaCenter().getZ());
        worldBorder.transitionSizeBetween(borderRadius, borderRadius - 1.0D, 20000000L);

        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
