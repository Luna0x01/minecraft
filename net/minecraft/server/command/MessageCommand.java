package net.minecraft.server.command;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.message.usage");
		} else {
			PlayerEntity playerEntity = method_4639(minecraftServer, commandSource, args[0]);
			if (playerEntity == commandSource) {
				throw new PlayerNotFoundException("commands.message.sameTarget");
			} else {
				Text text = method_8406(commandSource, args, 1, !(commandSource instanceof PlayerEntity));
				TranslatableText translatableText = new TranslatableText("commands.message.display.incoming", commandSource.getName(), text.copy());
				TranslatableText translatableText2 = new TranslatableText("commands.message.display.outgoing", playerEntity.getName(), text.copy());
				translatableText.getStyle().setFormatting(Formatting.GRAY).setItalic(true);
				translatableText2.getStyle().setFormatting(Formatting.GRAY).setItalic(true);
				playerEntity.sendMessage(translatableText);
				commandSource.sendMessage(translatableText2);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return method_2894(strings, server.getPlayerNames());
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
