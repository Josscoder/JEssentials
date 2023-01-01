package me.josscoder.jessentials.utils.worldprotect;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class WorldProtectCommand extends Command {

    public WorldProtectCommand() {
        super("worldprotect",
                "Protect worlds",
                TextFormat.RED + "Usage: /%s help",
                new String[]{"wp"}
        );
        setPermission("worldprotect.permission");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(String.format(getUsage(), label));
            return false;
        }

        WorldProtectManager manager = WorldProtectManager.getInstance();

        String childArg = args[0].toLowerCase();

        switch (childArg) {
            case "help":
                sendCommandList(sender, label);
                break;
            case "reload":
                manager.reloadFromConfig();
                sender.sendMessage(TextFormat.AQUA + "WorldProtect reloaded successfully!");
                break;
            default:
                String world = args[1];

                if (manager.containsWorld(world)) {
                    manager.unprotectWorld(world);
                    sender.sendMessage(TextFormat.RED + String.format("You have unprotected the world '%s'", world));
                } else {
                    manager.protectWorld(world);
                    sender.sendMessage(TextFormat.GREEN + String.format("You have protected the world '%s'", world));
                }
                break;
        }

        return true;
    }

    private void sendCommandList(CommandSender sender, String label) {
        sender.sendMessage(TextFormat.colorize("&b&lWorldProtect Plugin&r" +
                "\n" +
                "&7- /% help: &fShow command list" +
                "\n" +
                "&7- /% reload: &fReload the configuration" +
                "\n" +
                "&7- /% <world>: &fToggle allow or not world protection"
        ).replace("%", label));
    }
}
