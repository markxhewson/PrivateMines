package tech.markxhewson.mines.util;

import org.bukkit.Location;
import tech.markxhewson.mines.PrivateMines;

public class LocationUtil {

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location deserializeLocation(String location) {
        String[] split = location.split(",");

        return new Location(
                PrivateMines.getInstance().getServer().getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5])
        );
    }

}
