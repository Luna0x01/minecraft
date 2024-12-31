package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.PlayerSelector;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CommandRegistry implements CommandRegistryProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, Command> commandMap = Maps.newHashMap();
	private final Set<Command> commands = Sets.newHashSet();

	@Override
	public int execute(CommandSource source, String name) {
		name = name.trim();
		if (name.startsWith("/")) {
			name = name.substring(1);
		}

		String[] strings = name.split(" ");
		String string = strings[0];
		strings = method_3104(strings);
		Command command = (Command)this.commandMap.get(string);
		int i = 0;

		try {
			int j = this.method_4642(command, strings);
			if (command == null) {
				TranslatableText translatableText = new TranslatableText("commands.generic.notFound");
				translatableText.getStyle().setFormatting(Formatting.RED);
				source.sendMessage(translatableText);
			} else if (command.method_3278(this.getServer(), source)) {
				if (j > -1) {
					List<Entity> list = PlayerSelector.method_10866(source, strings[j], Entity.class);
					String string2 = strings[j];
					source.setStat(CommandStats.Type.AFFECTED_ENTITIES, list.size());
					if (list.isEmpty()) {
						throw new PlayerNotFoundException("commands.generic.selector.notFound", strings[j]);
					}

					for (Entity entity : list) {
						strings[j] = entity.getEntityName();
						if (this.method_10732(source, strings, command, name)) {
							i++;
						}
					}

					strings[j] = string2;
				} else {
					source.setStat(CommandStats.Type.AFFECTED_ENTITIES, 1);
					if (this.method_10732(source, strings, command, name)) {
						i++;
					}
				}
			} else {
				TranslatableText translatableText2 = new TranslatableText("commands.generic.permission");
				translatableText2.getStyle().setFormatting(Formatting.RED);
				source.sendMessage(translatableText2);
			}
		} catch (CommandException var12) {
			TranslatableText translatableText3 = new TranslatableText(var12.getMessage(), var12.getArgs());
			translatableText3.getStyle().setFormatting(Formatting.RED);
			source.sendMessage(translatableText3);
		}

		source.setStat(CommandStats.Type.SUCCESS_COUNT, i);
		return i;
	}

	protected boolean method_10732(CommandSource commandSource, String[] strings, Command command, String string) {
		try {
			command.method_3279(this.getServer(), commandSource, strings);
			return true;
		} catch (IncorrectUsageException var7) {
			TranslatableText translatableText = new TranslatableText("commands.generic.usage", new TranslatableText(var7.getMessage(), var7.getArgs()));
			translatableText.getStyle().setFormatting(Formatting.RED);
			commandSource.sendMessage(translatableText);
		} catch (CommandException var8) {
			TranslatableText translatableText2 = new TranslatableText(var8.getMessage(), var8.getArgs());
			translatableText2.getStyle().setFormatting(Formatting.RED);
			commandSource.sendMessage(translatableText2);
		} catch (Throwable var9) {
			TranslatableText translatableText3 = new TranslatableText("commands.generic.exception");
			translatableText3.getStyle().setFormatting(Formatting.RED);
			commandSource.sendMessage(translatableText3);
			LOGGER.warn("Couldn't process command: " + string, var9);
		}

		return false;
	}

	protected abstract MinecraftServer getServer();

	public Command registerCommand(Command command) {
		this.commandMap.put(command.getCommandName(), command);
		this.commands.add(command);

		for (String string : command.getAliases()) {
			Command command2 = (Command)this.commandMap.get(string);
			if (command2 == null || !command2.getCommandName().equals(string)) {
				this.commandMap.put(string, command);
			}
		}

		return command;
	}

	private static String[] method_3104(String[] strings) {
		String[] strings2 = new String[strings.length - 1];
		System.arraycopy(strings, 1, strings2, 0, strings.length - 1);
		return strings2;
	}

	@Override
	public List<String> getCompletions(CommandSource source, String name, @Nullable BlockPos pos) {
		String[] strings = name.split(" ", -1);
		String string = strings[0];
		if (strings.length == 1) {
			List<String> list = Lists.newArrayList();

			for (Entry<String, Command> entry : this.commandMap.entrySet()) {
				if (AbstractCommand.method_2883(string, (String)entry.getKey()) && ((Command)entry.getValue()).method_3278(this.getServer(), source)) {
					list.add(entry.getKey());
				}
			}

			return list;
		} else {
			if (strings.length > 1) {
				Command command = (Command)this.commandMap.get(string);
				if (command != null && command.method_3278(this.getServer(), source)) {
					return command.method_10738(this.getServer(), source, method_3104(strings), pos);
				}
			}

			return Collections.emptyList();
		}
	}

	@Override
	public List<Command> method_3309(CommandSource source) {
		List<Command> list = Lists.newArrayList();

		for (Command command : this.commands) {
			if (command.method_3278(this.getServer(), source)) {
				list.add(command);
			}
		}

		return list;
	}

	@Override
	public Map<String, Command> getCommandMap() {
		return this.commandMap;
	}

	private int method_4642(Command command, String[] args) throws CommandException {
		if (command == null) {
			return -1;
		} else {
			for (int i = 0; i < args.length; i++) {
				if (command.isUsernameAtIndex(args, i) && PlayerSelector.method_4088(args[i])) {
					return i;
				}
			}

			return -1;
		}
	}
}
