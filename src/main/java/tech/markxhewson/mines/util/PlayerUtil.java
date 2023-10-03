package tech.markxhewson.mines.util;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(CC.translate(message)), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendTitle(Player player, String message) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a(CC.translate("{\"text\": \"" + message + "\"}")),
                3, // fade in
                5, // stay time
                3 // fade out
        );

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
    }

    public static void sendSubTitle(Player player, String message) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                IChatBaseComponent.ChatSerializer.a(CC.translate("{\"text\": \"" + message + "\"}")),
                3, // fade in
                5, // stay time
                3 // fade out
        );

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
    }

}
