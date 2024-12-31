package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3289;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class FunctionCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "function";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.function.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length != 1 && args.length != 3) {
			throw new IncorrectUsageException("commands.function.usage");
		} else {
			Identifier identifier = new Identifier(args[0]);
			Function function = minecraftServer.method_14911().getFunction(identifier);
			if (function == null) {
				throw new CommandException("commands.function.unknown", identifier);
			} else {
				if (args.length == 3) {
					String string = args[1];
					boolean bl;
					if ("if".equals(string)) {
						bl = true;
					} else {
						if (!"unless".equals(string)) {
							throw new IncorrectUsageException("commands.function.usage");
						}

						bl = false;
					}

					boolean bl4 = false;

					try {
						bl4 = !method_12704(minecraftServer, commandSource, args[2]).isEmpty();
					} catch (EntityNotFoundException var10) {
					}

					if (bl != bl4) {
						throw new CommandException("commands.function.skipped", identifier);
					}
				}

				int i = minecraftServer.method_14911().execute(function, class_3289.method_14640(commandSource).method_14643().method_14639(2).method_14642(false));
				run(commandSource, this, "commands.function.success", new Object[]{identifier, i});
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_10708(strings, server.method_14911().getFunctions().keySet());
		} else if (strings.length == 2) {
			return method_2894(strings, new String[]{"if", "unless"});
		} else {
			return strings.length == 3 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
		}
	}
}
