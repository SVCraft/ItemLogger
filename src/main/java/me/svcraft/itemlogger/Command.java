package me.svcraft.itemlogger;

import me.svcraft.minigames.command.SubCommandedCommand;
import me.svcraft.minigames.command.subcommand.SubCommand;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

public class Command extends SubCommandedCommand {
    private ItemLogger itemLogger;

    public Command(ItemLogger itemLogger) {
        super(itemLogger);
        this.itemLogger = itemLogger;

        this.addSubCommand(new SubCommand("reload", (sender, args) -> {
            try {
                this.itemLogger.reload();
                this.itemLogger.msg(sender, "command.reload.success");
            } catch (IOException | InvalidConfigurationException e) {
                this.itemLogger.crash("reload command", e);
                this.itemLogger.msg(sender, "command.reload.fail");
            }
        }));
    }
}
