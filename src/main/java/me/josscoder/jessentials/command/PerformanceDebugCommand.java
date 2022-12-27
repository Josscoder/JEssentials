package me.josscoder.jessentials.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class PerformanceDebugCommand extends Command {

    public PerformanceDebugCommand() {
        super("performancedebug",
                "Show the performance of your server",
                "/performancedebug",
                new String[]{"pdebug"}
        );
        setPermission("performancedebug.permission");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return true;
    }
}
