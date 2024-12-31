package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SayCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "say";
	}

	@Override
	public int getPermissionLevel() {
		return 1;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.say.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length > 0 && args[0].length() > 0) {
			Text text = method_8406(commandSource, args, 0, true);
			minecraftServer.getPlayerManager().sendToAll(new TranslatableText("chat.type.announcement", commandSource.getName(), text));
		} else {
			throw new IncorrectUsageException("commands.say.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length >= 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}
}
