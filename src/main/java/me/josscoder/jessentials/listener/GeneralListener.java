package me.josscoder.jessentials.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.iwareq.scoreboard.Scoreboard;
import me.iwareq.scoreboard.packet.data.DisplaySlot;

public class GeneralListener implements Listener {

    private final Scoreboard scoreboard;

    public GeneralListener() {
        scoreboard = new Scoreboard("Test Scoreboard", DisplaySlot.SIDEBAR, 20);
        scoreboard.setHandler(player -> {
            scoreboard.addLine(player.getName());
            scoreboard.addLine("§1"); // used for skip line
            scoreboard.addLine("random: " + Math.random());
            scoreboard.addLine("random: " + Math.random());
            scoreboard.addLine("random: " + Math.random());
            scoreboard.addLine("random: " + Math.random());
            scoreboard.addLine("random: " + Math.random());
            scoreboard.addLine("§2"); // used for skip line
            scoreboard.addLine("Online: " + Server.getInstance().getOnlinePlayers().size());
        });
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        scoreboard.show(event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        scoreboard.hide(event.getPlayer());
    }
}
