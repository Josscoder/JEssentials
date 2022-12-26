package me.josscoder.jessentials;

import cn.nukkit.command.Command;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import me.iwareq.scoreboard.ScoreboardAPI;
import me.josscoder.jessentials.command.PerformanceDebugCommand;
import me.josscoder.jessentials.listener.GeneralListener;

import java.util.Arrays;

public class JEssentialsPlugin extends PluginBase {

    @Getter
    private static JEssentialsPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        new ScoreboardAPI().onEnable();

        registerListener(new GeneralListener());
        registerCommand(new PerformanceDebugCommand());
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
