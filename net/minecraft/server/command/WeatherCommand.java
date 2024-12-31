package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("weather")
							.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
						.then(
							((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), 6000)))
								.then(
									CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
										.executes(
											commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)
										)
								)
						))
					.then(
						((LiteralArgumentBuilder)CommandManager.literal("rain").executes(commandContext -> executeRain((ServerCommandSource)commandContext.getSource(), 6000)))
							.then(
								CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
									.executes(
										commandContext -> executeRain((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)
									)
							)
					))
				.then(
					((LiteralArgumentBuilder)CommandManager.literal("thunder")
							.executes(commandContext -> executeThunder((ServerCommandSource)commandContext.getSource(), 6000)))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(
									commandContext -> executeThunder((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)
								)
						)
				)
		);
	}

	private static int executeClear(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().getLevelProperties().setClearWeatherTime(i);
		serverCommandSource.getWorld().getLevelProperties().setRainTime(0);
		serverCommandSource.getWorld().getLevelProperties().setThunderTime(0);
		serverCommandSource.getWorld().getLevelProperties().setRaining(false);
		serverCommandSource.getWorld().getLevelProperties().setThundering(false);
		serverCommandSource.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
		return i;
	}

	private static int executeRain(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().getLevelProperties().setClearWeatherTime(0);
		serverCommandSource.getWorld().getLevelProperties().setRainTime(i);
		serverCommandSource.getWorld().getLevelProperties().setThunderTime(i);
		serverCommandSource.getWorld().getLevelProperties().setRaining(true);
		serverCommandSource.getWorld().getLevelProperties().setThundering(false);
		serverCommandSource.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
		return i;
	}

	private static int executeThunder(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().getLevelProperties().setClearWeatherTime(0);
		serverCommandSource.getWorld().getLevelProperties().setRainTime(i);
		serverCommandSource.getWorld().getLevelProperties().setThunderTime(i);
		serverCommandSource.getWorld().getLevelProperties().setRaining(true);
		serverCommandSource.getWorld().getLevelProperties().setThundering(true);
		serverCommandSource.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
		return i;
	}
}
