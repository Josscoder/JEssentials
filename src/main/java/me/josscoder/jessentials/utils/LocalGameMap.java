package me.josscoder.jessentials.utils;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import lombok.Getter;
import me.josscoder.jessentials.JEssentialsPlugin;

import java.io.File;
import java.io.IOException;

public class LocalGameMap {

    private final String gameId;

    private final File sourceWorldFolder;
    private File activeWorldFolder;

    public static final String WORLDS_PATH = Server.getInstance().getDataPath() + "worlds/";

    @Getter
    private Level world;

    public LocalGameMap(String gameId, String backupPath, String worldName, boolean loadOnInit) {
        this.gameId = gameId;
        sourceWorldFolder = new File(backupPath, worldName);
        if (loadOnInit) load();
    }

    public boolean load() {
        if (isLoaded()) return true;

        activeWorldFolder = new File(
                WORLDS_PATH,
                gameId + "_" + sourceWorldFolder.getName() + "_" + System.currentTimeMillis()
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
