package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.me.usage");
		} else {
			Text text = method_8406(source, args, 0, !(source instanceof PlayerEntity));
			MinecraftServer.getServer().getPlayerManager().sendToAll(new TranslatableText("chat.type.emote", source.getName(), text));
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return method_2894(args, MinecraftServer.getServer().getPlayerNames());
	}
}
