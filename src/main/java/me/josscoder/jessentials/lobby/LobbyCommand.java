package me.josscoder.jessentials.lobby;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.nukkit.JBridgeNukkit;
import me.josscoder.jessentials.JEssentialsPlugin;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby",
                "Return to lobby",
                "/lobby",
                new String[]{"hub"}
        );
        setPermission("lobby.permission");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender.isPlayer() && args.length == 0) {
            Player player = (Player) sender;

            String sortedLobbyService = JEssentialsPlugin.getInstance()
                    .getLobbyManager()
                    .geSortedLobbyServiceShortId();
            //TODO: impl party
            if (sortedLobbyService.isEmpty()) {
                player.sendMessage(TextFormat.RED + "No rotating lobby servers!");
                return false;
            }

            JBridgeNukkit.getInstance().networkTransfer(player, sortedLobbyService);
            return true;
        }

        if (testPermission(sender) && args.length > 0) {
            LobbyManager manager = JEssentialsPlugin.getInstance().getLobbyManager();

            String childArg = args[0].toLowerCase();

            switch (childArg) {
                case "help":
                    sendCommandList(sender, label);
                    break;
                case "reload":
                    manager.reloadFromConfig();
                    sender.sendMessage(TextFormat.AQUA + "Lobby reloaded successfully!");
                    break;
                default:
                    if (args.length == 2) {
                        String value = args[1];

                        if (childArg.equalsIgnoreCase("addgroup")) {
                            if (manager.containsGroup(value)) {
                                sender.sendMessage(TextFormat.RED + "That group has already been added!");
                                return false;
                            }

                             manager.addGroup(value);
                            sender.sendMessage(TextFormat.GREEN + String.format("You have added the %s group", value));
                        } else if (childArg.equalsIgnoreCase("removegroup")) {
                            if (!manager.containsGroup(value)) {
                                sender.sendMessage(TextFormat.RED + "That group has not been added!");
                                return false;
                            }

                            manager.removeGroup(value);
                            sender.sendMessage(TextFormat.GREEN + String.format("You have removed the %s group", value));
                        } else if (childArg.equalsIgnoreCase("setsortmode")) {
                            manager.setSortMode(value);
                            sender.sendMessage(TextFormat.GREEN + "You have set the sortmode to " + value);
                        }

                        return true;
                    }

                    sender.sendMessage(TextFormat.RED + String.format(
                            "Usage: /%s <addgroup|removegroup|setsortmode> <value>",
                            label
                    ));
                    break;
            }

            return true;
        }

        return true;
    }

    private void sendCommandList(CommandSender sender, String label) {
        sender.sendMessage(TextFormat.colorize("&9&lLobby Plugin&r" +
                "\n" +
                "&7- /% help: &fShow command list" +
                "\n" +
                "&7- /% reload: &fReload the configuration" +
                "\n" +
                "&7- /% addgroup <group>: &fAdd group"  +
                "\n" +
                "&7- /% removegroup <group>: &fRemove group"   +
                "\n" +
                "&7- /% setsortmode <RANDOM|LOWEST|FILL>: &fSet sortmode"
        ).replace("%", label));
    }
}