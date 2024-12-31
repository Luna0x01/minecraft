package net.minecraft.server.command;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class MeCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "me";
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.me.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.me.usage");
		} else {
			Text text = method_8406(commandSource, args, 0, !(commandSource instanceof PlayerEntity));
			minecraftServer.getPlayerManager().sendToAll(new TranslatableText("chat.type.emote", commandSource.getName(), text));
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return method_2894(strings, server.getPlayerNames());
	}
}
