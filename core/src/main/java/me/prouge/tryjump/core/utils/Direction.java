package me.prouge.tryjump.core.utils;

public enum Direction {
    NORTH("Z"),
    EAST("X"),
    SOUTH("Z"),
    WEST("X");

    Direction(String symbol) {
    }

    public static Direction fromYaw(float yaw) {
        yaw = (yaw % 360 + 360) % 360; // normalizes the yaw value to be between 0 and 360
        if (yaw >= 315 || yaw < 45) {
            return NORTH;
        } else if (yaw >= 45 && yaw < 135) {
            return EAST;
        } else if (yaw >= 135 && yaw < 225) {
            return SOUTH;
        } else {
            return WEST;
        }
    }
}
