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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "worldborder";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.worldborder.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.worldborder.usage");
		} else {
			WorldBorder worldBorder = this.method_12552(minecraftServer);
			if (args[0].equals("set")) {
				if (args.length != 2 && args.length != 3) {
					throw new IncorrectUsageException("commands.worldborder.set.usage");
				}

				double d = worldBorder.getTargetSize();
				double e = parseClampedDouble(args[1], 1.0, 6.0E7);
				long l = args.length > 2 ? parseClampedLong(args[2], 0L, 9223372036854775L) * 1000L : 0L;
				if (l > 0L) {
					worldBorder.interpolateSize(d, e, l);
					if (d > e) {
						run(
							commandSource,
							this,
							"commands.worldborder.setSlowly.shrink.success",
							new Object[]{String.format("%.1f", e), String.format("%.1f", d), Long.toString(l / 1000L)}
						);
					} else {
						run(
							commandSource,
							this,
							"commands.worldborder.setSlowly.grow.success",
							new Object[]{String.format("%.1f", e), String.format("%.1f", d), Long.toString(l / 1000L)}
						);
					}
				} else {
					worldBorder.setSize(e);
					run(commandSource, this, "commands.worldborder.set.success", new Object[]{String.format("%.1f", e), String.format("%.1f", d)});
				}
			} else if (args[0].equals("add")) {
				if (args.length != 2 && args.length != 3) {
					throw new IncorrectUsageException("commands.worldborder.add.usage");
				}

				double f = worldBorder.getOldSize();
				double g = f + parseClampedDouble(args[1], -f, 6.0E7 - f);
				long m = worldBorder.getInterpolationDuration() + (args.length > 2 ? parseClampedLong(args[2], 0L, 9223372036854775L) * 1000L : 0L);
				if (m > 0L) {
					worldBorder.interpolateSize(f, g, m);
					if (f > g) {
						run(
							commandSource,
							this,
							"commands.worldborder.setSlowly.shrink.success",
							new Object[]{String.format("%.1f", g), String.format("%.1f", f), Long.toString(m / 1000L)}
						);
					} else {
						run(
							commandSource,
							this,
							"commands.worldborder.setSlowly.grow.success",
							new Object[]{String.format("%.1f", g), String.format("%.1f", f), Long.toString(m / 1000L)}
						);
					}
				} else {
					worldBorder.setSize(g);
					run(commandSource, this, "commands.worldborder.set.success", new Object[]{String.format("%.1f", g), String.format("%.1f", f)});
				}
			} else if (args[0].equals("center")) {
				if (args.length != 3) {
					throw new IncorrectUsageException("commands.worldborder.center.usage");
				}

				BlockPos blockPos = commandSource.getBlockPos();
				double h = parseDouble((double)blockPos.getX() + 0.5, args[1], true);
				double i = parseDouble((double)blockPos.getZ() + 0.5, args[2], true);
				worldBorder.setCenter(h, i);
				run(commandSource, this, "commands.worldborder.center.success", new Object[]{h, i});
			} else if (args[0].equals("damage")) {
				if (args.length < 2) {
					throw new IncorrectUsageException("commands.worldborder.damage.usage");
				}

				if (args[1].equals("buffer")) {
					if (args.length != 3) {
						throw new IncorrectUsageException("commands.worldborder.damage.buffer.usage");
					}

					double j = parseClampedDouble(args[2], 0.0);
					double k = worldBorder.getSafeZone();
					worldBorder.setSafeZone(j);
					run(commandSource, this, "commands.worldborder.damage.buffer.success", new Object[]{String.format("%.1f", j), String.format("%.1f", k)});
				} else if (args[1].equals("amount")) {
					if (args.length != 3) {
						throw new IncorrectUsageException("commands.worldborder.damage.amount.usage");
					}

					double n = parseClampedDouble(args[2], 0.0);
					double o = worldBorder.getBorderDamagePerBlock();
					worldBorder.setDamagePerBlock(n);
					run(commandSource, this, "commands.worldborder.damage.amount.success", new Object[]{String.format("%.2f", n), String.format("%.2f", o)});
				}
			} else if (args[0].equals("warning")) {
				if (args.length < 2) {
					throw new IncorrectUsageException("commands.worldborder.warning.usage");
				}

				if (args[1].equals("time")) {
					if (args.length != 3) {
						throw new IncorrectUsageException("commands.worldborder.warning.time.usage");
					}

					int p = parseClampedInt(args[2], 0);
					int q = worldBorder.getWarningTime();
					worldBorder.setWarningTime(p);
					run(commandSource, this, "commands.worldborder.warning.time.success", new Object[]{p, q});
				} else if (args[1].equals("distance")) {
					if (args.length != 3) {
						throw new IncorrectUsageException("commands.worldborder.warning.distance.usage");
					}

					int r = parseClampedInt(args[2], 0);
					int s = worldBorder.getWarningBlocks();
					worldBorder.setWarningBlocks(r);
					run(commandSource, this, "commands.worldborder.warning.distance.success", new Object[]{r, s});
				}
			} else {
				if (!args[0].equals("get")) {
					throw new IncorrectUsageException("commands.worldborder.usage");
				}

				double t = worldBorder.getOldSize();
				commandSource.setStat(CommandStats.Type.QUERY_RESULT, MathHelper.floor(t + 0.5));
				commandSource.sendMessage(new TranslatableText("commands.worldborder.get.success", String.format("%.0f", t)));
			}
		}
	}

	protected WorldBorder method_12552(MinecraftServer minecraftServer) {
		return minecraftServer.worlds[0].getWorldBorder();
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"set", "center", "damage", "warning", "add", "get"});
		} else if (strings.length == 2 && strings[0].equals("damage")) {
			return method_2894(strings, new String[]{"buffer", "amount"});
		} else if (strings.length >= 2 && strings.length <= 3 && strings[0].equals("center")) {
			return method_10712(strings, 1, pos);
		} else {
			return strings.length == 2 && strings[0].equals("warning") ? method_2894(strings, new String[]{"time", "distance"}) : Collections.emptyList();
		}
	}
}
