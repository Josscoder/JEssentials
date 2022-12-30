package me.josscoder.jessentials.manager;

import cn.nukkit.utils.Config;

public abstract class Manager implements IManager {

    protected final Config config;

    public Manager(Config config) {
        this.config = config;
    }
}
