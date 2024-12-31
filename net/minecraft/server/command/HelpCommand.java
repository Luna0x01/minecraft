package net.minecraft.server.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.CommandBlockExecutor;

public class HelpCommand extends AbstractCommand {
	private static final String[] field_12651 = new String[]{
		"Yolo",
		"/achievement take achievement.understandCommands @p",
		"Ask for help on twitter",
		"/deop @p",
		"Scoreboard deleted, commands blocked",
		"Contact helpdesk for help",
		"/testfornoob @p",
		"/trigger warning",
		"Oh my god, it's full of stats",
		"/kill @p[name=!Searge]",
		"Have you tried turning it off and on again?",
		"Sorry, no help today"
	};
	private final Random field_12652 = new Random();

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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (commandSource instanceof CommandBlockExecutor) {
			commandSource.sendMessage(new LiteralText("Searge says: ").append(field_12651[this.field_12652.nextInt(field_12651.length) % field_12651.length]));
		} else {
			List<Command> list = this.method_11608(commandSource, minecraftServer);
			int i = 7;
			int j = (list.size() - 1) / 7;
			int k = 0;

			try {
				k = args.length == 0 ? 0 : parseClampedInt(args[0], 1, j + 1) - 1;
			} catch (InvalidNumberException var13) {
				Map<String, Command> map = this.method_11609(minecraftServer);
				Command command = (Command)map.get(args[0]);
				if (command != null) {
					throw new IncorrectUsageException(command.getUsageTranslationKey(commandSource));
				}

				if (MathHelper.parseInt(args[0], -1) != -1) {
					throw var13;
				}

				throw new NotFoundException();
			}

			int l = Math.min((k + 1) * 7, list.size());
			TranslatableText translatableText = new TranslatableText("commands.help.header", k + 1, j + 1);
			translatableText.getStyle().setFormatting(Formatting.DARK_GREEN);
			commandSource.sendMessage(translatableText);

			for (int m = k * 7; m < l; m++) {
				Command command2 = (Command)list.get(m);
				TranslatableText translatableText2 = new TranslatableText(command2.getUsageTranslationKey(commandSource));
				translatableText2.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command2.getCommandName() + " "));
				commandSource.sendMessage(translatableText2);
			}

			if (k == 0 && commandSource instanceof PlayerEntity) {
				TranslatableText translatableText3 = new TranslatableText("commands.help.footer");
				translatableText3.getStyle().setFormatting(Formatting.GREEN);
				commandSource.sendMessage(translatableText3);
			}
		}
	}

	protected List<Command> method_11608(CommandSource commandSource, MinecraftServer minecraftServer) {
		List<Command> list = minecraftServer.getCommandManager().method_3309(commandSource);
		Collections.sort(list);
		return list;
	}

	protected Map<String, Command> method_11609(MinecraftServer minecraftServer) {
		return minecraftServer.getCommandManager().getCommandMap();
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			Set<String> set = this.method_11609(server).keySet();
			return method_2894(strings, (String[])set.toArray(new String[set.size()]));
		} else {
			return Collections.emptyList();
		}
	}
}
