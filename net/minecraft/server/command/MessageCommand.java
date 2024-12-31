package net.minecraft.server.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class MessageCommand extends AbstractCommand {
	@Override
	public List<String> getAliases() {
		return Arrays.asList("w", "msg");
	}

	@Override
	public String getCommandName() {
		return "tell";
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.message.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.message.usage");
		} else {
			PlayerEntity playerEntity = getPlayer(source, args[0]);
			if (playerEntity == source) {
				throw new PlayerNotFoundException("commands.message.sameTarget");
			} else {
				Text text = method_8406(source, args, 1, !(source instanceof PlayerEntity));
				TranslatableText translatableText = new TranslatableText("commands.message.display.incoming", source.getName(), text.copy());
				TranslatableText translatableText2 = new TranslatableText("commands.message.display.outgoing", playerEntity.getName(), text.copy());
				translatableText.getStyle().setFormatting(Formatting.GRAY).setItalic(true);
				translatableText2.getStyle().setFormatting(Formatting.GRAY).setItalic(true);
				playerEntity.sendMessage(translatableText);
				source.sendMessage(translatableText2);
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return method_2894(args, MinecraftServer.getServer().getPlayerNames());
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
