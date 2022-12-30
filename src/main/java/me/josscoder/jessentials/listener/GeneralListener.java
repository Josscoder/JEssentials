package me.josscoder.jessentials.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.josscoder.jessentials.JEssentialsPlugin;

public class GeneralListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        JEssentialsPlugin.getInstance()
                .getItemManager()
                .switchGroupItems("lobby", event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }
}
