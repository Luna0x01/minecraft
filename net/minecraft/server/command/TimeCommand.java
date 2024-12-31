package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length > 1) {
			if (args[0].equals("set")) {
				int i;
				if (args[1].equals("day")) {
					i = 1000;
				} else if (args[1].equals("night")) {
					i = 13000;
				} else {
					i = parseClampedInt(args[1], 0);
				}

				this.method_14(source, i);
				run(source, this, "commands.time.set", new Object[]{i});
				return;
			}

			if (args[0].equals("add")) {
				int l = parseClampedInt(args[1], 0);
				this.method_15(source, l);
				run(source, this, "commands.time.added", new Object[]{l});
				return;
			}

			if (args[0].equals("query")) {
				if (args[1].equals("daytime")) {
					int m = (int)(source.getWorld().getTimeOfDay() % 2147483647L);
					source.setStat(CommandStats.Type.QUERY_RESULT, m);
					run(source, this, "commands.time.query", new Object[]{m});
					return;
				}

				if (args[1].equals("gametime")) {
					int n = (int)(source.getWorld().getLastUpdateTime() % 2147483647L);
					source.setStat(CommandStats.Type.QUERY_RESULT, n);
					run(source, this, "commands.time.query", new Object[]{n});
					return;
				}
			}
		}

		throw new IncorrectUsageException("commands.time.usage");
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"set", "add", "query"});
		} else if (args.length == 2 && args[0].equals("set")) {
			return method_2894(args, new String[]{"day", "night"});
		} else {
			return args.length == 2 && args[0].equals("query") ? method_2894(args, new String[]{"daytime", "gametime"}) : null;
		}
	}

	protected void method_14(CommandSource commandSource, int i) {
		for (int j = 0; j < MinecraftServer.getServer().worlds.length; j++) {
			MinecraftServer.getServer().worlds[j].setTimeOfDay((long)i);
		}
	}

	protected void method_15(CommandSource commandSource, int i) {
		for (int j = 0; j < MinecraftServer.getServer().worlds.length; j++) {
			ServerWorld serverWorld = MinecraftServer.getServer().worlds[j];
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)i);
		}
	}
}
