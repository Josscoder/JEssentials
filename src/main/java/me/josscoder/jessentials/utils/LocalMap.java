package me.josscoder.jessentials.utils;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import lombok.Getter;
import me.josscoder.jessentials.JEssentialsPlugin;

import java.io.File;
import java.io.IOException;

public class LocalMap {

    private final File sourceWorldFolder;
    private File activeWorldFolder;

    public static final String WORLDS_PATH = Server.getInstance().getDataPath() + "worlds/";
    public static final  String BACKUP_PATH = JEssentialsPlugin.getInstance().getDataFolder() + "/backup/";

    @Getter
    private Level world;

    public LocalMap(String worldName, boolean loadOnInit) {
        sourceWorldFolder = new File(BACKUP_PATH, worldName);
        if (loadOnInit) load();
    }

    public boolean load() {
        if (isLoaded()) return true;

        activeWorldFolder = new File(
                WORLDS_PATH,
                sourceWorldFolder.getName() + "_" + System.currentTimeMillis()
        );

        try {
            FileUtils.copy(sourceWorldFolder, activeWorldFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Server.getInstance().loadLevel(activeWorldFolder.getName());

        world = Server.getInstance().getLevelByName(activeWorldFolder.getName());
        if (world != null) {
            world.setAutoSave(false);
            world.setThundering(false);
            world.setRaining(false);
            world.setTime(Level.TIME_DAY);
            world.stopTime();
        }

        return isLoaded();
    }

    public void unload() {
        if (isLoaded()) world.unload();
        if (activeWorldFolder != null) FileUtils.delete(activeWorldFolder);
        world = null;
        activeWorldFolder = null;
    }

    public boolean restoreFromResource() {
        unload();
        return load();
    }

    public boolean isLoaded() {
        return world != null;
    }
}
