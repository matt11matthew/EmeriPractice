package net.emeri.practice.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class LocationUtil {

    public static Location getPlayerLocationFromString(String locationString) {
        String[] strings = locationString.split(",");
        World world = Bukkit.getWorld(strings[0].trim());
        double x = Double.parseDouble(strings[1].trim());
        double y = Double.parseDouble(strings[2].trim());
        double z = Double.parseDouble(strings[3].trim());
        float yaw = Float.parseFloat(strings[4].trim());
        float pitch = Float.parseFloat(strings[5].trim());
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location getBlockLocationFromString(String locationString) {
        String[] strings = locationString.split(",");
        World world = Bukkit.getWorld(strings[0].trim());
        int x = Integer.parseInt(strings[1].trim());
        int y = Integer.parseInt(strings[2].trim());
        int z = Integer.parseInt(strings[3].trim());
        return new Location(world, x, y, z);
    }

    public static String getStringFromPlayerLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," +  location.getPitch();
    }

    public static String getStringFromBlockLocation(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}
