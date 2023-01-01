package me.josscoder.jessentials.utils.lobby;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.denzelcode.form.window.SimpleWindowForm;
import me.josscoder.jbridge.nukkit.JBridgeNukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class LobbySelectorCommand extends Command {
    public LobbySelectorCommand() {
        super("lobbyselector",
                "Select a lobby",
                "/lobbyselector",
                new String[]{"lobbyswitcher"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !LobbyManager.getInstance().allowSelector()) return false;
        Player player = (Player) sender;

        SimpleWindowForm windowForm = new SimpleWindowForm("Lobby Selector");

        AtomicInteger i = new AtomicInteger(1);
        LobbyManager.getInstance().getLobbyServices().forEach(lobby -> {
            boolean containsPlayer = lobby.containsPlayer(player.getName());

            windowForm.addButton(lobby.getShortId(),
                    TextFormat.colorize(String.format("&bLobby #%s &8(%s/%s)\n%s",
                            i,
                            lobby.getPlayersOnline(),
                            lobby.getMaxPlayers(),
                            containsPlayer ? "&cConnected" : "&2Click to connect"
                    )),
                    "textures/items/" + (containsPlayer ? "door_iron" : "door_wood") + ".png"
            );
            i.getAndIncrement();
        });
        windowForm.addHandler(e -> {
            if (e.wasClosed()) return;
            JBridgeNukkit.getInstance().transferPlayer(player, e.getButton().getName());
        });
        windowForm.sendTo(player);

        return true;
    }
}
