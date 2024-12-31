package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class TimeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("time")
							.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
						.then(
							((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("set")
												.then(CommandManager.literal("day").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 1000))))
											.then(CommandManager.literal("noon").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 6000))))
										.then(CommandManager.literal("night").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 13000))))
									.then(CommandManager.literal("midnight").executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), 18000))))
								.then(
									CommandManager.argument("time", TimeArgumentType.time())
										.executes(commandContext -> executeSet((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
								)
						))
					.then(
						CommandManager.literal("add")
							.then(
								CommandManager.argument("time", TimeArgumentType.time())
									.executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
							)
					))
				.then(
					((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("query")
								.then(
									CommandManager.literal("daytime")
										.executes(
											commandContext -> executeQuery(
													(ServerCommandSource)commandContext.getSource(), getDayTime(((ServerCommandSource)commandContext.getSource()).getWorld())
												)
										)
								))
							.then(
								CommandManager.literal("gametime")
									.executes(
										commandContext -> executeQuery(
												(ServerCommandSource)commandContext.getSource(), (int)(((ServerCommandSource)commandContext.getSource()).getWorld().getTime() % 2147483647L)
											)
									)
							))
						.then(
							CommandManager.literal("day")
								.executes(
									commandContext -> executeQuery(
											(ServerCommandSource)commandContext.getSource(),
											(int)(((ServerCommandSource)commandContext.getSource()).getWorld().getTimeOfDay() / 24000L % 2147483647L)
										)
								)
						)
				)
		);
	}

	private static int getDayTime(ServerWorld serverWorld) {
		return (int)(serverWorld.getTimeOfDay() % 24000L);
	}

	private static int executeQuery(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.sendFeedback(new TranslatableText("commands.time.query", i), false);
		return i;
	}

	public static int executeSet(ServerCommandSource serverCommandSource, int i) {
		for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
			serverWorld.setTimeOfDay((long)i);
		}

		serverCommandSource.sendFeedback(new TranslatableText("commands.time.set", i), true);
		return getDayTime(serverCommandSource.getWorld());
	}

	public static int executeAdd(ServerCommandSource serverCommandSource, int i) {
		for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)i);
		}

		int j = getDayTime(serverCommandSource.getWorld());
		serverCommandSource.sendFeedback(new TranslatableText("commands.time.set", j), true);
		return j;
	}
}
