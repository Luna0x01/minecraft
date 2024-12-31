package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StatsCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "stats";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.stats.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.stats.usage");
		} else {
			boolean bl;
			if ("entity".equals(args[0])) {
				bl = false;
			} else {
				if (!"block".equals(args[0])) {
					throw new IncorrectUsageException("commands.stats.usage");
				}

				bl = true;
			}

			int i;
			if (bl) {
				if (args.length < 5) {
					throw new IncorrectUsageException("commands.stats.block.usage");
				}

				i = 4;
			} else {
				if (args.length < 3) {
					throw new IncorrectUsageException("commands.stats.entity.usage");
				}

				i = 2;
			}

			String string = args[i++];
			if ("set".equals(string)) {
				if (args.length < i + 3) {
					if (i == 5) {
						throw new IncorrectUsageException("commands.stats.block.set.usage");
					}

					throw new IncorrectUsageException("commands.stats.entity.set.usage");
				}
			} else {
				if (!"clear".equals(string)) {
					throw new IncorrectUsageException("commands.stats.usage");
				}

				if (args.length < i + 1) {
					if (i == 5) {
						throw new IncorrectUsageException("commands.stats.block.clear.usage");
					}

					throw new IncorrectUsageException("commands.stats.entity.clear.usage");
				}
			}

			CommandStats.Type type = CommandStats.Type.getByName(args[i++]);
			if (type == null) {
				throw new CommandException("commands.stats.failed");
			} else {
				World world = commandSource.getWorld();
				CommandStats commandStats;
				if (bl) {
					BlockPos blockPos = getBlockPos(commandSource, args, 1, false);
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity == null) {
						throw new CommandException("commands.stats.noCompatibleBlock", blockPos.getX(), blockPos.getY(), blockPos.getZ());
					}

					if (blockEntity instanceof CommandBlockBlockEntity) {
						commandStats = ((CommandBlockBlockEntity)blockEntity).getCommandStats();
					} else {
						if (!(blockEntity instanceof SignBlockEntity)) {
							throw new CommandException("commands.stats.noCompatibleBlock", blockPos.getX(), blockPos.getY(), blockPos.getZ());
						}

						commandStats = ((SignBlockEntity)blockEntity).getCommandStats();
					}
				} else {
					Entity entity = method_10711(minecraftServer, commandSource, args[1]);
					commandStats = entity.getCommandStats();
				}

				if ("set".equals(string)) {
					String string2 = args[i++];
					String string3 = args[i];
					if (string2.isEmpty() || string3.isEmpty()) {
						throw new CommandException("commands.stats.failed");
					}

					CommandStats.method_10795(commandStats, type, string2, string3);
					run(commandSource, this, "commands.stats.success", new Object[]{type.getName(), string3, string2});
				} else if ("clear".equals(string)) {
					CommandStats.method_10795(commandStats, type, null, null);
					run(commandSource, this, "commands.stats.cleared", new Object[]{type.getName()});
				}

				if (bl) {
					BlockPos blockPos2 = getBlockPos(commandSource, args, 1, false);
					BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
					blockEntity2.markDirty();
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"entity", "block"});
		} else if (strings.length == 2 && "entity".equals(strings[0])) {
			return method_2894(strings, server.getPlayerNames());
		} else if (strings.length >= 2 && strings.length <= 4 && "block".equals(strings[0])) {
			return method_10707(strings, 1, pos);
		} else if ((strings.length != 3 || !"entity".equals(strings[0])) && (strings.length != 5 || !"block".equals(strings[0]))) {
			if ((strings.length != 4 || !"entity".equals(strings[0])) && (strings.length != 6 || !"block".equals(strings[0]))) {
				return (strings.length != 6 || !"entity".equals(strings[0])) && (strings.length != 8 || !"block".equals(strings[0]))
					? Collections.emptyList()
					: method_10708(strings, this.method_12227(server));
			} else {
				return method_2894(strings, CommandStats.Type.getValues());
			}
		} else {
			return method_2894(strings, new String[]{"set", "clear"});
		}
	}

	protected List<String> method_12227(MinecraftServer minecraftServer) {
		Collection<ScoreboardObjective> collection = minecraftServer.getWorld(0).getScoreboard().getObjectives();
		List<String> list = Lists.newArrayList();

		for (ScoreboardObjective scoreboardObjective : collection) {
			if (!scoreboardObjective.getCriterion().method_4919()) {
				list.add(scoreboardObjective.getName());
			}
		}

		return list;
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return args.length > 0 && "entity".equals(args[0]) && index == 1;
	}
}
