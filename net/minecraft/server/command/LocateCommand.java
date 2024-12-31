package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class LocateCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "locate";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.locate.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length != 1) {
			throw new IncorrectUsageException("commands.locate.usage");
		} else {
			String string = args[0];
			BlockPos blockPos = commandSource.getWorld().method_13688(string, commandSource.getBlockPos(), false);
			if (blockPos != null) {
				commandSource.sendMessage(new TranslatableText("commands.locate.success", string, blockPos.getX(), blockPos.getZ()));
			} else {
				throw new CommandException("commands.locate.failure", string);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1
			? method_2894(strings, new String[]{"Stronghold", "Monument", "Village", "Mansion", "EndCity", "Fortress", "Temple", "Mineshaft"})
			: Collections.emptyList();
	}
}
