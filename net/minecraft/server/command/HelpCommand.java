package net.minecraft.server.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.command.NotFoundException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class HelpCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.help.usage";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("?");
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		List<Command> list = this.method_3863(source);
		int i = 7;
		int j = (list.size() - 1) / 7;
		int k = 0;

		try {
			k = args.length == 0 ? 0 : parseClampedInt(args[0], 1, j + 1) - 1;
		} catch (InvalidNumberException var12) {
			Map<String, Command> map = this.method_3862();
			Command command = (Command)map.get(args[0]);
			if (command != null) {
				throw new IncorrectUsageException(command.getUsageTranslationKey(source));
			}

			if (MathHelper.parseInt(args[0], -1) != -1) {
				throw var12;
			}

			throw new NotFoundException();
		}

		int l = Math.min((k + 1) * 7, list.size());
		TranslatableText translatableText = new TranslatableText("commands.help.header", k + 1, j + 1);
		translatableText.getStyle().setFormatting(Formatting.DARK_GREEN);
		source.sendMessage(translatableText);

		for (int m = k * 7; m < l; m++) {
			Command command2 = (Command)list.get(m);
			TranslatableText translatableText2 = new TranslatableText(command2.getUsageTranslationKey(source));
			translatableText2.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command2.getCommandName() + " "));
			source.sendMessage(translatableText2);
		}

		if (k == 0 && source instanceof PlayerEntity) {
			TranslatableText translatableText3 = new TranslatableText("commands.help.footer");
			translatableText3.getStyle().setFormatting(Formatting.GREEN);
			source.sendMessage(translatableText3);
		}
	}

	protected List<Command> method_3863(CommandSource commandSource) {
		List<Command> list = MinecraftServer.getServer().getCommandManager().method_3309(commandSource);
		Collections.sort(list);
		return list;
	}

	protected Map<String, Command> method_3862() {
		return MinecraftServer.getServer().getCommandManager().getCommandMap();
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			Set<String> set = this.method_3862().keySet();
			return method_2894(args, (String[])set.toArray(new String[set.size()]));
		} else {
			return null;
		}
	}
}
