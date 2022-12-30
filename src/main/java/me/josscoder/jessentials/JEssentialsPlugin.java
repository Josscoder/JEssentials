package me.josscoder.jessentials;

import cn.nukkit.command.Command;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import com.denzelcode.form.FormAPI;
import lombok.Getter;
import me.iwareq.scoreboard.ScoreboardAPI;
import me.josscoder.jessentials.customitem.ItemManager;
import me.josscoder.jessentials.luckperms.LuckPermsAPI;
import me.josscoder.jessentials.lobby.LobbyCommand;
import me.josscoder.jessentials.luckformat.LuckFormatCommand;
import me.josscoder.jessentials.lobby.LobbyManager;
import me.josscoder.jessentials.luckformat.LuckFormatManager;
import me.josscoder.jessentials.worldprotect.WorldProtectCommand;
import me.josscoder.jessentials.worldprotect.WorldProtectManager;

import java.util.Arrays;

@Getter
public class JEssentialsPlugin extends PluginBase {

    @Getter
    private static JEssentialsPlugin instance;

    private LuckFormatManager luckFormatManager;
    private LobbyManager lobbyManager;
    private ItemManager itemManager;
    private WorldProtectManager worldProtectManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        handleDebugTPSDrop();

        loadAPIS();
        loadManagers();

        registerListener(
                new NetworkListener(),
                luckFormatManager,
                itemManager
        );

        registerCommand(
                new LuckFormatCommand(),
                new LobbyCommand(),
                new WorldProtectCommand()
        );
    }

    private void handleDebugTPSDrop() {
        if (!getConfig().getBoolean("debug-tps-drop")) return;

        getLogger().info("I will notify you when there is a drop in TPS");

        getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            float tps = getServer().getTicksPerSecond();
            if ((int) tps >= 20) return;

            String message = "There was a drop in Ticks per Second (TPS): " + tps;

            getServer().getOnlinePlayers()
                    .values()
                    .stream()
                    .filter(player -> player.hasPermission("debug.tps.drop.fall.permission"))
                    .forEach(player -> player.sendMessage(message));

            getLogger().warning(message);
        }, 20);
    }

    private void loadAPIS() {
        LuckPermsAPI luckPermsAPI = new LuckPermsAPI();
        luckPermsAPI.init();

        ScoreboardAPI scoreboardAPI = new ScoreboardAPI();
        scoreboardAPI.init();

        FormAPI.init(this);
    }

    private void loadManagers() {
        luckFormatManager = new LuckFormatManager();
        luckFormatManager.init();

        lobbyManager = new LobbyManager();
        lobbyManager.init();

        itemManager = new ItemManager();
        itemManager.init();

        worldProtectManager = new WorldProtectManager();
        worldProtectManager.init();
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
