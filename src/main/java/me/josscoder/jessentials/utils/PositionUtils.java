package me.josscoder.jessentials.utils;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

public class PositionUtils {

    public static Vector3 stringToVector(String string) {
        String[] data = string.split(":");

        return new Vector3(Double.parseDouble(data[0]),
                Double.parseDouble(data[1]),
                Double.parseDouble(data[2])
        );
    }

    public static String vectorToString(Vector3 vector3) {
        return String.format("%s:%s:%s", vector3.getX(), vector3.getY(), vector3.getZ());
    }

    public static Position stringToPosition(String string) {
        String[] data = string.split(":");

        return new Position(Double.parseDouble(data[0]),
                Double.parseDouble(data[1]),
                Double.parseDouble(data[2]),
                Server.getInstance().getLevelByName(data[3])
        );
    }

    public static String positionToString(Position position) {
        return String.format("%s:%s:%s:%s",
                position.getX(),
                position.getY(),
                position.getZ(),
                position.getLevel().getFolderName()
        );
    }
}
