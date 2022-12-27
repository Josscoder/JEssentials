package me.josscoder.jessentials.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class LuckFormatCommand extends Command {

    public LuckFormatCommand() {
        super("luckformat",
                "Handles the format of LuckPerms groups",
                "/luckformat",
                new String[]{"lf"}
        );
        setPermission("luckformat.permission");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return true;
    }
}
