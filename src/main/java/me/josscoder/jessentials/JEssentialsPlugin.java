package me.josscoder.jessentials;

import cn.nukkit.command.Command;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import me.iwareq.scoreboard.ScoreboardAPI;
import me.josscoder.jessentials.api.LuckPermsAPI;
import me.josscoder.jessentials.command.LuckFormatCommand;
import me.josscoder.jessentials.command.PerformanceDebugCommand;
import me.josscoder.jessentials.listener.GeneralListener;
import me.josscoder.jessentials.manager.LuckFormatManager;

import java.util.Arrays;

@Getter
public class JEssentialsPlugin extends PluginBase {

    @Getter
    private static JEssentialsPlugin instance;

    private LuckFormatManager luckFormatManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadAPIS();
        loadManagers();

        registerListener(new GeneralListener(), luckFormatManager);
        registerCommand(new PerformanceDebugCommand(), new LuckFormatCommand());
    }

    private void loadManagers() {
        luckFormatManager = new LuckFormatManager(getConfig());
    }

    private void loadAPIS() {
        LuckPermsAPI luckPermsAPI = new LuckPermsAPI();
        luckPermsAPI.init();

        ScoreboardAPI scoreboardAPI = new ScoreboardAPI();
        scoreboardAPI.init();
    }

    public void registerListener(Listener ...listeners) {
        Arrays.stream(listeners).forEach(listener ->
                getServer().getPluginManager().registerEvents(listener, this)
        );
    }

    public void registerCommand(Command ...commands) {
        Arrays.stream(commands).forEach(command ->
                getServer().getCommandMap().register(command.getName(), command)
        );
    }
}
