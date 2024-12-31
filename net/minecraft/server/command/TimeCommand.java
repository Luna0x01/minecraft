package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class TimeCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "time";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.time.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length > 1) {
			if ("set".equals(args[0])) {
				int i;
				if ("day".equals(args[1])) {
					i = 1000;
				} else if ("night".equals(args[1])) {
					i = 13000;
				} else {
					i = parseClampedInt(args[1], 0);
				}

				this.method_12478(minecraftServer, i);
				run(commandSource, this, "commands.time.set", new Object[]{i});
				return;
			}

			if ("add".equals(args[0])) {
				int l = parseClampedInt(args[1], 0);
				this.method_15(minecraftServer, l);
				run(commandSource, this, "commands.time.added", new Object[]{l});
				return;
			}

			if ("query".equals(args[0])) {
				if ("daytime".equals(args[1])) {
					int m = (int)(commandSource.getWorld().getTimeOfDay() % 24000L);
					commandSource.setStat(CommandStats.Type.QUERY_RESULT, m);
					run(commandSource, this, "commands.time.query", new Object[]{m});
					return;
				}

				if ("day".equals(args[1])) {
					int n = (int)(commandSource.getWorld().getTimeOfDay() / 24000L % 2147483647L);
					commandSource.setStat(CommandStats.Type.QUERY_RESULT, n);
					run(commandSource, this, "commands.time.query", new Object[]{n});
					return;
				}

				if ("gametime".equals(args[1])) {
					int o = (int)(commandSource.getWorld().getLastUpdateTime() % 2147483647L);
					commandSource.setStat(CommandStats.Type.QUERY_RESULT, o);
					run(commandSource, this, "commands.time.query", new Object[]{o});
					return;
				}
			}
		}

		throw new IncorrectUsageException("commands.time.usage");
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"set", "add", "query"});
		} else if (strings.length == 2 && "set".equals(strings[0])) {
			return method_2894(strings, new String[]{"day", "night"});
		} else {
			return strings.length == 2 && "query".equals(strings[0]) ? method_2894(strings, new String[]{"daytime", "gametime", "day"}) : Collections.emptyList();
		}
	}

	protected void method_12478(MinecraftServer minecraftServer, int i) {
		for (int j = 0; j < minecraftServer.worlds.length; j++) {
			minecraftServer.worlds[j].setTimeOfDay((long)i);
		}
	}

	protected void method_15(MinecraftServer minecraftServer, int i) {
		for (int j = 0; j < minecraftServer.worlds.length; j++) {
			ServerWorld serverWorld = minecraftServer.worlds[j];
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)i);
		}
	}
}
