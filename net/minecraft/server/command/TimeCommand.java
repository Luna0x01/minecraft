package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class TimeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("time")
							.requires(source -> source.hasPermissionLevel(2)))
						.then(
							((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("set")
												.then(CommandManager.literal("day").executes(context -> executeSet((ServerCommandSource)context.getSource(), 1000))))
											.then(CommandManager.literal("noon").executes(context -> executeSet((ServerCommandSource)context.getSource(), 6000))))
										.then(CommandManager.literal("night").executes(context -> executeSet((ServerCommandSource)context.getSource(), 13000))))
									.then(CommandManager.literal("midnight").executes(context -> executeSet((ServerCommandSource)context.getSource(), 18000))))
								.then(
									CommandManager.argument("time", TimeArgumentType.time())
										.executes(context -> executeSet((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "time")))
								)
						))
					.then(
						CommandManager.literal("add")
							.then(
								CommandManager.argument("time", TimeArgumentType.time())
									.executes(context -> executeAdd((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "time")))
							)
					))
				.then(
					((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("query")
								.then(
									CommandManager.literal("daytime")
										.executes(context -> executeQuery((ServerCommandSource)context.getSource(), getDayTime(((ServerCommandSource)context.getSource()).getWorld())))
								))
							.then(
								CommandManager.literal("gametime")
									.executes(
										context -> executeQuery(
												(ServerCommandSource)context.getSource(), (int)(((ServerCommandSource)context.getSource()).getWorld().getTime() % 2147483647L)
											)
									)
							))
						.then(
							CommandManager.literal("day")
								.executes(
									context -> executeQuery(
											(ServerCommandSource)context.getSource(), (int)(((ServerCommandSource)context.getSource()).getWorld().getTimeOfDay() / 24000L % 2147483647L)
										)
								)
						)
				)
		);
	}

	private static int getDayTime(ServerWorld world) {
		return (int)(world.getTimeOfDay() % 24000L);
	}

	private static int executeQuery(ServerCommandSource source, int time) {
		source.sendFeedback(new TranslatableText("commands.time.query", time), false);
		return time;
	}

	public static int executeSet(ServerCommandSource source, int time) {
		for (ServerWorld serverWorld : source.getServer().getWorlds()) {
			serverWorld.setTimeOfDay((long)time);
		}

		source.sendFeedback(new TranslatableText("commands.time.set", time), true);
		return getDayTime(source.getWorld());
	}

	public static int executeAdd(ServerCommandSource source, int time) {
		for (ServerWorld serverWorld : source.getServer().getWorlds()) {
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)time);
		}

		int i = getDayTime(source.getWorld());
		source.sendFeedback(new TranslatableText("commands.time.set", i), true);
		return i;
	}
}
