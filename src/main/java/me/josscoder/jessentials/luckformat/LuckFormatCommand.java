package me.josscoder.jessentials.luckformat;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jessentials.JEssentialsPlugin;

public class LuckFormatCommand extends Command {

    public LuckFormatCommand() {
        super("luckformat",
                "Handles the format of LuckPerms groups",
                TextFormat.RED + "/%s help",
                new String[]{"lf"}
        );
        setPermission("luckformat.permission");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        LuckFormatManager manager = JEssentialsPlugin.getInstance().getLuckFormatManager();

        if (args.length == 0) {
            sender.sendMessage(String.format(getUsage(), label));
            return false;
        }

        String childArg = args[0].toLowerCase();

        switch (childArg) {
            case "help":
                sendCommandList(sender, label);
                break;
            case "reload":
                manager.reloadFromConfig();
                sender.sendMessage(TextFormat.AQUA + "LuckFormat reloaded successfully!");
                break;
            case "togglechatformat":
                boolean allowChatFormat = !manager.allowChatFormat();
                manager.setAllowChatFormat(allowChatFormat);

                if (allowChatFormat) {
                    sender.sendMessage(TextFormat.GREEN + "You have allowed the chat format!");
                } else {
                    sender.sendMessage(TextFormat.RED + "You have restricted the chat format!");
                }
                break;
            case "toggletagformat":
                boolean allowTagFormat = !manager.allowTagFormat();
                manager.setAllowTagFormat(allowTagFormat);

                if (allowTagFormat) {
                    sender.sendMessage(TextFormat.GREEN + "You have allowed the tag format!");
                } else {
                    sender.sendMessage(TextFormat.RED + "You have restricted the tag format!");
                }
                break;
            default:
                if (args.length == 2) {
                    String value = args[1];

                    if (childArg.equalsIgnoreCase("setchatformat")) {
                        manager.setChatFormat(value);
                        sender.sendMessage(TextFormat.GREEN + "You have established a new chat format");
                    } else if (childArg.equalsIgnoreCase("settagformat")) {
                        manager.setTagFormat(value);
                        sender.sendMessage(TextFormat.GREEN + "You have established a new tag format");
                    }

                    return true;
                }

                sender.sendMessage(TextFormat.RED + String.format(
                        "Usage: /%s <setchatformat|settagformat> <value>",
                        label
                ));
                break;
        }

        return true;
    }

    private void sendCommandList(CommandSender sender, String label) {
        sender.sendMessage(TextFormat.colorize("&2&lLuckFormat Plugin&r" +
                "\n" +
                "&7- /% help: &fShow command list" +
                "\n" +
                "&7- /% reload: &fReload the configuration" +
                "\n" +
                "&7- /% togglechatformat: &fToggle allow chat format"  +
                "\n" +
                "&7- /% toggletagformat: &fToggle allow tag format"   +
                "\n" +
                "&7- /% setchatformat: &fSet chat format"   +
                "\n" +
                "&7- /% settagformat: &fSet tag format"
        ).replace("%", label));
    }
}
